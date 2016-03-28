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
package it.vige.greenarea.sgaplconsole.controllers;

import static it.vige.greenarea.Constants.ITALY;
import static it.vige.greenarea.sgapl.sgot.webservice.ResultStatus.NOK;
import static java.lang.String.valueOf;
import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import static javax.faces.application.FacesMessage.SEVERITY_WARN;
import static org.slf4j.LoggerFactory.getLogger;

//import it.vige.greenarea.sgaplconsole.data.TerminiDiConsegna;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Topic;
import javax.xml.ws.WebServiceRef;

import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.itseasy.lib.configurationData.MqConstants;
import it.vige.greenarea.itseasy.lib.configurationData.SGAPLconstants;
import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyProducer;
import it.vige.greenarea.sgapl.sgot.webservice.GATException_Exception;
import it.vige.greenarea.sgapl.sgot.webservice.RequestShippingResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.ResultOperationResponse;
import it.vige.greenarea.sgapl.sgot.webservice.SGOTadminService;
import it.vige.greenarea.sgapl.sgot.webservice.SGOTadminService_Service;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingItemData;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderData;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderData.TerminiDiConsegna;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderDetails;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager_Service;
import it.vige.greenarea.sgapl.sgot.webservice.TransportInfo;
import it.vige.greenarea.sgaplconsole.controllers.utils.Converters;
import it.vige.greenarea.sgaplconsole.data.MyOrder;
import it.vige.greenarea.sgaplconsole.data.MyTransport;
import it.vige.greenarea.sgrl.webservices.ObjectFactory;

@ManagedBean(name = "sgaplConsoleController")
@SessionScoped
public class SGAPLconsoleController implements Serializable, MqConstants, SGAPLconstants {

	private Logger logger = getLogger(getClass());

	@Resource(mappedName = "java:/jms/topic/GatTopic")
	private Topic gatTopic;

	@Inject
	private JMSContext jmsContext;

	private static final long serialVersionUID = -1846771184656120543L;
	@WebServiceRef(wsdlLocation = "http://localhost:8080/greenarea-service/ShippingOrderManager?wsdl")
	// @WebServiceRef(wsdlLocation =
	// "WEB-INF/wsdl/163.162.24.76/SGOTserver/ShippingOrderManager.wsdl")
	private ShippingOrderManager_Service sgotService;
	@WebServiceRef(wsdlLocation = "http://localhost:8080/greenarea-service/SGOTadminService?wsdl")
	// @WebServiceRef(wsdlLocation =
	// "WEB-INF/wsdl/163.162.24.76/SGOTserver/SGOTadminService.wsdl")
	private SGOTadminService_Service adminService;
	ObjectFactory sgotObj = new ObjectFactory();
	List<MyOrder> orderList = new ArrayList<MyOrder>();
	List<MyTransport> transportList = new ArrayList<MyTransport>();

	public FacesMessage loadOrders() {
		orderList = new ArrayList<MyOrder>();
		transportList = new ArrayList<MyTransport>();
		List<ShippingOrderDetails> sodList = loadOrderIDS();
		int count = sodList.size();
		if ((sodList != null) && (!sodList.isEmpty())) {
			for (ShippingOrderDetails sd : sodList) {
				if ((sd.getStato().equals("completed")) || (sd.getStato().equals("returning"))) {
					// non carico gli ordini gia' completati
					continue;
				}
				MyOrder currentOrder = new MyOrder(sd);

				TransportInfo trInfo;
				try {
					trInfo = getTransportInfo(sd.getTransportID());
				} catch (GATException_Exception ex) {
					logger.error("errore gat", ex);
					continue; // se ho errore non carico nulla
				}
				if (trInfo != null) {
					MyTransport currentTransport = new MyTransport(trInfo);

					transportList.add(currentTransport);
				}
				orderList.add(currentOrder);
			}
		}
		FacesMessage msg = new FacesMessage(SEVERITY_INFO, "Ordini caricati: ", valueOf(count));

		return msg;
	}

