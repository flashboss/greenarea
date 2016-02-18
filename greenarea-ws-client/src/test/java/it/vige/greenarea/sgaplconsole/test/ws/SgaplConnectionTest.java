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
package it.vige.greenarea.sgaplconsole.test.ws;
 
import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Constants.ITALY;
import static it.vige.greenarea.GTGsystem.giulioCesare;
import static it.vige.greenarea.GTGsystem.reiss;
import static it.vige.greenarea.sgaplconsole.test.ws.WSConversioni.convertiGeoLocationToWS;
import static java.util.Collections.singletonList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static javax.xml.ws.handler.MessageContext.HTTP_REQUEST_HEADERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.primefaces.util.Base64.encodeToString;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.xml.ws.BindingProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.sgapl.sgot.webservice.GetTrackingUrlResponseData;
import it.vige.greenarea.sgapl.sgot.webservice.SGOTadminService;
import it.vige.greenarea.sgapl.sgot.webservice.SGOTadminService_Service;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderDetails;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager_Service;
import it.vige.greenarea.sgrl.webservices.GeoLocation;
import it.vige.greenarea.sgrl.webservices.LogisticNetworkRouting;
import it.vige.greenarea.sgrl.webservices.LogisticNetworkRouting_Service;
import it.vige.greenarea.sgrl.webservices.SGRLServiceException_Exception;
import it.vige.greenarea.sgrl.webservices.SgrlRoute;

public class SgaplConnectionTest {

	private Logger logger = getLogger(getClass());
	private ShippingOrder shippingOrder;
	private List<String> alfacodes;

	@Before
	public void init() {
		alfacodes = addTransports();
		addShipping();
	}

	@After
	public void close() {
		deleteShipping();
		deleteTransports();
	}

	private void deleteShipping() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/deleteShipping").request(APPLICATION_JSON);
		ShippingOrder response = bldr.post(entity(shippingOrder, APPLICATION_JSON), ShippingOrder.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	private void deleteTransports() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/deleteSchedulers").request(APPLICATION_JSON);
		List<Transport> response = bldr.get(new GenericType<List<Transport>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	private void addShipping() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/addShipping").request(APPLICATION_JSON);
		ShippingOrder shippingOrder = new ShippingOrder("prova");
		shippingOrder.setTransport(new Transport(alfacodes.get(0)));
		ShippingOrder response = bldr.post(entity(shippingOrder, APPLICATION_JSON), ShippingOrder.class);
		assertNotNull(response);

		logger.info(response + "");
		this.shippingOrder = response;
	}

	private List<String> addTransports() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/addTransports").request(APPLICATION_JSON);
		List<String> response = bldr.get(new GenericType<List<String>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
		return response;
	}

	@Test
	public void testSGOTadminService() {
		SGOTadminService_Service service = new SGOTadminService_Service();
		SGOTadminService sGOTadminService = service.getSGOTadminServicePort();

		BindingProvider provider = (BindingProvider) sGOTadminService;
		Map<String, Object> req_ctx = provider.getRequestContext();
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String credenziale = "prova:prova";
		String credenzialeCriptata = encodeToString(credenziale.getBytes(), false);
		headers.put("Authorization", singletonList("_____" + credenzialeCriptata));
		req_ctx.put(HTTP_REQUEST_HEADERS, headers);
		/*
		 * req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
		 * "http://82.107.53.122/greenarea-service/SGOTadminService");
		 */
		/*
		 * switch a localhost
		 */

		req_ctx.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/greenarea-service/SGOTadminService");

		try {
			ShippingOrderDetails shippingOrderDetails = sGOTadminService.showOrderDetails(shippingOrder.getId());
			logger.info("shippingOrderDetails = " + shippingOrderDetails);
		} catch (Exception e) {
			logger.error("shippingOrderDetails console", e);
			fail();
		}
	}

	@Test
	public void testShippingOrderManagerService() {
		ShippingOrderManager_Service service = new ShippingOrderManager_Service();
		ShippingOrderManager shippingOrderManager = service.getShippingOrderManagerPort();

		BindingProvider provider = (BindingProvider) shippingOrderManager;
		Map<String, Object> req_ctx = provider.getRequestContext();
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String credenziale = "prova:prova";
		String credenzialeCriptata = encodeToString(credenziale.getBytes(), false);
		headers.put("Authorization", singletonList("_____" + credenzialeCriptata));
		req_ctx.put(HTTP_REQUEST_HEADERS, headers);
		/*
		 * req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
		 * "http://82.107.53.122/greenarea-service/ShippingOrderManager");
		 */
		/*
		 * switch a localhost
		 */

		req_ctx.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/greenarea-service/ShippingOrderManager");

		try {
			GetTrackingUrlResponseData getTrackingUrlResponseData = shippingOrderManager.getTrackingUrl("44L");
			logger.info("getTrackingUrlResponseData = " + getTrackingUrlResponseData);
		} catch (Exception e) {
			logger.error("getTrackingUrlResponseData console", e);
			fail();
		}
	}

	@Test
	public void testLogisticNetworkRoutingService() {
		LogisticNetworkRouting_Service service = new LogisticNetworkRouting_Service();
		LogisticNetworkRouting logisticNetworkRouting = service.getLogisticNetworkRoutingPort();

		BindingProvider provider = (BindingProvider) logisticNetworkRouting;
		Map<String, Object> req_ctx = provider.getRequestContext();
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String credenziale = "prova:prova";
		String credenzialeCriptata = encodeToString(credenziale.getBytes(), false);
		headers.put("Authorization", singletonList("_____" + credenzialeCriptata));
		req_ctx.put(HTTP_REQUEST_HEADERS, headers);
		/*
		 * req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
		 * "http://82.107.53.122/greenarea-sgr/LogisticNetworkRouting");
		 */
		/*
		 * switch a localhost
		 */

		req_ctx.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/greenarea-sgr/LogisticNetworkRouting");

		GeoLocation source = convertiGeoLocationToWS(reiss);
		source.setZipCode("00167");
		source.setCountry(ITALY);
		GeoLocation destination = convertiGeoLocationToWS(giulioCesare);
		destination.setZipCode("00987");
		destination.setCountry(ITALY);

		List<SgrlRoute> routes = null;
		try {
			routes = logisticNetworkRouting.getSGRLRoutes(source, destination, null);
		} catch (SGRLServiceException_Exception e) {
			logger.error("SgrlRoute console", e);
			fail();
		}
		assertEquals(routes.size(), 0);
	}
}
