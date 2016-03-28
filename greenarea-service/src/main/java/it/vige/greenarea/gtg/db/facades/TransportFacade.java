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
package it.vige.greenarea.gtg.db.facades;

import static it.vige.greenarea.Utilities.createMockShippingId;
import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static it.vige.greenarea.dto.TipoRichiesta.RITIRO;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.FreightItemState;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.cl.library.entities.Transport.TransportState;
import it.vige.greenarea.cl.library.entities.TransportServiceClass;
import it.vige.greenarea.cl.library.entities.Transport_;
import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.TipiRichiesta;
import it.vige.greenarea.dto.TipoRichiesta;
import it.vige.greenarea.itseasy.lib.configurationData.MqConstants;
import it.vige.greenarea.itseasy.lib.configurationData.SGAPLconstants;
import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyProducer;
import it.vige.greenarea.itseasy.lib.mqData.MqFreightData;
import it.vige.greenarea.itseasy.lib.mqData.MqShippingData;
import it.vige.greenarea.sgapl.sgot.facade.ShippingOrderFacade;
import it.vige.greenarea.utilities.Application;

@Stateless
public class TransportFacade extends AbstractFacade<Transport, String> {

	private Logger logger = getLogger(getClass());

	@Resource(mappedName = "java:/jms/topic/GatTopic")
	private Topic gatTopic;

	@Inject
	private JMSContext jmsContext;

	@EJB
	private FreightFacade freightFacade;

	@EJB
	private ShippingOrderFacade shippingOrderFacade;

	@EJB
	private TransportServiceClassFacade transportServiceClassFacade;

	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public TransportFacade() {
		super(Transport.class);
	}

	@Override
	public void create(Transport t) {
		if (t.getAlfacode() == null) {
			throw new PersistenceException("Transport ID alfacode cannot be null");
		}
		// calcolo il total volume:
		t.setTotalVolume(0);
		for (Freight f : t.getFreightItems()) {
			t.setTotalVolume(t.getTotalVolume() + f.getVolume());
		}
		super.create(t);
	}

	@Override
	public void edit(Transport t) {
		// ricalcolo il total volume:
		t.setTotalVolume(0);
		List<Freight> freights = new ArrayList<Freight>();
		try {
			freights = t.getFreightItems();
			if (freights.size() > 0)
				freights = t.getFreightItems();
		} catch (Exception ex) {
			freights = freightFacade.findAll(t);
		}
		for (Freight f : freights) {
			t.setTotalVolume(t.getTotalVolume() + f.getVolume());
		}
		super.edit(t);
	}

	public List<Transport> findAll(Richiesta richiesta) {
		boolean where = false;
		String tipo = richiesta.getTipo();
		TipoRichiesta tipoRichiesta = null;
		String ga = richiesta.getRoundCode();
		Date dataInizio = richiesta.getOrarioInizio();
		Date dataFine = richiesta.getOrarioFine();
		if (tipo != null) {
			TipiRichiesta tipiRichiesta = TipiRichiesta.valueOf(richiesta.getTipo());
			switch (tipiRichiesta) {
			case CONSEGNE:
				tipoRichiesta = CONSEGNA;
				break;
			case RITIRI:
				tipoRichiesta = RITIRO;
				break;
			case ENTRAMBI:
				break;
			}
		}
		String queryString = "select t from Transport t";
		if (dataInizio != null) {
			if (!where) {
				queryString += " where";
				where = true;
				queryString += " t.dateMiss >= :timeAccept";
			} else
				queryString += " and t.dateMiss >= :timeAccept";
		}
		if (dataFine != null) {
			if (!where) {
				queryString += " where";
				where = true;
				queryString += " t.dateMiss <= :timeClosing";
			} else
				queryString += " and t.dateMiss <= :timeClosing";
		}
		if (tipoRichiesta != null) {
			if (!where) {
				queryString += " where";
				where = true;
				queryString += " t.tipo = :tipo";
			} else
				queryString += " and t.tipo = :tipo";
		}
		if (ga != null && !ga.trim().equals("")) {
			if (!where) {
				queryString += " where";
				where = true;
				queryString += " t.timeSlot.roundCode = :roundCode";
			} else
				queryString += " and t.timeSlot.roundCode = :roundCode";
		}
		queryString += " order by t.dateMiss";

		Query q = this.getEntityManager().createQuery(queryString);
		if (dataInizio != null)
			q.setParameter("timeAccept", richiesta.getOrarioInizio());
		if (dataFine != null)
			q.setParameter("timeClosing", richiesta.getOrarioFine());
		if (ga != null && !ga.trim().equals(""))
			q.setParameter("roundCode", richiesta.getRoundCode());
		if (tipoRichiesta != null) {
			q.setParameter("tipo", tipoRichiesta);
		}
		return q.getResultList();
	}

