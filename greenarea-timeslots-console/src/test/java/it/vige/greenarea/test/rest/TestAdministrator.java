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
package it.vige.greenarea.test.rest;

import static it.vige.greenarea.Constants.BASE_URI_ADMINISTRATOR;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.Filter;

public class TestAdministrator {

	private Logger logger = getLogger(getClass());

	@Before
	public void init() {
		addFilter();
	}

	@After
	public void close() {
		deleteFilter();
	}
	
	private void addFilter() {

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

	private void deleteFilter() {

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
