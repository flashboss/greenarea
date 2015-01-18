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
package it.vige.greenarea.sgapl.sgot.webservice;

import static it.vige.greenarea.gtg.constants.ConversioniGTG.convertiShippingOrderDataToShippingOrder;
import static it.vige.greenarea.gtg.constants.ConversioniGTG.convertiShippingOrderDataToShippingOrders;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.CONFIRM_NULL_SHIPPING_ID;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.DROP_NULL_SHIPPING_ID;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.ESTIMATE_SHIPPING_NO_DEST;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.ESTIMATE_SHIPPING_NO_SOURCE;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.GET_STATUS_NULL_SHIPPING_ID;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.GET_TRACKING_NULL_SHIPPING_ID;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.LOCATE_NULL_SHIPPING_ID;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.REQUEST_SHIPPING_NO_DATA;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.REQUEST_SHIPPING_NO_DEST;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.REQUEST_SHIPPING_NO_ITEMS;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.REQUEST_SHIPPING_NO_SOURCE;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.createErrorResponse;
import static it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.ResultStatus.NOK;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.dto.Address;
import it.vige.greenarea.sgapl.sgot.business.SGOTbean;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.GetShippingStatusResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.GetTrackingUrlResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.LocateShippingResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.RequestShippingResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.RequestShippingsResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.ResultOperationResponse;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.ShippingItemData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.ShippingOrderData;

import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.slf4j.Logger;

@WebService(serviceName = "ShippingOrderManager")
public class ShippingOrderManager {

	private Logger logger = getLogger(getClass());

	@EJB
	private SGOTbean sgotBean;

	/**
	 * This is a sample web service operation
	 */
	@WebMethod(operationName = "testNewOrder")
	public String testNewOrder(@WebParam(name = "name") String txt) {

		ShippingItemData[] siData = new ShippingItemData[3];
		ShippingItemData si1 = new ShippingItemData("uno", "primo item",
				new HashMap<String, String>());
		ShippingItemData si2 = new ShippingItemData("due", "secondo item",
				new HashMap<String, String>());
		ShippingItemData si3 = new ShippingItemData("tre", "terzo item",
				new HashMap<String, String>());
		siData[0] = si1;
		siData[1] = si2;
		siData[2] = si3;
		ShippingOrderData soData = new ShippingOrderData("new_id", siData,
				new HashMap<String, String>(), txt);
		RequestShippingResponseData res;

		StringBuilder sb = new StringBuilder("Result status: ");
		DBGeoLocation mitt = new DBGeoLocation();
		mitt.setName("Stabilimento Rozzano");
		mitt.setAdminAreaLevel1("Lombardia");
		mitt.setAdminAreaLevel2("Milano");
		mitt.setCity("Rozzano");
		mitt.setZipCode("20089");
		DBGeoLocation dest = new DBGeoLocation();
		dest.setName("Negozio di Via Roma");
		dest.setAdminAreaLevel1("Piemonte");
		dest.setAdminAreaLevel2("Torino");
		dest.setCity("Torino");
		dest.setZipCode("10121");
		dest.setNumber("12");
		dest.setStreet("Via Roma");
		DBGeoLocation mittest = new DBGeoLocation();
		mittest.setName("");
		mittest.setAdminAreaLevel1(null);
		mittest.setAdminAreaLevel2("");
		mittest.setCity("Torino");
		mittest.setZipCode("10148");
		mittest.setCountry("IT");
		mittest.setNumber("");
		mittest.setRadius(0);
		mittest.setStreet("");
		DBGeoLocation destest = new DBGeoLocation();
		destest.setName("Negozio");
		destest.setAdminAreaLevel1(null);
		destest.setAdminAreaLevel2("TO");
		destest.setCity("");
		destest.setCountry("IT");
		destest.setZipCode("10121");
		destest.setNumber("");
		destest.setStreet("");
		destest.setRadius(0);
		soData.setFromAddress(new Address(mittest.getStreet(), mittest
				.getNumber(), mittest.getZipCode(), mittest.getCity(), null,
				null, mittest.getCountry()));

		res = sgotBean.estimateShipping(soData);
		if (res.getResult().getStatus().equals(NOK)) {
			sb.append("Errore estimate: ").append(
					res.getResult().getErrorDescription());
		} else {
			sb.append("Estimate ok con costo:").append(res.getTotalCost());
		}
		res = sgotBean
				.requestShipping(convertiShippingOrderDataToShippingOrder(soData));
		sb.append(res.getResult().getStatus().name()).append(" --> ");
		if (res.getResult().getStatus().equals(NOK)) {
			sb.append("\n Errore request: ").append(
					res.getResult().getErrorDescription());
		} else {
			sb.append("\n Salvato ordine con ID:").append(
					res.getShippingOrderID());
		}
		return "Result: " + sb.toString() + " !";
	}

