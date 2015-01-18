/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
