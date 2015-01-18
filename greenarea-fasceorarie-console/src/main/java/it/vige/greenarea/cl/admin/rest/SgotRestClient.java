/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.admin.rest;

import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import it.vige.greenarea.cl.library.entities.ShippingItem;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.TransportServiceClass;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

public class SgotRestClient {
	private Client client;
	private static final String BASE_URI = "http://localhost:8080/greenarea-service/resources/TimeSlot";

	public SgotRestClient() {
		client = newClient();
	}

	public List<TransportServiceClass> findTransportServiceClass(
			String description) throws Exception {
		Client client = newClient();
		Builder bldr = client.target(
				BASE_URI + "/findTransportServiceClass/" + description)
				.request(APPLICATION_JSON);
		return bldr.get(new GenericType<List<TransportServiceClass>>() {
		});
	}

	public String addShipping(ShippingOrder shippingOrder) throws Exception {
		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/addShipping").request(
				APPLICATION_JSON);
		return bldr.post(entity(shippingOrder, APPLICATION_JSON), String.class);
	}

	public String addShippingItem(ShippingItem shippingItem) throws Exception {
		Client client = newClient();
		Builder bldr = client.target(BASE_URI + "/addShippingItem").request(
				APPLICATION_JSON);
		return bldr.post(entity(shippingItem, APPLICATION_JSON), String.class);
	}

	public void close() {
		client.close();
	}

}
