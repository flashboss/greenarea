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
package it.vige.greenarea.cl.admin.rest;

import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;

import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.bean.TimeSlotInfo;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.cl.library.entities.TsStat;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.dto.Sched;

public class TimeSlotRestClient {
	private Client client;
	private static final String BASE_URI = "http://localhost:8080/greenarea-service/resources/TimeSlot";

	public TimeSlotRestClient() {
		client = newClient();
	}

	public List<ParameterGen> findAllParameterGen() throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/findAllParameterGen").request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<ParameterGen>>() {
		});
	}

	public List<ParameterGen> findAllParameterGenAvailable() throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/findAllParameterGenAvailable").request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<ParameterGen>>() {
		});
	}

	public List<TimeSlot> findAllTimeSlots() throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/findAllTimeSlot").request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<TimeSlot>>() {
		});
	}

	public List<TsStat> getAllTsStats() throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/getAllTsStats").request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<TsStat>>() {
		});
	}

	public List<Sched> getAllSchedules() throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/getAllSchedules").request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<Sched>>() {
		});
	}

	public List<Request> selectRequests(Date dateMiss, String idTimeSlot, String typePG) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/requests/" + dateMiss + "/" + idTimeSlot + "/" + typePG)
				.request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<Request>>() {
		});
	}

	public List<Request> simulRank(String idTimeSlot, Date dateMiss) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/simulRank/" + idTimeSlot + "/" + dateMiss)
				.request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<Request>>() {
		});
	}

	public List<ParameterTS> findTimeSlot(String idTimeSlot) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/findTimeSlot/" + idTimeSlot).request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<ParameterTS>>() {
		});
	}

	public List<Request> getStoryBoard(String idTimeSlot, Date dateMiss) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/getStoryBoard/" + idTimeSlot + "/" + dateMiss)
				.request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<Request>>() {
		});
	}

	public List<ParameterTS> findParameterOfTimeSlot(String idTimeSlot) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/findParameterOfTimeSlot/" + idTimeSlot)
				.request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<ParameterTS>>() {
		});
	}

	public List<Price> getPriceOfTimeSlot(String idTimeSlot) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/getPriceOfTimeSlot/" + idTimeSlot)
				.request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<Price>>() {
		});
	}

	public ParameterTS configParameterTsToTimeSlot(Object requestEntity) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/configParameterTS").request(APPLICATION_JSON);
		return bldr.post(entity(requestEntity, APPLICATION_JSON), ParameterTS.class);
	}

	public ParameterTS getParameterOfTimeSlot(String idTimeSlot) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/getParameterForRank/" + idTimeSlot)
				.request(APPLICATION_JSON);
		return bldr.get(ParameterTS.class);
	}

	public List<Request> getrRank(String idTimeSlot, Date dateMiss) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/getRank/" + idTimeSlot + "/" + dateMiss)
				.request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<Request>>() {
		});
	}

	public TimeSlotInfo getInfoTimeSlot(String idTimeSlot) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/getInfoTimeSlot/" + idTimeSlot).request(APPLICATION_JSON);
		return bldr.get(TimeSlotInfo.class);
	}

	public ParameterGen updateParameterGen(ParameterGen requestEntity) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/updateParameterGen").request(APPLICATION_JSON);
		return bldr.post(entity(requestEntity, APPLICATION_JSON), ParameterGen.class);
	}

	public List<Vehicle> getAllVehicles() throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/getVehicles").request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<Vehicle>>() {
		});
	}

	public ParameterGen addParameterGen(Object requestEntity) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/addParameterGen").request(APPLICATION_JSON);
		return bldr.post(entity(requestEntity, APPLICATION_JSON), ParameterGen.class);
	}

	public TimeSlot findTS(String idTimeSlot) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/findTimeSlot/" + idTimeSlot).request(APPLICATION_JSON);
		return bldr.get(TimeSlot.class);
	}

	public Price addPrices(Object requestEntity) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/addPrice").request(APPLICATION_JSON);
		return bldr.post(entity(requestEntity, APPLICATION_JSON), Price.class);
	}

	public TimeSlot addTimeSlot(Object requestEntity) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/addTimeSlot").request(APPLICATION_JSON);
		return bldr.post(entity(requestEntity, APPLICATION_JSON), TimeSlot.class);
	}

	public String simulSch(Transport transport) throws Exception {
		Client client = newClient();
		Invocation.Builder bldr = client.target(BASE_URI + "/startScheduler").request(APPLICATION_JSON);
		return bldr.post(entity(transport, APPLICATION_JSON), String.class);
	}

	public void close() {
		client.close();
	}

}
