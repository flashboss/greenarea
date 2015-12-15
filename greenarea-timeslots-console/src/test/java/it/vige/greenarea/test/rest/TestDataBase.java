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

import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.junit.Assert.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class TestDataBase {

	private static final String BASE_URI = "http://localhost:8080/greenarea-service/resources/DataBase";

	private Logger logger = getLogger(getClass());

	@Before
	public void init() {
		populateDB();
		addMission();
	}

	@After
	public void close() {
		removeMission();
		removeDB();
	}

	private void populateDB() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/populateDB").request(TEXT_PLAIN);
		String response = bldr.get(String.class);
		assertNotNull(response);

		logger.info(response);

	}

	private void removeDB() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/removeDB").request(TEXT_PLAIN);
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

	private void addMission() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/addMission").request(TEXT_PLAIN);
		String response = bldr.get(String.class);
		assertNotNull(response);

		logger.info(response);

	}

	private void removeMission() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/removeMission").request(TEXT_PLAIN);
		String response = bldr.get(String.class);
		assertNotNull(response);

		logger.info(response);

	}
}