	public FacesMessage estimateOrder(MyOrder currentOrder) {
		// preparo ShippingOrderData per la request:
		ShippingOrderData shData = new ShippingOrderData();
		shData.setShipmentId(currentOrder.getId());
		shData.setTerminiDiConsegna(new ShippingOrderData.TerminiDiConsegna());
		for (TerminiDiConsegna.Entry entry : currentOrder.getOrderData().getTerminiDiConsegna().getEntry()) {
			ShippingOrderData.TerminiDiConsegna.Entry shEntry = new ShippingOrderData.TerminiDiConsegna.Entry();
			shEntry.setKey(entry.getKey());
			shEntry.setValue(entry.getValue());
			shData.getTerminiDiConsegna().getEntry().add(shEntry);
		}
		for (ShippingItemData sid : currentOrder.getOrderData().getShippingItems()) {

			shData.getShippingItems().add(sid);
		}
		RequestShippingResponseData res;
		// ripulisco i campi per estimate
		DBGeoLocation estiamteFrom = new DBGeoLocation();
		DBGeoLocation estiamteTo = new DBGeoLocation();
		estiamteFrom.setCountry(ITALY);
		estiamteFrom.setAdminAreaLevel2(currentOrder.getFrom().getAdminAreaLevel2());
		estiamteFrom.setCity(currentOrder.getFrom().getCity());
		estiamteFrom.setZipCode(currentOrder.getFrom().getZipCode());
		estiamteTo.setCountry(ITALY);
		estiamteTo.setAdminAreaLevel2(currentOrder.getTo().getAdminAreaLevel2());
		estiamteTo.setCity(currentOrder.getTo().getCity());
		estiamteTo.setZipCode(currentOrder.getTo().getZipCode());
		res = estimateShipping(currentOrder.getFrom().getName(), Converters.convertAddress(estiamteFrom),
				currentOrder.getTo().getName(), Converters.convertAddress(estiamteTo), shData);
		if (res.getResult().getStatus().equals(NOK)) {
			FacesMessage msg = new FacesMessage(SEVERITY_ERROR, "Request Order ",
					res.getResult().getErrorDescription());

			return msg;
		}

		// }
		// se va bene assegno a shipping order i nuovi valori:

		FacesMessage msg = new FacesMessage(SEVERITY_INFO, "Valutazione Costo",
				"Ordine: " + currentOrder.getId() + " costo: " + res.getTotalCost());
		return msg;
	}

	public FacesMessage requestOrder(MyOrder currentOrder) {
		// preparo ShippingOrderData per la request:
		ShippingOrderData shData = new ShippingOrderData();// sgotObj.createShippingOrderData();
		shData.setShipmentId(currentOrder.getId());
		shData.setTerminiDiConsegna(new ShippingOrderData.TerminiDiConsegna());// sgotObj.createShippingOrderDataTerminiDiConsegna());
		for (TerminiDiConsegna.Entry entry : currentOrder.getOrderData().getTerminiDiConsegna().getEntry()) {
			ShippingOrderData.TerminiDiConsegna.Entry shEntry = new ShippingOrderData.TerminiDiConsegna.Entry();// sgotObj.createShippingOrderDataTerminiDiConsegnaEntry();
			shEntry.setKey(entry.getKey());
			shEntry.setValue(entry.getValue());
			shData.getTerminiDiConsegna().getEntry().add(shEntry);
		}
		for (ShippingItemData fi : currentOrder.getOrderData().getShippingItems()) {
			shData.getShippingItems().add(fi);
		}
		RequestShippingResponseData res;
		res = requestShipping(currentOrder.getFrom().getName(), Converters.convertAddress(currentOrder.getFrom()),
				currentOrder.getTo().getName(), Converters.convertAddress(currentOrder.getTo()), shData);
		if (res.getResult().getStatus().equals(NOK)) {
			FacesMessage msg = new FacesMessage(SEVERITY_ERROR, "Request Order ",
					res.getResult().getErrorDescription());
			return msg;
		}
		// se va bene assegno a shipping order i nuovi valori:

		currentOrder.setId(res.getShippingOrderID());
		currentOrder.refreshData(showOrderDetails(currentOrder.getId()));
		TransportInfo trInfo;
		try {

			trInfo = getTransportInfo(currentOrder.getTransportID());
		} catch (Exception ex) {
			logger.error("errore gat", ex);
			FacesMessage msg = new FacesMessage(SEVERITY_ERROR, "Request Order ", ex.getMessage());
			return msg;
		}
		FacesMessage msg;
		MyTransport currentTransport;
		if (trInfo != null) {
			currentTransport = new MyTransport(trInfo);
			transportList.add(currentTransport);

			msg = new FacesMessage(SEVERITY_INFO, "Successo Request",
					"Ordine: " + currentOrder.getId() + " con trasporto numero: " + currentTransport.getId());
		} else {
			msg = new FacesMessage(SEVERITY_WARN, "Error Request",
					"Ordine: " + currentOrder.getId() + " non ha nessun trasporto!!!");

		}
		return msg;
	}