	/**
	 * metodo di inserimento delle richieste per il servizio
	 * AggiornamentoConsegneERitiri
	 */
	@WebMethod(operationName = "requestShippings")
	public ResultOperationResponse requestShippings(
			@WebParam(name = "shippings") List<ShippingOrderData> richieste) {
		return sgotBean
				.requestShippings(convertiShippingOrderDataToShippingOrders(richieste));
	}

	/**
	 * metodo di lettura delle richieste per il servizio RecuperaConsegneERitiri
	 */
	@WebMethod(operationName = "getShippings")
	public RequestShippingsResponseData getShippings(
			@WebParam(name = "operatoreLogistico") String operatoreLogistico) {
		return sgotBean.getShippings(operatoreLogistico);
	}

	private RequestShippingResponseData validateShipping(String fromName,
			Address fromAddress, String toName, Address toAddress,
			ShippingOrderData shippingOrder) {
		// prima serie di check per vedere se ci sono elementi null
		if (shippingOrder == null) {
			return new RequestShippingResponseData(createErrorResponse(
					REQUEST_SHIPPING_NO_DATA,
					"Request Shipping with null ShippingOrderData"));
		}
		if (toAddress == null) {
			return new RequestShippingResponseData(createErrorResponse(
					REQUEST_SHIPPING_NO_DEST,
					"Request Shipping with null Destination Address"));
		}
		if (fromAddress == null) {
			return new RequestShippingResponseData(createErrorResponse(
					REQUEST_SHIPPING_NO_SOURCE,
					"Request Shipping with null Source Address"));
		}
		if ((shippingOrder.getShippingItems() == null)
				|| (shippingOrder.getShippingItems().length == 0)) {
			return new RequestShippingResponseData(createErrorResponse(
					REQUEST_SHIPPING_NO_ITEMS,
					"Request Shipping with no ShippingItemList"));
		}
		logger.info("+++ richiesta  ordine: " + fromAddress.toString());
		return null;
	}

	/**
	 * con questo metodo si richiede a SGAPL di fornire un preventivo per una
	 * spedizione di item (dettagliata nella struttura complessa
	 * ShippingOrderData) dall'indirizzo fromAddress all'indirizzo toAddress
	 */
	@WebMethod(operationName = "addShipping")
	public RequestShippingResponseData addShipping(
			@WebParam(name = "fromName") String fromName,
			@WebParam(name = "fromAddress") Address fromAddress,
			@WebParam(name = "toName") String toName,
			@WebParam(name = "toAddress") Address toAddress,
			@WebParam(name = "shippingOrder") ShippingOrderData shippingOrder) {
		validateShipping(fromName, fromAddress, toName, toAddress,
				shippingOrder);
		DBGeoLocation mitt = new DBGeoLocation();
		mitt.setName(fromName);
		mitt.setStreet(fromAddress.getStreet());
		mitt.setNumber(fromAddress.getNumber());
		mitt.setZipCode(fromAddress.getZipCode());
		mitt.setCity(fromAddress.getCity());
		mitt.setCountry(fromAddress.getCountry());
		DBGeoLocation dest = new DBGeoLocation();
		dest.setName(toName);
		dest.setStreet(toAddress.getStreet());
		dest.setNumber(toAddress.getNumber());
		dest.setZipCode(toAddress.getZipCode());
		dest.setCity(toAddress.getCity());
		dest.setCountry(toAddress.getCountry());
		return sgotBean
				.addShipping(convertiShippingOrderDataToShippingOrder(shippingOrder));
	}

