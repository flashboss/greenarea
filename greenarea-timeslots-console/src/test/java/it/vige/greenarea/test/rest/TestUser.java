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

import static it.vige.greenarea.Constants.BASE_URI_USER;
import static it.vige.greenarea.dto.StatoVeicolo.MAINTAINANCE;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.RichiestaVeicolo;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.Veicolo;

import java.sql.Timestamp;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.junit.Test;
import org.slf4j.Logger;

public class TestUser {

	private Logger logger = getLogger(getClass());

	@Test
	public void testGetInfoRequest() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/getInfoRequest/3")
				.request(APPLICATION_JSON);
		Request response = bldr.get(Request.class);
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testAddMission() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/addMission").request(
				APPLICATION_JSON);
		Mission mission = new Mission();
		mission.setAddressList("nuovo indirizzo");
		mission.setCompany("nuova compagnia");
		mission.setStartTime(new Timestamp(342423423));
		mission.setTimeSlot(new TimeSlot(24234));
		mission.setTruck(new Vehicle("mio veicolo"));
		mission.setName("missione ino");
		mission.setResVikor(322.44);
		ValueMission lunghezza = new ValueMission();
		lunghezza.setIdParameter(6);
		lunghezza.setValuePar(22.8);
		ValueMission peso = new ValueMission();
		peso.setIdParameter(8);
		peso.setValuePar(11.8);
		mission.setValuesMission(asList(new ValueMission[] { lunghezza, peso }));
		Mission response = bldr.post(entity(mission, APPLICATION_JSON),
				Mission.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindAllTimeSlots() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/findAllTimeSlot")
				.request(APPLICATION_JSON);
		List<TimeSlot> response = bldr.get(new GenericType<List<TimeSlot>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testGetAllVehicles() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/getVehicles").request(
				APPLICATION_JSON);
		List<Vehicle> response = bldr.get(new GenericType<List<Vehicle>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindVehiclesWithNoValue() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/findVehicles").request(
				APPLICATION_JSON);
		Veicolo veicolo = new Veicolo();
		List<Veicolo> response = bldr.post(entity(veicolo, APPLICATION_JSON),
				new GenericType<List<Veicolo>>() {
				});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindVehiclesWithAutista() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/findVehicles").request(
				APPLICATION_JSON);
		Veicolo veicolo = new Veicolo();
		veicolo.setAutista(new GreenareaUser("autista2"));
		List<Veicolo> response = bldr.post(entity(veicolo, APPLICATION_JSON),
				new GenericType<List<Veicolo>>() {
				});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindVehiclesWithVin() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/findVinVehicles")
				.request(APPLICATION_JSON);
		RichiestaVeicolo veicolo = new RichiestaVeicolo();
		veicolo.setAutista(new GreenareaUser("autista2"));
		List<Veicolo> response = bldr.post(entity(veicolo, APPLICATION_JSON),
				new GenericType<List<Veicolo>>() {
				});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindVehiclesWithAllFields() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/findVehicles").request(
				APPLICATION_JSON);
		Veicolo veicolo = new Veicolo();
		veicolo.setAutista(new GreenareaUser("autista2"));
		veicolo.setSocietaDiTrasporto(new GreenareaUser("buscar"));
		veicolo.setOperatoreLogistico(new OperatoreLogistico(new GreenareaUser(
				"dhl")));
		List<Veicolo> response = bldr.post(entity(veicolo, APPLICATION_JSON),
				new GenericType<List<Veicolo>>() {
				});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testGetVehiclesForOP() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/getVehiclesForOP/tnt")
				.request(APPLICATION_JSON);
		List<Vehicle> response = bldr.get(new GenericType<List<Vehicle>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testAggiornaStatoVeicolo() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/aggiornaStatoVeicolo")
				.request(APPLICATION_JSON);
		Veicolo veicolo = new Veicolo();
		veicolo.setTarga("91GTK");
		veicolo.setStato(MAINTAINANCE.name());
		Veicolo response = bldr.post(entity(veicolo, APPLICATION_JSON),
				Veicolo.class);
		assertNotNull(response);

		logger.info(response + "");
	}

}
