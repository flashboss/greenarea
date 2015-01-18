package it.vige.greenarea.test.rest;

import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.junit.Assert.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;

import org.junit.Test;
import org.slf4j.Logger;

public class TestDataBase {

	private static final String BASE_URI = "http://localhost:8080/greenarea-service/resources/DataBase";

	private Logger logger = getLogger(getClass());

	@Test
	public void testPopulateDB() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/populateDB").request(
				TEXT_PLAIN);
		String response = bldr.get(String.class);
		assertNotNull(response);

		logger.info(response);

	}

	@Test
	public void testRetHello() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/hello").request(TEXT_PLAIN);
		String response = bldr.get(String.class);
		assertNotNull(response);

		logger.info(response);

	}

	@Test
	public void testAddMission() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/addMission").request(
				TEXT_PLAIN);
		String response = bldr.get(String.class);
		assertNotNull(response);

		logger.info(response);

	}
}