	public FacesMessage confirmOrder(MyOrder currentOrder) {
		ResultOperationResponse confirmShipping = confirmShipping(currentOrder.getId());
		if (confirmShipping.getStatus().equals(NOK)) {
			FacesMessage msg = new FacesMessage(SEVERITY_ERROR, "Conferma Ordine: ",
					confirmShipping.getErrorDescription());
			return msg;
		}

		// currentOrder.setOrderDetails(showOrderDetails(currentOrder.getId()));
		// devo aggiornare il trasporto di transportList:
		for (MyTransport t : transportList) {
			if (t.getOrderID().equals(currentOrder.getId())) {
				TransportInfo trInfo = null;
				try {
					trInfo = getTransportInfo(currentOrder.getTransportID());
				} catch (GATException_Exception ex) {
					logger.error("errore gat", ex);
					FacesMessage msg = new FacesMessage(SEVERITY_ERROR, "Conferma Ordine: ", ex.getMessage());
					return msg;
				}
				if (trInfo != null) {
					t.setStatus(trInfo.getStatus());
				}
			}
		}
		// aggiorno lo stato:

		FacesMessage msg = new FacesMessage(SEVERITY_INFO, "Conferma Ordine: ",
				" Ordine " + currentOrder.getId() + "confermato");
		return msg;
	}

	public FacesMessage dropOrder(MyOrder currentOrder) {

		ResultOperationResponse dropShipping = dropShipping(currentOrder.getId());
		if (dropShipping.getStatus().equals(NOK)) {
			FacesMessage msg = new FacesMessage(SEVERITY_ERROR, "Drop Ordine: ", dropShipping.getErrorDescription());
			return msg;
		}

		// currentOrder.setOrderDetails(showOrderDetails(currentOrder.getId()));
		// devo rimuovere il trasporto di transportList:
		for (MyTransport t : transportList) {
			if (t.getOrderID().equals(currentOrder.getId())) {
				transportList.remove(t);
				break;
			}
		}
		orderList.remove(currentOrder);
		FacesMessage msg = new FacesMessage(SEVERITY_INFO, "Drop Ordine: ",
				" Ordine " + currentOrder.getId() + " rimosso");
		return msg;
	}

	FacesMessage locateOrder(MyOrder currentOrder) {
		// currentOrder.setOrderDetails(showOrderDetails(currentOrder.getId()));
		// devo rimuovere il trasporto di transportList:
		FacesMessage msg;
		for (MyTransport t : transportList) {
			if (t.getOrderID().equals(currentOrder.getId())) {
				msg = new FacesMessage(SEVERITY_INFO, "Locate Ordine: ",
						" Ordine " + currentOrder.getId() + " in carico al vettore: " + t.getVettore());
				return msg;
			}
		}
		msg = new FacesMessage(SEVERITY_ERROR, "Locate Ordine: ",
				"Impossibile localizzare Ordine " + currentOrder.getId());
		return msg;
	}

	public FacesMessage startTransport(String trID, String vectorName) {

		Date now = new Date();
		// mando un messaggio a GAT su MQ
		Map<String, String> properties = new HashMap<String, String>();
		// aggiungo alcune proprieta'
		// TIMESTAMP
		properties.put(MQ_KEY_TIMESTAMP, now.toString());
		// TransportID
		properties.put(MQ_KEY_TRANSPORT_ID, trID);

		properties.put(MQ_KEY_VECTOR_NAME, vectorName);
		// CAUSE
		properties.put(MQ_KEY_CAUSE, ON_DELIVERY_STATUS);

		ItseasyProducer producer = new ItseasyProducer();
		JMSProducer jmsProducer = jmsContext.createProducer();
		producer.publishTextMessage(gatTopic, jmsProducer, "Partito ", properties);
		TransportInfo info;
		try {
			info = getTransportInfo(trID);
		} catch (GATException_Exception ex) {
			logger.error("errore gat", ex);
			FacesMessage msg = new FacesMessage(SEVERITY_ERROR, "Avvio trasporto: ", ex.getMessage());
			return msg;
		}

		refreshTransports();
		refreshOrder(info.getOrderID());

		FacesMessage msg = new FacesMessage(SEVERITY_INFO, "Avvio trasporto ", "confermato per ordine: " + trID);
		return msg;
	}

	public FacesMessage doneTransport(String trID, String vectorName) {
		Date now = new Date();
		// mando un messaggio a GAT su MQ
		Map<String, String> properties = new HashMap<String, String>();
		// aggiungo alcune proprieta'
		// TIMESTAMP
		properties.put(MQ_KEY_TIMESTAMP, now.toString());
		// TransportID
		properties.put(MQ_KEY_TRANSPORT_ID, valueOf(trID));

		properties.put(MQ_KEY_VECTOR_NAME, vectorName);
		// CAUSE
		properties.put(MQ_KEY_CAUSE, DONE_STATUS);

		ItseasyProducer producer = new ItseasyProducer();
		JMSProducer jmsProducer = jmsContext.createProducer();
		producer.publishTextMessage(gatTopic, jmsProducer, "Completato ", properties);
		TransportInfo info;
		try {
			info = getTransportInfo(trID);
		} catch (Exception ex) {
			logger.error("errore gat", ex);
			FacesMessage msg = new FacesMessage(SEVERITY_ERROR, "Avvio trasporto: ", ex.getMessage());
			return msg;
		}

		refreshTransports();
		refreshOrder(info.getOrderID());
		FacesMessage msg = new FacesMessage(SEVERITY_INFO, "Consegna trasporto ", "consegnato ordine: " + trID);
		return msg;
	}

