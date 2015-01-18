/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.enforcement.rest;

import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import it.vige.greenarea.cl.bean.Request;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

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

	public Request getInfoRequest(String idTimeSlot, String idVehicle)
			throws Exception {
		Invocation.Builder bldr = client.target(
				BASE_URI + "/Enforcement/getInfoRequest/" + idTimeSlot + "/"
						+ idVehicle).request(APPLICATION_JSON);
		return bldr.get(Request.class);
	}

	public void close() {
		client.close();
	}

}
