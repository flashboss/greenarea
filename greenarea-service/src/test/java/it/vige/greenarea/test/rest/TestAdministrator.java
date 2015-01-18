package it.vige.greenarea.test.rest;

import static it.vige.greenarea.Constants.BASE_URI_ADMINISTRATOR;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.Filter;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.junit.Test;
import org.slf4j.Logger;

public class TestAdministrator {

	private Logger logger = getLogger(getClass());

	@Test
	public void testAddFilter() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_ADMINISTRATOR + "/addFilter")
				.request(APPLICATION_JSON);
		Filter filter = new Filter("08", "dhl");
		Filter response = bldr.post(entity(filter, APPLICATION_JSON),
				Filter.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindAllFiltersForOP() {

		Client client = newClient();
		Builder bldr = client.target(
				BASE_URI_ADMINISTRATOR + "/getFiltersForOP/dhl").request(
				APPLICATION_JSON);
		List<Filter> response = bldr.get(new GenericType<List<Filter>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindAllFilters() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_ADMINISTRATOR + "/getFilters")
				.request(APPLICATION_JSON);
		List<Filter> response = bldr.get(new GenericType<List<Filter>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testDeleteFilter() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_ADMINISTRATOR + "/deleteFilter")
				.request(APPLICATION_JSON);
		Filter filter = new Filter("08", "dhl");
		Filter response = bldr.post(entity(filter, APPLICATION_JSON),
				Filter.class);
		assertNotNull(response);

		logger.info(response + "");
	}

}
