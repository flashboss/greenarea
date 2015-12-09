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

import static it.vige.greenarea.Constants.BASE_URI_ADMINISTRATOR;
import static it.vige.greenarea.Conversioni.convertiFiltersToFiltri;
import static it.vige.greenarea.Conversioni.convertiRichiesteToShippingOrders;
import static it.vige.greenarea.cl.library.entities.OrderStatus.StateValue.ongoing;
import static it.vige.greenarea.cl.library.entities.OrderStatus.StateValue.ready;
import static it.vige.greenarea.cl.library.entities.OrderStatus.StateValue.suspended;
import static it.vige.greenarea.cl.library.entities.OrderStatus.StateValue.unknown;
import static it.vige.greenarea.gtg.constants.ConversioniGTG.convertiRichiesta;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.JMS_Credential;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.JMS_PRINCIPAL;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.OBJ_FACTORY_JMS_TYPE;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.CANNOT_LOCATE;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.CONFIRM_NOT_ALLOWED;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.CONFIRM_UNKNOWN_SHIPPING_ID;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.DROP_NOT_ALLOWED;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.DROP_UNKNOWN_SHIPPING_ID;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.ESTIMATE_SHIPPING_NO_TRANSPORT;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.GET_STATUS_UNKNOWN_SHIPPING_ID;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.GET_TRACKING_UNKNOWN_SHIPPING_ID;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.LOCATE_UNKNOWN_SHIPPING_ID;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.REQUEST_SHIPPING_NO_TRANSPORT;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.UNKNOWN_REF_CONTRACT;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.createErrorResponse;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.ResultStatus.NOK;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.ResultStatus.OK;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.Customer;
import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.Filter;
import it.vige.greenarea.cl.library.entities.ShippingItem;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.dto.GeoLocationInterface;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.file.ImportaCSVFile;
import it.vige.greenarea.file.ImportaFile;
import it.vige.greenarea.gtg.db.facades.FreightFacade;
import it.vige.greenarea.gtg.db.facades.TransportFacade;
import it.vige.greenarea.gtg.db.facades.TransportServiceClassFacade;
import it.vige.greenarea.itseasy.lib.configurationData.SGAPLconstants;
import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyStoreInfo;
import it.vige.greenarea.sgapl.sgot.business.exception.GATException;
import it.vige.greenarea.sgapl.sgot.facade.CustomerFacade;
import it.vige.greenarea.sgapl.sgot.facade.ShippingItemFacade;
import it.vige.greenarea.sgapl.sgot.facade.ShippingOrderFacade;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.ResultStatus;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.GetShippingStatusResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.GetTrackingUrlResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.LocateShippingResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.RequestShippingResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.RequestShippingsResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.ResultOperationResponse;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.ShippingOrderData;
import it.vige.greenarea.vo.RichiestaXML;

@Named("sgotBean")
@Stateless
public class SGOTbean {

	private Logger logger = getLogger(getClass());

	@EJB
	private GATbean gATbean;
	@EJB
	private CustomerFacade clientAccountFacade;
	@EJB
	private ShippingItemFacade shippingItemFacade;
	@EJB
	private TransportFacade transportFacade;
	@EJB
	private FreightFacade freightFacade;
	@EJB
	private TransportServiceClassFacade transportServiceClassFacade;
	@EJB
	private ShippingOrderFacade shippingOrderFacade;
	ItseasyStoreInfo store = new ItseasyStoreInfo(OBJ_FACTORY_JMS_TYPE, "", JMS_PRINCIPAL, JMS_Credential);

	public RequestShippingResponseData addShippingItem(ShippingItem shippingItem) {
		shippingItemFacade.create(shippingItem);
		logger.debug("HO creato un item: " + shippingItem.toString());

		RequestShippingResponseData res = new RequestShippingResponseData();
		res.setResult(new ResultOperationResponse(ResultStatus.OK));
		res.setShippingOrderID(shippingItem.getShippingOrder().getId());
		res.setMaxTimeValidity(new Long(1000));
		res.setMaxTimeShipment(new Long(1000));
		return res;
	}

