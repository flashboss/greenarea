package it.vige.greenarea.test.rest;

import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.bean.Request;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;

import org.junit.Test;
import org.slf4j.Logger;

public class TestEnforcement {

	private static final String BASE_URI = "http://localhost:8080/greenarea-service/resources/Enforcement";

	private Logger logger = getLogger(getClass());

	@Test
	public void testGetInfoRequest() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/getInfoRequest/3/4").request(
				APPLICATION_JSON);
		Request response = bldr.get(Request.class);
		assertNotNull(response);

		logger.info(response + "");

	}

}
