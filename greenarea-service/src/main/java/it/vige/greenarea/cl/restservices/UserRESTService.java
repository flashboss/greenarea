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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.control.TimeSlotControl;
import it.vige.greenarea.cl.control.UserControl;
import it.vige.greenarea.cl.control.VehicleControl;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.TruckServiceClass;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.dto.RichiestaVeicolo;
import it.vige.greenarea.dto.Veicolo;

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
@Path("/User")
@Stateless
public class UserRESTService {

	@EJB
	private UserControl uc;
	@EJB
	private TimeSlotControl tsc;
	@EJB
	private VehicleControl vc;

	/**
	 * <p>
	 * Method: getInfoRequest
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce informazioni riguardo una
	 * richiesta processata
	 * </p>
	 * 
	 * @param int idMission
	 * @return Request
	 */
	@GET
	@Path("/getInfoRequest/{idMission}")
	@Produces(MediaType.APPLICATION_JSON)
	public Request getInfoRequest(@PathParam("idMission") int idMission) {
		return uc.getInfoRequest(idMission);

	}

	/**
	 * <p>
	 * Method: addValueMission
	 * </p>
	 * <p>
	 * Description: Questo metodo aggiunge un valore missione al sistema lato utente
	 * </p>
	 * 
	 * @param ValueMission
	 *            m
	 * @return ValueMission
	 */
	@POST
	@Path("/addValueMission")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ValueMission addValueMission(ValueMission m) {
		return uc.addValueMission(m);
	}

	/**
	 * <p>
	 * Method: addMission
	 * </p>
	 * <p>
	 * Description: Questo metodo aggiunge una missione al sistema lato utente
	 * </p>
	 * 
	 * @param Mission
	 *            m
	 * @return Mission
	 */
	@POST
	@Path("/addMission")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Mission addMission(Mission m) {
		return uc.addMission(m);
	}

	/**
	 * <p>
	 * Method: findAllTimeSlots
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce tutte le fasce orarie del sistema
	 * </p>
	 * 
	 * @return List<TimeSlot>
	 */
	@GET
	@Path("/findAllTimeSlot")
	@Produces({ "application/json" })
	public List<TimeSlot> findAllTimeSlots() {
		return tsc.findAllTimeSlots();
	}

	/**
	 * <p>
	 * Method: getAllVehicles
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce i veicoli registrati nel sistema
	 * per utilizzare i dati per una richeista
	 * </p>
	 * 
	 * @return List<Vehicle>
	 */
	@GET
	@Path("/findVehicles")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Vehicle> getAllVehicles() {
		return vc.getAllVehicle();
	}

	/**
	 * <p>
	 * Method: findVehicles
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce i veicoli utilizzabili nelle
	 * missioni
	 * </p>
	 * 
	 * @return
	 */
	@POST
	@Path("/findVehicles")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public List<Veicolo> findVehicles(Veicolo veicolo) {
		return uc.findVehicles(veicolo);
	}

	/**
	 * <p>
	 * Method: findVinVehicles
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce i veicoli utilizzabili nelle
	 * missioni associati ad un vin
	 * </p>
	 * 
	 * @return
	 */
	@POST
	@Path("/findVinVehicles")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public List<Veicolo> findVehicles(RichiestaVeicolo veicolo) {
		return uc.findVehicles(veicolo);
	}

	/**
	 * <p>
	 * Method: getVehiclesForOP
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce i veicoli utilizzabili nelle
	 * missioni
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/getVehiclesForOP/{operatoreLogistico}")
	@Produces(APPLICATION_JSON)
	public List<Vehicle> getVehicles(
			@PathParam("operatoreLogistico") String operatoreLogistico) {
		return vc.getVehicles(operatoreLogistico);
	}

	/**
	 * <p>
	 * Method: aggiornaStatoVeicolo
	 * </p>
	 * <p>
	 * Description: Questo metodo aggiorna i veicoli utilizzabili nelle missioni
	 * </p>
	 * 
	 * @return
	 */
	@POST
	@Path("/aggiornaStatoVeicolo")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public Veicolo aggiornaStatoVeicolo(Veicolo veicolo) {
		return uc.aggiornaStatoVeicolo(veicolo);
	}

	/**
	 * <p>
	 * Method: getTruckServiceClass
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce i modelli dei veicoli utilizzabili
	 * nelle missioni
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/getTruckServiceClass")
	@Produces(APPLICATION_JSON)
	public List<TruckServiceClass> getAllTruckServiceClass() {
		return vc.getAllTruckServiceClass();
	}
}