	/**
	 * con questo metodo si richiede a SGAPL di fornire un preventivo per una
	 * spedizione di item (dettagliata nella struttura complessa
	 * ShippingOrderData) dall'indirizzo fromAddress all'indirizzo toAddress
	 */
	@WebMethod(operationName = "requestShipping")
	public RequestShippingResponseData requestShipping(
			@WebParam(name = "fromName") String fromName,
			@WebParam(name = "fromAddress") Address fromAddress,
			@WebParam(name = "toName") String toName,
			@WebParam(name = "toAddress") Address toAddress,
			@WebParam(name = "shippingOrder") ShippingOrderData shippingOrder) {
		validateShipping(fromName, fromAddress, toName, toAddress,
				shippingOrder);
		DBGeoLocation mitt = new DBGeoLocation();
		mitt.setName(fromName);
		mitt.setStreet(fromAddress.getStreet());
		mitt.setNumber(fromAddress.getNumber());
		mitt.setZipCode(fromAddress.getZipCode());
		mitt.setCity(fromAddress.getCity());
		mitt.setCountry(fromAddress.getCountry());
		DBGeoLocation dest = new DBGeoLocation();
		dest.setName(toName);
		dest.setStreet(toAddress.getStreet());
		dest.setNumber(toAddress.getNumber());
		dest.setZipCode(toAddress.getZipCode());
		dest.setCity(toAddress.getCity());
		dest.setCountry(toAddress.getCountry());
		return sgotBean
				.requestShipping(convertiShippingOrderDataToShippingOrder(shippingOrder));
	}

	/**
	 * con questo metodo si richiede a SGAPL di fornire un preventivo per una
	 * spedizione di item (dettagliata nella struttura complessa
	 * ShippingOrderData) dall'indirizzo fromAddress all'indirizzo toAddress
	 */
	@WebMethod(operationName = "estimateShipping")
	public RequestShippingResponseData estimateShipping(
			@WebParam(name = "shippingOrder") ShippingOrderData shippingOrder) {

		if (shippingOrder.getToAddress() == null) {
			return new RequestShippingResponseData(createErrorResponse(
					ESTIMATE_SHIPPING_NO_DEST,
					"Estimate Shipping with null Destination Address"));
		}
		if (shippingOrder.getFromAddress() == null) {
			return new RequestShippingResponseData(createErrorResponse(
					ESTIMATE_SHIPPING_NO_SOURCE,
					"Estimate Shipping with null Source Address"));
		}

		return sgotBean.estimateShipping(shippingOrder);

	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "confirmShipping")
	public ResultOperationResponse confirmShipping(
			@WebParam(name = "shippingOrderID") String shippingOrderID) { // throws
																			// SGOException
																			// {
		if (shippingOrderID == null) {
			return createErrorResponse(CONFIRM_NULL_SHIPPING_ID,
					"Confirm Shipping: null ShippingOrderID");
		}

		return sgotBean.confirmShipping(shippingOrderID);

	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "dropShipping")
	public ResultOperationResponse dropShipping(
			@WebParam(name = "shippingOrderID") String shippingOrderID) { // throws
																			// SGOException
																			// {
		if (shippingOrderID == null) {
			return createErrorResponse(DROP_NULL_SHIPPING_ID,
					"Drop Shipping: null ShippingOrderID");
		}
		return sgotBean.dropShipping(shippingOrderID);
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "locateShipping")
	public LocateShippingResponseData locateShipping(
			@WebParam(name = "shippingOrderID") String shippingOrderID) { // throws
																			// SGOException
																			// {
		if (shippingOrderID == null) {
			return new LocateShippingResponseData(createErrorResponse(
					LOCATE_NULL_SHIPPING_ID,
					"Locate Shipping: null ShippingOrderID"));
		}

		return sgotBean.locateShipping(shippingOrderID);

	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "getTrackingUrl")
	public GetTrackingUrlResponseData getTrackingUrl(
			@WebParam(name = "shippingID") String shippingID) { // throws
																// SGOException
																// {
		if (shippingID == null) {
			return new GetTrackingUrlResponseData(createErrorResponse(
					GET_TRACKING_NULL_SHIPPING_ID,
					"Get Tracking URL: null ShippingOrderID"));
		}

		return sgotBean.getTrackingUrl(shippingID);

	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "getShippingStatus")
	public GetShippingStatusResponseData getShippingStatus(
			@WebParam(name = "shippingID") String shippingID) { // throws
																// SGOException
																// {
		if (shippingID == null) {
			return new GetShippingStatusResponseData(createErrorResponse(
					GET_STATUS_NULL_SHIPPING_ID,
					"Get Shipping Status: null ShippingOrderID"));
		}

		return sgotBean.getShippingStatus(shippingID);

	}
}