	public RequestShippingResponseData addShipping(ShippingOrder shippingOrder) {
		// mi procuro il client account
		// TODO andra' fatto prendendolo dalla session
		// per ora cerco il primo clientaccount disponibile, se non c'e' lo
		// creo:
		Customer customer = new Customer(shippingOrder.getDestinatario().getName(),
				shippingOrder.getMittente().getName(), "");
		try {
			clientAccountFacade.create(customer);
		} catch (Exception ex) {
			return new RequestShippingResponseData(
					createErrorResponse(UNKNOWN_REF_CONTRACT, "Request Shipping: Ref contract is unknown"));
		}
		shippingOrder.setOrderStatus(suspended);
		shippingOrder.setCustomer(customer);
		shippingOrderFacade.create(shippingOrder);

		// mi creo tutti gli shippingItem e li metto in un array (nel frattempo
		// creo anche la lista per GAT
		logger.debug("HO creato un ordine: " + shippingOrder.toString());

		List<String> shItemNameList = new ArrayList<String>();
		for (ShippingItem sid : shippingOrder.getShippingItems()) {
			sid.setShippingOrder(shippingOrder);
			shippingItemFacade.create(sid);
			shItemNameList.add(sid.getId());
		}
		shippingOrderFacade.edit(shippingOrder);

		RequestShippingResponseData res = new RequestShippingResponseData();
		res.setResult(new ResultOperationResponse(ResultStatus.OK));
		res.setShippingOrderID(shippingOrder.getId());
		res.setMaxTimeValidity(new Long(1000));
		res.setMaxTimeShipment(new Long(1000));
		return res;

	}

	public RequestShippingResponseData requestShipping(ShippingOrder shippingOrder) {

		RequestShippingResponseData res = addShipping(shippingOrder);
		ShippingOrder so = shippingOrderFacade.find(res.getShippingOrderID());
		String cost;
		try {

			cost = gATbean.getTransport(so);

		} catch (GATException ex) {
			shippingOrderFacade.remove(so);
			return new RequestShippingResponseData(
					createErrorResponse(REQUEST_SHIPPING_NO_TRANSPORT, "Request Shipping: No transport available"));
		}
		res.setTotalCost(cost);
		return res;
	}

	public ResultOperationResponse requestShippings(List<ShippingOrder> richieste) {
		ResultOperationResponse result = new ResultOperationResponse(OK);
		try {
			int i = 0;
			for (ShippingOrder richiesta : richieste) {
				logger.info("inserisco il record " + i++);

				result = requestShipping(richiesta).getResult();
				if (result.getStatus() == NOK)
					return result;
			}
		} catch (Exception ex) {
			logger.error("errore nell'inserimento degli ordini", ex);
			result = new ResultOperationResponse(NOK);
		}
		return result;
	}

