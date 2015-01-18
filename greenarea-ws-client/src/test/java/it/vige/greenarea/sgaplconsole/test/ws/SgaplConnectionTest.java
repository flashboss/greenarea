package it.vige.greenarea.sgaplconsole.test.ws;

import static org.junit.Assert.assertNull;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.sgapl.sgot.webservice.SGOTadminService;
import it.vige.greenarea.sgapl.sgot.webservice.SGOTadminService_Service;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderManager_Service;
import it.vige.greenarea.sgrl.webservices.GeoLocation;
import it.vige.greenarea.sgrl.webservices.LogisticNetworkRouting;
import it.vige.greenarea.sgrl.webservices.LogisticNetworkRouting_Service;
import it.vige.greenarea.sgrl.webservices.SGRLServiceException_Exception;
import it.vige.greenarea.sgrl.webservices.SgrlRoute;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;

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
		ShippingOrderManager shippingOrderManager = service
				.getShippingOrderManagerPort();
		shippingOrderManager.getTrackingUrl("44L");
	}

	@Test
	public void testLogisticNetworkRoutingService() {
		LogisticNetworkRouting_Service service = new LogisticNetworkRouting_Service();
		LogisticNetworkRouting logisticNetworkRouting = service
				.getLogisticNetworkRoutingPort();
		GeoLocation source = new GeoLocation();
		GeoLocation destination = new GeoLocation();
		List<SgrlRoute> routes = null;
		try {
			routes = logisticNetworkRouting.getSGRLRoutes(source, destination,
					null);
		} catch (SGRLServiceException_Exception e) {
			logger.error("missioni console", e);
		}
		assertNull(routes);
	}
}
