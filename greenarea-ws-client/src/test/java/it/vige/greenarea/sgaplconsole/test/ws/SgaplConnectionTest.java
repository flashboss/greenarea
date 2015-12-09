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

import static org.junit.Assert.assertNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;

import it.vige.greenarea.sgapl.sgot.webservice.SGOTadminService;
import it.vige.greenarea.sgapl.sgot.webservice.SGOTadminService_Service;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager_Service;
import it.vige.greenarea.sgrl.webservices.GeoLocation;
import it.vige.greenarea.sgrl.webservices.LogisticNetworkRouting;
import it.vige.greenarea.sgrl.webservices.LogisticNetworkRouting_Service;
import it.vige.greenarea.sgrl.webservices.SGRLServiceException_Exception;
import it.vige.greenarea.sgrl.webservices.SgrlRoute;

public class SgaplConnectionTest {

	private Logger logger = getLogger(getClass());

	@Test
	public void testSGOTadminService() {
		SGOTadminService_Service service = new SGOTadminService_Service();
		SGOTadminService sGOTadminService = service.getSGOTadminServicePort();
		sGOTadminService.showOrderDetails("3L");
	}

	@Test
	public void testShippingOrderManagerService() {
		ShippingOrderManager_Service service = new ShippingOrderManager_Service();
		ShippingOrderManager shippingOrderManager = service.getShippingOrderManagerPort();
		shippingOrderManager.getTrackingUrl("44L");
	}

	@Test
	public void testLogisticNetworkRoutingService() {
		LogisticNetworkRouting_Service service = new LogisticNetworkRouting_Service();
		LogisticNetworkRouting logisticNetworkRouting = service.getLogisticNetworkRoutingPort();
		GeoLocation source = new GeoLocation();
		GeoLocation destination = new GeoLocation();
		List<SgrlRoute> routes = null;
		try {
			routes = logisticNetworkRouting.getSGRLRoutes(source, destination, null);
		} catch (SGRLServiceException_Exception e) {
			logger.error("missioni console", e);
		}
		assertNull(routes);
	}
}