	public String caricaTrasportiDaFile(InputStream inputStream) {
		FacesContext context = FacesContext.getCurrentInstance();

		ImportaFile importaFile = new ImportaCSVFile(new OperatoreLogistico(new GreenareaUser("tnt")), null);

		try {
			Client client = newClient();
			Builder bldr = client.target(BASE_URI_ADMINISTRATOR + "/getFiltersForOP/tnt").request(APPLICATION_JSON);
			List<Filter> filters = bldr.get(new GenericType<List<Filter>>() {
			});

			List<RichiestaXML> richiesteXML = importaFile.prelevaDati(inputStream, convertiFiltersToFiltri(filters));
			List<Richiesta> richieste = importaFile.convertiARichieste(richiesteXML,
					new OperatoreLogistico(new GreenareaUser("tnt")));
			requestShippings(convertiRichiesteToShippingOrders(richieste));
		} catch (Exception ex) {
			logger.error("Errore caricamento trasporti da file");
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				logger.error("Errore nella chiusura del file");
			}
		}
		FacesMessage msg = new FacesMessage("GTG database inizializzato correttamente");
		context.addMessage(null, msg);
		return "";
	}

	public RequestShippingsResponseData getShippings(String operatoreLogistico) {
		List<ShippingOrderData> shippingOrderDatas = null;
		RequestShippingsResponseData result = new RequestShippingsResponseData(new ResultOperationResponse(OK));
		try {
			List<ShippingOrder> shippingOrders = shippingOrderFacade.findAll(operatoreLogistico);
			for (ShippingOrder shippingOrder : shippingOrders)
				shippingOrder.setShippingItems(shippingItemFacade.findAll(shippingOrder));
			shippingOrderDatas = new ArrayList<ShippingOrderData>();
			for (ShippingOrder ShippingOrder : shippingOrders)
				shippingOrderDatas.add(convertiRichiesta(ShippingOrder));
		} catch (Exception ex) {
			logger.error("errore nell'inserimento degli ordini", ex);
			result = new RequestShippingsResponseData(new ResultOperationResponse(NOK));
		}
		result.setShippings(shippingOrderDatas);
		return result;
	}

	public RequestShippingResponseData estimateShipping(ShippingOrderData shippingOrder) {

		String cost;
		DBGeoLocation mitt = new DBGeoLocation();
		mitt.setName(shippingOrder.getFromName());
		mitt.setCity(shippingOrder.getFromAddress().getCity());
		mitt.setCountry(shippingOrder.getFromAddress().getCountry());
		mitt.setNumber(shippingOrder.getFromAddress().getNumber());
		mitt.setStreet(shippingOrder.getFromAddress().getStreet());
		mitt.setZipCode(shippingOrder.getFromAddress().getZipCode());
		DBGeoLocation dest = new DBGeoLocation();
		dest.setName(shippingOrder.getToName());
		dest.setCity(shippingOrder.getToAddress().getCity());
		dest.setCountry(shippingOrder.getToAddress().getCountry());
		dest.setNumber(shippingOrder.getToAddress().getNumber());
		dest.setStreet(shippingOrder.getToAddress().getStreet());
		dest.setZipCode(shippingOrder.getToAddress().getZipCode());

		try {

			cost = gATbean.estimateTransportCost(mitt, dest, new HashMap<String, String>());

		} catch (GATException ex) {
			return new RequestShippingResponseData(
					createErrorResponse(ESTIMATE_SHIPPING_NO_TRANSPORT, "Estimate Shipping: No transport available"));
		}
		RequestShippingResponseData res = new RequestShippingResponseData();
		res.setResult(new ResultOperationResponse(ResultStatus.OK));
		res.setShippingOrderID(null);
		res.setMaxTimeValidity(new Long(1000));
		res.setMaxTimeShipment(new Long(1000));
		res.setTotalCost(cost);
		return res;
	}

	public ResultOperationResponse confirmShipping(String shippingOrderID) {
		ShippingOrder so = shippingOrderFacade.find(shippingOrderID);
		ResultOperationResponse result = new ResultOperationResponse();
		if (so == null) {
			result.setStatus(ResultStatus.NOK);
			result.setErrorCode(CONFIRM_UNKNOWN_SHIPPING_ID);
			result.setErrorDescription("Confirm Shipping: shipping order ID <" + shippingOrderID + "> is unknown");
		} else {
			if (so.getOrderStatus().equals(suspended)) {
				so.setOrderStatus(ready);
				shippingOrderFacade.edit(so);
				try {
					gATbean.startShipment(so);
				} catch (GATException ex) {
					result.setStatus(ResultStatus.NOK);
					result.setErrorCode(CONFIRM_NOT_ALLOWED);
					result.setErrorDescription("ShippingOrderID unknown");
				}
				result.setStatus(ResultStatus.OK);
			}
		}

		return result;
	}

	public ResultOperationResponse dropShipping(String shippingOrderID) {
		ShippingOrder so = shippingOrderFacade.find(shippingOrderID);
		ResultOperationResponse result = new ResultOperationResponse();
		if (so == null) {
			result.setStatus(ResultStatus.NOK);
			result.setErrorCode(DROP_UNKNOWN_SHIPPING_ID);
			result.setErrorDescription("Confirm Shipping: shipping order ID <" + shippingOrderID + "> is unknown");
		} else {
			if (so.getOrderStatus().equals(suspended)) {
				shippingOrderFacade.remove(so);
				Transport transport = transportFacade.find(so.getId());
				transportFacade.remove(transport);
				result.setStatus(ResultStatus.OK);

			} else {
				result.setStatus(ResultStatus.NOK);
				result.setErrorCode(DROP_NOT_ALLOWED);
				result.setErrorDescription("Drop Shipping: cannot drop order already confirmed");
			}
		}

		return result;
	}

	public LocateShippingResponseData locateShipping(String shippingOrderID) {
		ShippingOrder so = shippingOrderFacade.find(shippingOrderID);
		if (so == null) {
			return new LocateShippingResponseData(createErrorResponse(LOCATE_UNKNOWN_SHIPPING_ID,
					"Locate Shipping: shipping order ID <" + shippingOrderID + "> is unknown"));
		}
		if (so.getOrderStatus().equals(ready) || so.getOrderStatus().equals(ongoing)) {
			LocateShippingResponseData locateRes;

			Transport t = so.getTransport();
			locateRes = new LocateShippingResponseData(new ResultOperationResponse(ResultStatus.OK));
			locateRes.setTransportState(t.getTransportState());
			locateRes.setExchangeSiteName(null);
			if ((t.getTransportState().equals(SGAPLconstants.READY_STATUS))
					|| (t.getTransportState().equals(SGAPLconstants.DONE_STATUS))) {
				locateRes.setExchangeSiteName(t.getRoute().get(t.getActiveLegIndex()).getSource().getName());
				StringBuilder sb = new StringBuilder();
				GeoLocationInterface gli = t.getRoute().get(t.getActiveLegIndex()).getSource().getLocation();
				if (gli.getStreet() != null && (gli.getStreet().length() > 0)) {
					sb.append(gli.getStreet());
				}
				if ((gli.getNumber() != null) && (gli.getNumber().length() > 0)) {
					sb.append(" n. ").append(gli.getNumber());
				}
				if (gli.getCity() != null && (gli.getCity().length() > 0)) {
					sb.append(" - ").append(gli.getCity()).append(" ");
				}
				if (gli.getAdminAreaLevel2() != null && gli.getAdminAreaLevel2().length() > 0) {
					sb.append(" (").append(gli.getAdminAreaLevel2()).append(")");
				}
				if (gli.getAdminAreaLevel1() != null && gli.getAdminAreaLevel1().length() > 0) {
					sb.append(" - ").append(gli.getAdminAreaLevel1());
				}

				locateRes.setAddress(sb.toString());
			} else if (t.getTransportState().equals(SGAPLconstants.ON_DELIVERY_STATUS)) {
				locateRes.setAddress(t.getRoute().get(t.getActiveLegIndex()).getVector());
			}
			return locateRes;
		} else {
			return new LocateShippingResponseData(createErrorResponse(CANNOT_LOCATE, "locate Shipping: Shipping ID <"
					+ shippingOrderID + "> is not active: " + "status is: " + so.getOrderStatus().toString()));
		}

	}

	public GetTrackingUrlResponseData getTrackingUrl(String shippingID) {
		ShippingOrder so = shippingOrderFacade.find(shippingID);
		GetTrackingUrlResponseData result = new GetTrackingUrlResponseData();
		if (so == null) {
			result.setResult(createErrorResponse(GET_TRACKING_UNKNOWN_SHIPPING_ID,
					"Get Tracking URL: ShippingID <" + shippingID + "> is unknown"));
			result.setUrl("");
		} else {
			result.setResult(new ResultOperationResponse(ResultStatus.OK));

			result.setUrl(so.getTrackingURL());
		}
		return result;
	}

	public GetShippingStatusResponseData getShippingStatus(String shippingID) {
		ShippingOrder so = shippingOrderFacade.find(shippingID);
		GetShippingStatusResponseData result = new GetShippingStatusResponseData();
		if (so == null) {
			result.setResult(createErrorResponse(GET_STATUS_UNKNOWN_SHIPPING_ID,
					"Get Order Status: ShippingID <" + shippingID + "> is unknown"));
			result.setShippingStatus(unknown);
		} else {
			result.setResult(new ResultOperationResponse(ResultStatus.OK));
			result.setShippingStatus(so.getOrderStatus());
		}
		return result;
	}
}
