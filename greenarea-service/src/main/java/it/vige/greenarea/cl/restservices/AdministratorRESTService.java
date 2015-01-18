/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.restservices;

import it.vige.greenarea.cl.library.entities.Filter;
import it.vige.greenarea.sgapl.sgot.facade.FilterFacade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * <p>
 * Class: UserRESTService
 * </p>
 * <p>
 * Description: Classe che gestisce l'utilizzo parte utente
 * </p>
 * 
 */
@Path("/Administrator")
@Stateless
public class AdministratorRESTService {

	@EJB
	private FilterFacade ff;

	/**
	 * <p>
	 * Method: getFilters
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce i filtri utilizzabili da un
	 * operatore logistico
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/getFiltersForOP/{operatoreLogistico}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Filter> getFilters(
			@PathParam("operatoreLogistico") String operatoreLogistico) {
		return ff.findAll(operatoreLogistico);
	}

	/**
	 * <p>
	 * Method: getFilters
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce i filtri utilizzabili da un
	 * operatore logistico
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/getFilters")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Filter> getFilters() {
		return ff.findAll();
	}

	/**
	 * <p>
	 * Method: addFilter
	 * </p>
	 * <p>
	 * Description: Questo metodo inserisce un Filter nel sistema
	 * </p>
	 * 
	 * @param Filter
	 *            filterToAdd
	 * @return Filter
	 */
	@POST
	@Path("/addFilter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Filter addFilter(Filter filterToAdd) {
		ff.create(filterToAdd);
		return filterToAdd;

	}

	/**
	 * <p>
	 * Method: deleteFilter
	 * </p>
	 * <p>
	 * Description: Questo metodo cancella un Filter dal sistema
	 * </p>
	 * 
	 * @param Filter
	 *            filterToDelete
	 * @return Filter
	 */
	@POST
	@Path("/deleteFilter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Filter deleteFilter(Filter filterToDelete) {
		ff.remove(filterToDelete);
		return filterToDelete;

	}
}
