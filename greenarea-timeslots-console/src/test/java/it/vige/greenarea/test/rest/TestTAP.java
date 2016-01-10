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

import static it.vige.greenarea.Constants.BASE_URI_TAP;
import static it.vige.greenarea.Conversioni.addDays;
import static it.vige.greenarea.Utilities.yyyyMMddDash;
import static it.vige.greenarea.dto.Selezione.TUTTI;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.slf4j.LoggerFactory.getLogger;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.TapGroupData;
import it.vige.greenarea.cl.library.entities.TapOutData;
import it.vige.greenarea.cl.library.entities.TapParamData;
import it.vige.greenarea.dto.AccessiInGA;
import it.vige.greenarea.dto.RichiestaAccesso;
import it.vige.greenarea.dto.RichiestaPosizioneVeicolo;

public class TestTAP {

	private Logger logger = getLogger(getClass());

	@Test
	public void testGetTapOutDatas() {

		Client client = newClient();
		removeTapData(client);
		move2(client);
		Builder bldr = client.target(BASE_URI_TAP + "/getTapOutDatas/75655").request(APPLICATION_JSON);
		List<TapOutData> response = bldr.get(new GenericType<List<TapOutData>>() {
		});
		assertNotNull(response);

		removeTapData(client);
		logger.info(response + "");
	}

	@Test
	public void testGetTapParamsDatas() {

		Client client = newClient();
		removeTapData(client);
		move2(client);
		TapGroupData tapGroupData = new TapGroupData();
		Builder bldr = client.target(BASE_URI_TAP + "/getTapParamDatas").request(APPLICATION_JSON);
		List<TapParamData> response = bldr.post(entity(tapGroupData, APPLICATION_JSON),
				new GenericType<List<TapParamData>>() {
				});
		assertNotNull(response);

		removeTapData(client);
		logger.info(response + "");
	}

	@Test
	public void testFindAllTapParamsData() {

		Client client = newClient();
		removeTapData(client);
		move2(client);
		Builder bldr = client.target(BASE_URI_TAP + "/getAllTapParams").request(APPLICATION_JSON);
		List<TapParamData> response = bldr.get(new GenericType<List<TapParamData>>() {
		});
		assertNotNull(response);

		removeTapData(client);
		logger.info(response + "");
	}

	@Test
	public void testFindAllTapOutsData() {

		Client client = newClient();
		removeTapData(client);
		move2(client);
		Builder bldr = client.target(BASE_URI_TAP + "/getAllTapOuts").request(APPLICATION_JSON);
		List<TapOutData> response = bldr.get(new GenericType<List<TapOutData>>() {
		});
		assertNotNull(response);

		removeTapData(client);
		logger.info(response + "");
	}

	@Test
	public void testFindAllTapGroupsData() {

		Client client = newClient();
		removeTapData(client);
		move2(client);
		Builder bldr = client.target(BASE_URI_TAP + "/getAllTapGroups").request(APPLICATION_JSON);
		List<TapGroupData> response = bldr.get(new GenericType<List<TapGroupData>>() {
		});
		assertNotNull(response);

		removeTapData(client);
		logger.info(response + "");
	}

	@Test
	public void testFindVeicoliInGA() {

		Client client = newClient();
		removeTapData(client);
		move1(client);
		Builder bldr = client.target(BASE_URI_TAP + "/veicoliInGA").request(APPLICATION_JSON);
		int response = bldr.get(Integer.class);
		assertNotNull(response);
		assertEquals(response, 1);
		move2(client);
		bldr = client.target(BASE_URI_TAP + "/veicoliInGA").request(APPLICATION_JSON);
		response = bldr.get(Integer.class);
		assertNotNull(response);
		assertEquals(response, 1);

		removeTapData(client);
		logger.info(response + "");
	}

	@Test
	public void testRichiediPosizioneVeicolo() {

		Client client = newClient();
		removeTapData(client);
		move1(client);
		Builder bldr = client.target(BASE_URI_TAP + "/getLastPosition").request(APPLICATION_JSON);
		RichiestaPosizioneVeicolo richiestaPosizioneVeicolo = new RichiestaPosizioneVeicolo();
		richiestaPosizioneVeicolo.setTarga("EL818YP");
		String response = bldr.post(entity(richiestaPosizioneVeicolo, APPLICATION_JSON), String.class);
		assertNotNull(response);
		assertEquals(response, "7.68086,45.065353888888886");
		move2(client);
		bldr = client.target(BASE_URI_TAP + "/getLastPosition").request(APPLICATION_JSON);
		richiestaPosizioneVeicolo = new RichiestaPosizioneVeicolo();
		richiestaPosizioneVeicolo.setTarga("EL818YP");
		response = bldr.post(entity(richiestaPosizioneVeicolo, APPLICATION_JSON), String.class);
		assertNotNull(response);
		assertEquals(response, "7.680926388888889,45.06603583333333");

		removeTapData(client);
		logger.info(response + "");
	}

