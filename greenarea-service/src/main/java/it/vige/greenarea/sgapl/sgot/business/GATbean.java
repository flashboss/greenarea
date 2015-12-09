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

import static it.vige.greenarea.Conversioni.convertiGeoLocationInterfaceToGeoLocation;
import static it.vige.greenarea.Conversioni.convertiShippingItemToFreight;
import static it.vige.greenarea.GTGsystem.olivetti;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.assigned;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.waiting;
import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static it.vige.greenarea.dto.TipoRichiesta.RITIRO;
import static it.vige.greenarea.gtg.constants.ConversioniGTG.fromSgotToSgrl;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_TIMESTAMP;
import static it.vige.greenarea.sgapl.sgot.business.exception.GATException.GATerrorCodes.UNKNOWN_TRANSPORT_ID;
import static java.lang.String.valueOf;
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
import javax.jms.Queue;
import javax.xml.ws.WebServiceRef;

import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.ShippingItem;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.gtg.db.facades.FreightFacade;
import it.vige.greenarea.gtg.db.facades.TransportFacade;
import it.vige.greenarea.gtg.db.facades.TransportServiceClassFacade;
import it.vige.greenarea.itseasy.lib.logger.ItseasyLogger;
import it.vige.greenarea.sgapl.sgot.business.exception.GATException;
import it.vige.greenarea.sgapl.sgot.facade.ShippingOrderFacade;
import it.vige.greenarea.sgrl.webservices.GeoLocation;
import it.vige.greenarea.sgrl.webservices.LogisticNetworkRouting_Service;
import it.vige.greenarea.sgrl.webservices.SGRLServiceException_Exception;
import it.vige.greenarea.sgrl.webservices.SgrlRoute;

@Stateless
public class GATbean {

	private Logger logger = getLogger(getClass());

	private ItseasyLogger itseasyLoggerUtil = new ItseasyLogger();

	@Resource(mappedName = "java:/jms/queue/ItseasyLogger")
	private Queue itseasyLogger;

	@Inject
	private JMSContext jmsContext;

	@WebServiceRef(wsdlLocation = "http://localhost:8080/greenarea-sgr/LogisticNetworkRouting?wsdl")
	private LogisticNetworkRouting_Service service;
	/*
	 * @WebServiceRef(wsdlLocation =
	 * "WEB-INF/wsdl/163.162.24.76_47453/SGRLserver/LogisticNetworkRouting.wsdl"
	 * ) private LogisticNetworkRouting_Service service;
	 */

	@EJB
	private ShippingOrderFacade shippingOrderFacade;
	@EJB
	private CarrierManagerBean carrierManagerBean;
	@EJB
	private TransportFacade transportFacade;
	@EJB
	private FreightFacade freightFacade;
	@EJB
	private TransportServiceClassFacade transportServiceClassFacade;

	public String startShipment(ShippingOrder so) throws GATException {
		// verifico che tID esista:
		Transport t = so.getTransport();
		if (t == null) {
			ArrayList<String> param = new ArrayList<String>();
			param.add(so.getId().toString());
			throw new GATException(GATException.GATerrorCodes.UNKNOWN_TRANSPORT_ID, param);
		}
		t.setTransportState(assigned);
		transportFacade.edit(t);
		carrierManagerBean.delivery(t);
		return t.getShippingOrder().getId();
	}

	public String getTransport(ShippingOrder so) throws GATException {
		Transport t = new Transport(so.getId());
		t.setShippingOrder(so);
		t.setRoundCode(so.getRoundCode());
		t.setCodiceFiliale(so.getCodiceFiliale());
		t.setActiveLegIndex(0);
		t.setAttributes(new HashMap<String, String>());
		t.setDateMiss(so.getCreationTimestamp());
		if (so.getMittente().toString().equals(olivetti.toString()))
			t.setTipo(CONSEGNA);
		else
			t.setTipo(RITIRO);
		t.setSource(convertiGeoLocationInterfaceToGeoLocation(so.getMittente()));
		t.setPickup(so.getMittente());
		t.setDestination(convertiGeoLocationInterfaceToGeoLocation(so.getDestinatario()));
		t.setDropdown(so.getDestinatario());
		t.setServiceClass(transportServiceClassFacade.findBySelection("FURGONATO").get(0));
		t.setTransportState(waiting);
		t.setOperatoreLogistico(so.getOperatoreLogistico());
		t.setDateMiss(t.getDateMiss());
		StringBuilder sb = new StringBuilder("http://");
		sb.append("www.multitaly.it/SGAPLweb");
		sb.append("/tracking?shippingID=");
		sb.append(so.getId());

		for (ShippingItem si : so.getShippingItems()) {
			Freight tr1fi1 = convertiShippingItemToFreight(si);
			tr1fi1.setTransport(t);
			freightFacade.create(tr1fi1);
			t.getFreightItems().add(tr1fi1);
		}

		transportFacade.create(t);
		so.setTransport(t);
		so.setTrackingURL(sb.toString());
		shippingOrderFacade.edit(so);
		return valueOf(t.getCost());
	}

