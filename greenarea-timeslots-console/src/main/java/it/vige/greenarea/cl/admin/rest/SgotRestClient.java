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

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import it.vige.greenarea.cl.library.entities.ShippingItem;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.TransportServiceClass;

public class SgotRestClient {
	private Client client;
	private static final String BASE_URI = "http://localhost:8080/greenarea-service/resources/TimeSlot";

	public SgotRestClient() {
		client = newClient();
	}

	public List<TransportServiceClass> findTransportServiceClass(String description) throws Exception {
		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/findTransportServiceClass/" + description).request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<TransportServiceClass>>() {
		});
	}

	public String addShipping(ShippingOrder shippingOrder) throws Exception {
		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/addShipping").request(APPLICATION_JSON);
		return bldr.post(entity(shippingOrder, APPLICATION_JSON), String.class);
	}

	public String addShippingItem(ShippingItem shippingItem) throws Exception {
		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/addShippingItem").request(APPLICATION_JSON);
		return bldr.post(entity(shippingItem, APPLICATION_JSON), String.class);
	}

	public void close() {
		client.close();
	}

}
