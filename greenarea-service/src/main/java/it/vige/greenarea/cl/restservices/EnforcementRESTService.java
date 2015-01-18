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
package it.vige.greenarea.cl.restservices;

import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.control.EnforcementControl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * 
 */
@Path("/Enforcement")
@Stateless
public class EnforcementRESTService {
	@EJB
	private EnforcementControl ec;

	@GET
	@Path("/getInfoRequest/{idTimeSlot}/{idVehicle}")
	@Produces(MediaType.APPLICATION_JSON)
	public Request getInfoRequest(@PathParam("idTimeSlot") int idTimeSlot,
			@PathParam("idVehicle") String idVehicle) {
		return ec.getInfoRequest(idTimeSlot, idVehicle);
	}
}