	@Test
	public void testStoricoAccessiInGA() {

		Client client = newClient();
		RichiestaAccesso richiestaAccesso = new RichiestaAccesso();
		try {
			richiestaAccesso.setDataInizio(yyyyMMddDash.parse("2014-12-17 10:58:00"));
		} catch (ParseException e1) {
			fail();
		}
		Date today = new Date();
		richiestaAccesso.setDataFine(addDays(today, 1));
		richiestaAccesso.setOperatoriLogistici(asList(new String[] { TUTTI.name() }));
		removeTapData(client);
		move1(client);
		Builder bldr = client.target(BASE_URI_TAP + "/storicoAccessiInGA").request(APPLICATION_JSON);
		Map<Date, AccessiInGA> response = bldr.post(entity(richiestaAccesso, APPLICATION_JSON),
				new GenericType<Map<Date, AccessiInGA>>() {
				});
		assertNotNull(response);
		assertEquals(response.size(), 7);
		try {
			AccessiInGA accessiInGA = response.get(yyyyMMddDash.parse("2014-12-19 10:58:00"));
			assertEquals(accessiInGA.getAccessi(), 1);
			assertEquals(accessiInGA.getKm(), 0.0, 0);
			assertEquals(accessiInGA.getTempoTrascorso(), 0);
			accessiInGA = response.get(yyyyMMddDash.parse("2014-12-19 10:58:27"));
			assertEquals(accessiInGA.getAccessi(), 0);
			assertEquals(accessiInGA.getKm(), 4.2454313331053627E-4, 0);
			assertEquals(accessiInGA.getTempoTrascorso(), 27000);
		} catch (ParseException e) {
			fail();
		}
		move2(client);
		bldr = client.target(BASE_URI_TAP + "/storicoAccessiInGA").request(APPLICATION_JSON);
		response = bldr.post(entity(richiestaAccesso, APPLICATION_JSON), new GenericType<Map<Date, AccessiInGA>>() {
		});
		assertNotNull(response);
		assertEquals(response.size(), 12);
		try {
			AccessiInGA accessiInGA = response.get(yyyyMMddDash.parse("2014-12-19 10:58:00"));
			assertEquals(accessiInGA.getAccessi(), 1);
			assertEquals(accessiInGA.getKm(), 0.0, 0);
			assertEquals(accessiInGA.getTempoTrascorso(), 0);
			accessiInGA = response.get(yyyyMMddDash.parse("2014-12-20 11:01:46"));
			assertEquals(accessiInGA.getAccessi(), 0);
			assertEquals(accessiInGA.getKm(), 0.0723958705535196, 0);
			assertEquals(accessiInGA.getTempoTrascorso(), 64000);
		} catch (ParseException e) {
			fail();
		}

		removeTapData(client);
		logger.info(response + "");
	}

	@Test
	public void testStoricoAccessiInGAConOP() {

		Client client = newClient();
		RichiestaAccesso richiestaAccesso = new RichiestaAccesso();
		try {
			richiestaAccesso.setDataInizio(yyyyMMddDash.parse("2014-12-17 10:58:00"));
		} catch (ParseException e1) {
			fail();
		}
		Date today = new Date();
		richiestaAccesso.setDataFine(addDays(today, 1));
		richiestaAccesso.setOperatoriLogistici(asList(new String[] { "tnt" }));
		removeTapData(client);
		move1(client);
		Builder bldr = client.target(BASE_URI_TAP + "/storicoAccessiInGA").request(APPLICATION_JSON);
		Map<Date, AccessiInGA> response = bldr.post(entity(richiestaAccesso, APPLICATION_JSON),
				new GenericType<Map<Date, AccessiInGA>>() {
				});
		assertNotNull(response);
		assertEquals(response.size(), 7);
		try {
			AccessiInGA accessiInGA = response.get(yyyyMMddDash.parse("2014-12-19 10:58:00"));
			assertEquals(accessiInGA.getAccessi(), 1);
			assertEquals(accessiInGA.getKm(), 0.0, 0);
			assertEquals(accessiInGA.getTempoTrascorso(), 0);
			accessiInGA = response.get(yyyyMMddDash.parse("2014-12-19 10:58:27"));
			assertEquals(accessiInGA.getAccessi(), 0);
			assertEquals(accessiInGA.getKm(), 4.2454313331053627E-4, 0);
			assertEquals(accessiInGA.getTempoTrascorso(), 27000);
		} catch (ParseException e) {
			fail();
		}
		move2(client);
		bldr = client.target(BASE_URI_TAP + "/storicoAccessiInGA").request(APPLICATION_JSON);
		response = bldr.post(entity(richiestaAccesso, APPLICATION_JSON), new GenericType<Map<Date, AccessiInGA>>() {
		});
		assertNotNull(response);
		assertEquals(response.size(), 12);
		try {
			AccessiInGA accessiInGA = response.get(yyyyMMddDash.parse("2014-12-19 10:58:00"));
			assertEquals(accessiInGA.getAccessi(), 1);
			assertEquals(accessiInGA.getKm(), 0.0, 0);
			assertEquals(accessiInGA.getTempoTrascorso(), 0);
			accessiInGA = response.get(yyyyMMddDash.parse("2014-12-20 11:01:46"));
			assertEquals(accessiInGA.getAccessi(), 0);
			assertEquals(accessiInGA.getKm(), 0.0723958705535196, 0);
			assertEquals(accessiInGA.getTempoTrascorso(), 64000);
		} catch (ParseException e) {
			fail();
		}
		removeTapData(client);
		logger.info(response + "");
	}

	private void move1(Client client) {
		Builder bldr = client.target(BASE_URI_TAP + "/spostamento1").request(APPLICATION_JSON);
		Response response = bldr.get();
		assertNotNull(response);
	}

	private void move2(Client client) {
		Builder bldr = client.target(BASE_URI_TAP + "/spostamento2").request(APPLICATION_JSON);
		Response response = bldr.get();
		assertNotNull(response);
	}

	private void removeTapData(Client client) {
		Builder bldr = client.target(BASE_URI_TAP + "/cancellaDatiTap").request(APPLICATION_JSON);
		Response response = bldr.get();
		assertNotNull(response);
	}
}
