package it.vige.greenarea.gtg.test.ws;

import static java.util.Collections.singletonList;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static javax.xml.ws.handler.MessageContext.HTTP_REQUEST_HEADERS;
import static org.junit.Assert.fail;
import static org.primefaces.util.Base64.encodeToString;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.gtg.webservice.FreightAction;
import it.vige.greenarea.gtg.webservice.GTGexception_Exception;
import it.vige.greenarea.gtg.webservice.GTGservice;
import it.vige.greenarea.gtg.webservice.GTGservice_Service;
import it.vige.greenarea.gtg.webservice.LDAPException_Exception;
import it.vige.greenarea.gtg.webservice.Mission;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.junit.Test;
import org.slf4j.Logger;

public class MissioniTest {

	private Logger logger = getLogger(getClass());

	@Test
	public void testMissioni() {
		GTGservice_Service service = new GTGservice_Service();
		GTGservice gtgService = service.getGTGservicePort();

		BindingProvider provider = (BindingProvider) gtgService;
		Map<String, Object> req_ctx = provider.getRequestContext();
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String credenziale = "prova:prova";
		String credenzialeCriptata = encodeToString(credenziale.getBytes(),
				false);
		headers.put("Authorization", singletonList("_____"
				+ credenzialeCriptata));
		req_ctx.put(HTTP_REQUEST_HEADERS, headers);
		/*
		 * req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
		 * "http://82.107.53.122/greenarea-service/GTGservice");
		 */
		/*
		 * switch a localhost
		 */

		req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
				"http://localhost:8080/greenarea-service/GTGservice");

		try {
			DateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmm");
			gtgService.userLogin();
			List<Mission> missioni = gtgService.getMissions(dataFormat
					.format(new Date()));
			logger.info("missioni = " + missioni);
		} catch (LDAPException_Exception e) {
			logger.error("missioni console", e);
			fail();
		}
	}

	@Test
	public void testCambioStatoRichiesta() {
		GTGservice_Service service = new GTGservice_Service();
		GTGservice gtgService = service.getGTGservicePort();

		BindingProvider provider = (BindingProvider) gtgService;
		Map<String, Object> req_ctx = provider.getRequestContext();
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String credenziale = "prova:prova";
		String credenzialeCriptata = encodeToString(credenziale.getBytes(),
				false);
		headers.put("Authorization", singletonList("_____"
				+ credenzialeCriptata));
		req_ctx.put(HTTP_REQUEST_HEADERS, headers);
		/*
		 * req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
		 * "http://82.107.53.122/greenarea-service/GTGservice");
		 */
		/*
		 * switch a localhost
		 */

		req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
				"http://localhost:8080/greenarea-service/GTGservice");

		try {
			DateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmm");
			gtgService.userLogin();
			List<Mission> missioni = gtgService.getMissions(dataFormat
					.format(new Date()));
			logger.info("missioni = " + missioni);
			for (Mission mission : missioni)
				try {
					gtgService.changeMissionState(mission.getId(), 1, 2,
							"cosà", dataFormat.format(new Date()));
				} catch (GTGexception_Exception e) {
					fail();
				}
		} catch (LDAPException_Exception e) {
			logger.error("missioni console", e);
			fail();
		}
	}

	@Test
	public void testNotifyFreightItemAction() {
		GTGservice_Service service = new GTGservice_Service();
		GTGservice gtgService = service.getGTGservicePort();

		BindingProvider provider = (BindingProvider) gtgService;
		Map<String, Object> req_ctx = provider.getRequestContext();
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String credenziale = "prova:prova";
		String credenzialeCriptata = encodeToString(credenziale.getBytes(),
				false);
		headers.put("Authorization", singletonList("_____"
				+ credenzialeCriptata));
		req_ctx.put(HTTP_REQUEST_HEADERS, headers);
		/*
		 * req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
		 * "http://82.107.53.122/greenarea-service/GTGservice");
		 */
		/*
		 * switch a localhost
		 */

		req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
				"http://localhost:8080/greenarea-service/GTGservice");

		try {
			DateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmm");
			gtgService.userLogin();
			List<Mission> missioni = gtgService.getMissions(dataFormat
					.format(new Date()));
			logger.info("missioni = " + missioni);
			List<FreightAction> freightItemsAction = new ArrayList<FreightAction>();
			FreightAction freightAction = new FreightAction();
			freightAction.setCause(2);
			freightAction.setDateTime(dataFormat.format(new Date()));
			freightAction.setState(1);
			freightAction.setNote("prova");
			freightAction.setExchangeStopId(missioni.get(0).getExchangeStops()
					.get(0).getId());
			freightAction.setCode(missioni.get(0).getFreights().get(0)
					.getCode());
			freightItemsAction.add(freightAction);
			try {
				gtgService.notifyFreightItemAction(freightItemsAction);
			} catch (GTGexception_Exception e) {
				fail();
			}
		} catch (LDAPException_Exception e) {
			logger.error("missioni console", e);
			fail();
		}
	}
}
