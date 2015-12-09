/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.greenarea.sgapl.sgot.business;

import static it.vige.greenarea.cl.library.entities.OrderStatus.StateValue.ongoing;
import static it.vige.greenarea.cl.library.entities.OrderStatus.StateValue.returning;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.assigned;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.completed;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.on_delivery;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.rejected;
import static it.vige.greenarea.gtg.constants.ConversioniGTG.fromSgotToMQ;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_CAUSE;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_SHIPPING_ID;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_TIMESTAMP;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_TRANSPORT_ID;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_VECTOR_NAME;
import static it.vige.greenarea.itseasy.lib.configurationData.SGAPLconstants.READY_STATUS;
import static it.vige.greenarea.sgapl.sgot.business.exception.GATException.GATerrorCodes.UNKNOWN_TRANSPORT_ID;
import static it.vige.greenarea.sgapl.sgot.business.exception.GATException.GATerrorCodes.UNSOPPORTED_OPERATION;
import static it.vige.greenarea.sgapl.sgot.mqHandlers.SGOTmessageHandler.getSGOTmessagHandler;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Topic;

import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.Carrier;
import it.vige.greenarea.cl.library.entities.OrderStatus;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.dto.Leg;
import it.vige.greenarea.gtg.db.facades.TransportFacade;
import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyConsumer;
import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyProducer;
import it.vige.greenarea.itseasy.lib.mqData.MqShippingData;
import it.vige.greenarea.sgapl.sgot.business.exception.GATException;
import it.vige.greenarea.sgapl.sgot.facade.CarrierFacade;
import it.vige.greenarea.sgapl.sgot.facade.ShippingOrderFacade;

@Stateless
public class CarrierManagerBean {

	private Logger logger = getLogger(getClass());

	@Resource(mappedName = "java:/jms/topic/VectorTopic")
	private Topic vectorTopic;

	@Resource(mappedName = "java:/jms/topic/GatTopic")
	private Topic gatTopic;

	@Inject
	private JMSContext jmsContext;

	@EJB
	private CarrierFacade carrierFacade;

	@EJB
	private ShippingOrderFacade shippingOrderFacade;
	@EJB
	private TransportFacade transportFacade;

	public CarrierManagerBean() {
	}

	public void delivery(Transport t) throws GATException {
		// prendo la leg corrente del trasporto
		Leg currentLeg = t.getRoute().get(t.getActiveLegIndex());
		Carrier currentVector = null;
		try {
			List<Carrier> carriers = carrierFacade.findAll();
			for (Carrier carrier : carriers)
				if (carrier.getName().equals(currentLeg.getVector()))
					currentVector = carrier;
		} catch (Exception ex) {
			logger.error("delivery", ex);
			throw new GATException(UNSOPPORTED_OPERATION, null);

		}
		MqShippingData sod = fromSgotToMQ(t.getShippingOrder());

		// A questo punto dovrei comunicare al vector che l'ordine e' pronto per
		// essere preso in carico
		// devo usare le info di vector per capire come comunicare.
		Date now = new Date();
		Map<String, String> properties = new HashMap<String, String>();
		// aggiungo alcune proprieta'
		// Timestamp
		properties.put(MQ_KEY_TIMESTAMP, now.toString());

		// TransportID
		properties.put(MQ_KEY_TRANSPORT_ID, t.getAlfacode());
		// ShippingOrderID
		properties.put(MQ_KEY_SHIPPING_ID, t.getShippingOrder().getId().toString());

		properties.put(MQ_KEY_VECTOR_NAME, t.getRoute().get(t.getActiveLegIndex()).getVector());

		// CAUSE
		properties.put(MQ_KEY_CAUSE, READY_STATUS);

		// aggiungo eventuali property del vector (se l'ho trovato)
		if (currentVector != null) {
			properties.putAll(currentVector.getProperties());
		} else {
			// TODO va gestito il caso in cui non trovo il vettore....
		}
		logger.debug("l'ordine " + t.getShippingOrder().getId() + "del trasporto " + t.getAlfacode() + " e' pronto !");
		logger.debug("Lo invio a " + t.getRoute().get(t.getActiveLegIndex()).getVector());

		// Invio il messaggio a Vector
		ItseasyProducer producer = new ItseasyProducer();
		JMSProducer jmsProducer = jmsContext.createProducer();

		producer.publishObjectMessage(vectorTopic, jmsProducer, sod, properties);
		ItseasyConsumer consumer = getSGOTmessagHandler().getConsumer(t.getAlfacode());
		getSGOTmessagHandler().putConsumer(t.getAlfacode(), consumer);
	}

	public String onDelivery(String transportID) throws GATException {
		// verifico che tID esista:
		Transport t = transportFacade.find(transportID);
		if (t == null) {
			ArrayList<String> param = new ArrayList<String>();
			param.add(transportID.toString());
			throw new GATException(UNKNOWN_TRANSPORT_ID, param);
		}
		// se e' il primo vector segnalo anche a SGO che e' partito:
		if (t.getActiveLegIndex() == 0) {
			// e' appena partito
			// Segnalo partenza a SGO
			ShippingOrder so = t.getShippingOrder();
			so.setOrderStatus(ongoing);
			shippingOrderFacade.edit(so);
		}
		// metto il trasporto in viaggio
		t.setTransportState(on_delivery);
		transportFacade.edit(t);
		return "in viaggio";
	}

	public String doneDelivery(String transportID) throws GATException {
		// verifico che tID esista:
		Transport t = transportFacade.find(transportID);
		if (t == null) {
			ArrayList<String> param = new ArrayList<String>();
			param.add(transportID.toString());
			throw new GATException(UNKNOWN_TRANSPORT_ID, param);
		}
		getSGOTmessagHandler().releaseConsumer(t.getAlfacode());
		// verifico se si tratta dell'ultima leg:
		int index = t.getActiveLegIndex() + 1;
		if (index == t.getRoute().size()) {
			// ho finito
			t.setTransportState(completed);
			transportFacade.edit(t);
			// Segnalo avvenuta consegna a SGO
			ShippingOrder so = t.getShippingOrder();
			so.setOrderStatus(OrderStatus.StateValue.completed);
			shippingOrderFacade.edit(so);
			return "Consegnato";
		} else {
			t.setActiveLegIndex(index);
			t.setTransportState(assigned);
			transportFacade.edit(t);
			delivery(t);
			return "in viaggio";
		}
	}

	public String rejectDelivery(String transportID) throws GATException {
		// verifico che tID esista:
		Transport t = transportFacade.find(transportID);
		if (t == null) {
			ArrayList<String> param = new ArrayList<String>();
			param.add(transportID.toString());
			throw new GATException(UNKNOWN_TRANSPORT_ID, param);
		}
		t.setTransportState(rejected);
		transportFacade.edit(t);
		// Segnalo rifiuto a SGO
		ShippingOrder so = t.getShippingOrder();
		so.setOrderStatus(returning);
		shippingOrderFacade.edit(so);
		return "Rifiutato";
	}
	// Add business logic below. (Right-click in editor and choose
	// "Insert Code > Add Business Method")
}