	public List<Transport> findSySelection(Transport.TransportState state, String serviceClass) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Transport> cq = cb.createQuery(Transport.class);
		Root<Transport> trRoot = cq.from(Transport.class);
		List<TransportServiceClass> transportClassList = transportServiceClassFacade.findBySelection(serviceClass);

		if ((transportClassList == null) || (transportClassList.isEmpty())) {
			return null;
		}
		logger.debug(String.format("->Found %d classes for \"%s\" service class\n", transportClassList.size(),
				serviceClass));
		Path<TransportServiceClass> tsc = trRoot.get(Transport_.serviceClass);
		Path<TransportState> tsc2 = trRoot.get(Transport_.transportState);
		Predicate ptsc = cb.equal(tsc, transportClassList.get(0));

		Predicate wherePredicate = null;
		if (state == null) {
			wherePredicate = ptsc;
		} else {
			Predicate ptsc2 = cb.equal(tsc2, state);
			wherePredicate = cb.and(ptsc, ptsc2);
		}
		cq = cq.select(trRoot).where(wherePredicate);
		return getEntityManager().createQuery(cq).getResultList();
	}

	@Override
	public String getId(Transport entity) {
		return entity.getAlfacode();
	}

	@Override
	public void setId(Transport entity, String id) {
		entity.setAlfacode(id);
	}

	public void notifyLoaded(Transport t) {
		// cerco il trasporto:

		if (t.getTransportState().equals(Transport.TransportState.on_delivery)) {
			return; // era gia' in stato delivered
		}
		t.setTransportState(Transport.TransportState.on_delivery);
		edit(t);
		// se lo stato del trasporto cambia lo devo segnalare a SGOT
		Date now = new Date();
		// mando un messaggio a GAT su MQ
		Map<String, String> properties = new HashMap<String, String>();
		// aggiungo alcune proprieta'
		// TIMESTAMP
		properties.put(MqConstants.MQ_KEY_TIMESTAMP, now.toString());
		// TransportID
		properties.put(MqConstants.MQ_KEY_TRANSPORT_ID, t.getAlfacode());

		properties.put(MqConstants.MQ_KEY_VECTOR_NAME, Application.getProperty("TransportCarrier.name"));

		// CAUSE
		properties.put(MqConstants.MQ_KEY_CAUSE, SGAPLconstants.ON_DELIVERY_STATUS);

		ItseasyProducer producer = new ItseasyProducer();
		JMSProducer jmsProducer = jmsContext.createProducer();
		producer.publishTextMessage(gatTopic, jmsProducer, "Caricato", properties);

	}

	public void notifyDelivery(Transport t) {
		// verifico che tutti i freight siano stati consegnati
		for (Freight fr : t.getFreightItems()) {
			if (!fr.getFreightState().equals(FreightItemState.DELIVERED)) {
				return;
			}
		}
		// Se arrivo qui vuol dire che tutti i freight sono tutti DELIVERED o
		// NOT LOADED
		t.setTransportState(Transport.TransportState.completed);
		edit(t);
		Date now = new Date();
		// mando un messaggio a GAT su MQ
		Map<String, String> properties = new HashMap<String, String>();
		// aggiungo alcune proprieta'
		// TIMESTAMP
		properties.put(MqConstants.MQ_KEY_TIMESTAMP, now.toString());
		// TransportID
		properties.put(MqConstants.MQ_KEY_TRANSPORT_ID, t.getAlfacode());

		properties.put(MqConstants.MQ_KEY_VECTOR_NAME, Application.getProperty("TransportCarrier.name"));

		// CAUSE
		properties.put(MqConstants.MQ_KEY_CAUSE, SGAPLconstants.DONE_STATUS);

		ItseasyProducer producer = new ItseasyProducer();
		JMSProducer jmsProducer = jmsContext.createProducer();
		producer.publishTextMessage(gatTopic, jmsProducer, "Consegnato", properties);
	}

	// questo metodo riceve uno shippingOrderData e crea tutte le entity
	// necessarie

	public void loadOrder(MqShippingData sod) {

		Transport tr = new Transport(createMockShippingId());
		tr.setSource(new GeoLocation(sod.getSourceLocation()));
		tr.setPickup(new DBGeoLocation(sod.getPickup()));
		tr.setDestination(new GeoLocation(sod.getDestinationLocation()));
		tr.setDropdown(new DBGeoLocation(sod.getDropdown()));
		tr.setTransportState(Transport.TransportState.waiting);

		String trCl = sod.getServiceClass();
		if ((trCl == null) || (trCl.isEmpty())) {
			// se non c'e' una classe devo mettere un default:
			trCl = "FURGONATO";
		}
		List<TransportServiceClass> trClassLIst = transportServiceClassFacade.findBySelection(trCl);
		TransportServiceClass trClassEntity;
		if ((trClassLIst == null) || (trClassLIst.isEmpty())) {
			// se la classe non c'e' la creo
			trClassEntity = new TransportServiceClass();
			trClassEntity.setDescription(trCl);
			transportServiceClassFacade.create(trClassEntity);
		}
		tr.setServiceClass(transportServiceClassFacade.findBySelection(trCl).get(0));
		tr.setShippingOrder(shippingOrderFacade.find(sod.getShId()));

		create(tr);

		for (MqFreightData sid : sod.getShippingItems()) {
			Freight f = freightFacade.create(sid);
			f.setFreightState(FreightItemState.AVAILABLE);
			// quando supportato da SGOT lo stato viene inviato in SOD
			f.setTransport(tr);
			freightFacade.edit(f);
			tr.getFreightItems().add(f);
		}
		edit(tr);
	}

	public List<Transport> listByExample(Transport exampleInstance, String orderCol, boolean asc) {
		StringBuilder queryString = new StringBuilder("select t from Transport t ");
		queryString.append(" where t.id=t.id ");
		ArrayList<Object> values = new ArrayList<Object>();
		int params = 1;
		if (exampleInstance != null) {
			if (exampleInstance.getAlfacode() != null) {
				queryString.append(" and s.alfacode like ?").append(params++);
				values.add(exampleInstance.getAlfacode());
			}
			if (exampleInstance.getShippingOrder() != null) {
				queryString.append(" and t.shippingOrder.id = ?").append(params++);
				values.add(exampleInstance.getShippingOrder().getId());
			}
			if (exampleInstance.getTransportState() != null) {
				queryString.append(" and t.transportState = ?").append(params++);
				values.add(exampleInstance.getTransportState());
			}
			if ((orderCol) != null) {
				queryString.append(" order by t.").append(orderCol);
				queryString.append(asc ? " asc" : " desc");
			} else {
				queryString.append(" order by t.id "); // default field
				queryString.append(asc ? " asc" : " desc");
			}
		}
		Query q = this.getEntityManager().createQuery(queryString.toString());
		params = 1;
		for (Object val : values) {
			q.setParameter(params++, val);
		}
		return q.getResultList();
	}

}