	public FacesMessage rejectTransport(String trID, String vectorName) {

		Date now = new Date();
		// mando un messaggio a GAT su MQ
		Map<String, String> properties = new HashMap<String, String>();
		// aggiungo alcune proprieta'
		// TIMESTAMP
		properties.put(MQ_KEY_TIMESTAMP, now.toString());
		// TransportID
		properties.put(MQ_KEY_TRANSPORT_ID, valueOf(trID));

		properties.put(MQ_KEY_VECTOR_NAME, vectorName);

		// CAUSE
		properties.put(MQ_KEY_CAUSE, REJECT_STATUS);

		ItseasyProducer producer = new ItseasyProducer();
		JMSProducer jmsProducer = jmsContext.createProducer();
		producer.publishTextMessage(gatTopic, jmsProducer, "Rifiutato", properties);
		TransportInfo info;
		try {
			info = getTransportInfo(trID);
		} catch (GATException_Exception ex) {
			logger.error("errore gat", ex);
			FacesMessage msg = new FacesMessage(SEVERITY_ERROR, "Avvio trasporto: ", ex.getMessage());
			return msg;
		}

		refreshTransports();
		refreshOrder(info.getOrderID());

		FacesMessage msg = new FacesMessage(SEVERITY_INFO, "Rifiuto trasporto: ", valueOf(trID));
		return msg;
	}

	private void refreshOrder(String orderID) {
		for (MyOrder currentOrder : orderList) {
			if (currentOrder.getId().equals(orderID)) {
				currentOrder.refreshData(showOrderDetails(currentOrder.getId()));
				break;
			}
		}
	}

	private void refreshTransports() {
		TransportInfo info;

		for (MyTransport currentTr : transportList) {
			try {
				info = getTransportInfo(currentTr.getId());
			} catch (GATException_Exception ex) {
				logger.error("errore gat", ex);
				return;
			}
			currentTr.setDestinationSite(info.getDestinationSite());
			currentTr.setStatus(info.getStatus());
			currentTr.setSourceSite(info.getSourceSite());
			currentTr.setVettore(info.getVettore());
		}
	}

	public List<MyOrder> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<MyOrder> orderList) {
		this.orderList = orderList;
	}

	public List<MyTransport> getTransportList() {
		return transportList;
	}

	public void setTransportList(List<MyTransport> transportList) {
		this.transportList = transportList;
	}

	private java.util.List<it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderDetails> loadOrderIDS() {
		SGOTadminService port = adminService.getSGOTadminServicePort();
		return port.loadOrderIDS();
	}

	private TransportInfo getTransportInfo(String trId) throws GATException_Exception {
		SGOTadminService port = adminService.getSGOTadminServicePort();
		return port.getTransportInfo(trId);
	}

	private ShippingOrderDetails showOrderDetails(String id) {
		SGOTadminService port = adminService.getSGOTadminServicePort();
		return port.showOrderDetails(id);
	}

	private ResultOperationResponse confirmShipping(String shippingOrderID) {
		ShippingOrderManager port = sgotService.getShippingOrderManagerPort();
		return port.confirmShipping(shippingOrderID);
	}

	private RequestShippingResponseData estimateShipping(java.lang.String fromName,
			it.vige.greenarea.sgapl.sgot.webservice.Address fromAddress, java.lang.String toName,
			it.vige.greenarea.sgapl.sgot.webservice.Address toAddress,
			it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderData shippingOrder) {
		ShippingOrderManager port = sgotService.getShippingOrderManagerPort();
		shippingOrder.setFromName(fromName);
		shippingOrder.setToName(toName);
		shippingOrder.setFromAddress(fromAddress);
		shippingOrder.setToAddress(toAddress);
		return port.estimateShipping(shippingOrder);
	}

	private RequestShippingResponseData requestShipping(java.lang.String fromName,
			it.vige.greenarea.sgapl.sgot.webservice.Address fromAddress, java.lang.String toName,
			it.vige.greenarea.sgapl.sgot.webservice.Address toAddress,
			it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderData shippingOrder) {
		ShippingOrderManager port = sgotService.getShippingOrderManagerPort();
		return port.requestShipping(fromName, fromAddress, toName, toAddress, shippingOrder);
	}

	private ResultOperationResponse dropShipping(String shippingOrderID) {
		ShippingOrderManager port = sgotService.getShippingOrderManagerPort();
		return port.dropShipping(shippingOrderID);
	}
}