	public String estimateTransportCost(DBGeoLocation source, DBGeoLocation destination,
			HashMap<String, String> attributes) throws GATException {
		ArrayList<String> errorParams = new ArrayList<String>();
		errorParams.add(source.toString());
		errorParams.add(destination.toString());
		List<SgrlRoute> routeList = null;
		GeoLocation sourceSgrl = fromSgotToSgrl(source);
		GeoLocation destinationSgrl = fromSgotToSgrl(destination);
		try {
			// TODO da cambiare per urare solo la getSGRLRoutes
			routeList = getSGRLRoutes(sourceSgrl, destinationSgrl, "");
		} catch (SGRLServiceException_Exception ex) {
			JMSProducer jmsProducer = jmsContext.createProducer();
			Date now = new Date();
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(MQ_KEY_TIMESTAMP, now.toString());
			itseasyLoggerUtil.logMessage("GATbean", "ERROR", properties, "*** ERROR: " + ex.getMessage(), itseasyLogger,
					jmsProducer);
			throw new GATException(GATException.GATerrorCodes.CANNOT_ESTIMATE, errorParams);
		}
		if ((routeList == null) || (routeList.isEmpty())) {
			throw new GATException(GATException.GATerrorCodes.CANNOT_ESTIMATE, errorParams);
		}
		return valueOf(routeList.get(0).getCost());
	}

	public String getTransportCost(Long tID) throws GATException {
		Transport t = transportFacade.find(tID);
		if (t == null) {
			ArrayList<String> param = new ArrayList<String>();
			param.add(tID.toString());
			throw new GATException(GATException.GATerrorCodes.UNKNOWN_TRANSPORT_ID, param);
		}
		return valueOf(t.getCost());
	}

	public void dropTransport(ShippingOrder so) throws GATException {
		// verifico che tID esista:
		Transport t = so.getTransport();
		if (t == null) {
			ArrayList<String> param = new ArrayList<String>();
			param.add(so.getId().toString());
			throw new GATException(GATException.GATerrorCodes.UNKNOWN_TRANSPORT_ID, param);
		}
		// Rimuovo il trasporto: dovrei verificare che non sia partito ma non e'
		// possibile
		// perche' posso fare drop solo se non ho ricevuto confirmShipment)

		transportFacade.remove(t);

	}

	public String getTrackingURL(Long tID) throws GATException {
		Transport t = transportFacade.find(tID);
		if (t == null) {
			ArrayList<String> param = new ArrayList<String>();
			param.add(tID.toString());
			throw new GATException(UNKNOWN_TRANSPORT_ID, param);
		}
		return "";// todo BISOGNA CREARLA.... t.getTrackingURL();
	}

	private String geoLocationToString(it.vige.greenarea.sgrl.webservices.GeoLocation geoLoc) {
		StringBuilder sb = new StringBuilder();
		sb.append(geoLoc.getStreet()).append(", ").append(geoLoc.getNumber());
		sb.append(" - ").append(geoLoc.getZipCode());
		sb.append(" - ").append(geoLoc.getCity());
		sb.append(" (").append(geoLoc.getAdminAreaLevel1()).append(")");
		sb.append(" ").append(geoLoc.getAdminAreaLevel2()).append(";");
		sb.append(" ").append(geoLoc.getCountry()).append(";");
		return sb.toString();
	}

	private java.util.List<it.vige.greenarea.sgrl.webservices.SgrlRoute> getSGRLRoutes(
			it.vige.greenarea.sgrl.webservices.GeoLocation source,
			it.vige.greenarea.sgrl.webservices.GeoLocation destination, java.lang.String options)
					throws SGRLServiceException_Exception {

		Date now = new Date();
		Map<String, String> properties = new HashMap<String, String>();
		// aggiungo alcune proprieta'
		// Timestamp
		properties.put(MQ_KEY_TIMESTAMP, now.toString());
		StringBuilder sb = new StringBuilder();
		sb.append("Invio richiesta getSGRLRoutes da ");
		sb.append(geoLocationToString(source));
		sb.append(" a ").append(geoLocationToString(destination));
		JMSProducer jmsProducer = jmsContext.createProducer();
		itseasyLoggerUtil.logMessage("GATbean", "INFO", properties, sb.toString(), itseasyLogger, jmsProducer);
		it.vige.greenarea.sgrl.webservices.LogisticNetworkRouting port = service.getLogisticNetworkRoutingPort();
		List<SgrlRoute> route;

		logger.debug("invio getSGRLRoutes da:" + source.getCity() + " --> " + destination.getCity());

		route = port.getSGRLRoutes(source, destination, options);

		return route;
	}
}
