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
package it.vige.greenarea.cl.enforcement.rest;

import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import it.vige.greenarea.cl.bean.Request;

/**
 * Jersey REST client generated for REST resource:EnforcementRESTService
 * [/Enforcement]<br>
 * USAGE:
 * 
 * <pre>
 *        RestClient client = new RestClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 * 
 */
public class RestClient {
	private Client client;
	private static final String BASE_URI = "http://localhost:8080/greenarea-service/resources";

	public RestClient() {
		client = newClient();
	}

	public Request getInfoRequest(String idTimeSlot, String idVehicle) throws Exception {
		Invocation.Builder bldr = client
				.target(BASE_URI + "/Enforcement/getInfoRequest/" + idTimeSlot + "/" + idVehicle)
				.request(APPLICATION_JSON);
		return bldr.get(Request.class);
	}

	public void close() {
		client.close();
	}

}
