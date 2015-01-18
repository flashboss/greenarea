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
package it.vige.greenarea.cl.control;

import it.vige.greenarea.cl.library.entities.TruckServiceClass;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.gtg.db.facades.TruckFacade;
import it.vige.greenarea.gtg.db.facades.TruckServiceClassFacade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * <p>
 * Class: TimeSlotControl
 * </p>
 * <p>
 * Description: Questa classe ?? il core di cityLogistics, Gestisce tutte le
 * chiamate e risposte
 * </p>
 * 
 * @author City Logistics Team
 * @version 0.0
 */
@Stateless
public class VehicleControl {

	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@EJB
	private TruckFacade vf;
	@EJB
	private TruckServiceClassFacade tscf;

	/**
	 * <p>
	 * Method: getAllVehicle
	 * </p>
	 * <p>
	 * Description: restituisce una lista di tutti i veicoli nel DB
	 * </p>
	 * 
	 * @param
	 * @return List<Vehicle>
	 */
	public List<Vehicle> getAllVehicle() {
		return vf.findAll();
	}

	/**
	 * <p>
	 * Method: getAllTruckServiceClass
	 * </p>
	 * <p>
	 * Description: restituisce una lista di tutti i modelli dei veicoli nel DB
	 * </p>
	 * 
	 * @param
	 * @return List<Vehicle>
	 */
	public List<TruckServiceClass> getAllTruckServiceClass() {
		return tscf.findAll();
	}

	/**
	 * <p>
	 * Method: getAllVehicle
	 * </p>
	 * <p>
	 * Description: restituisce una lista di tutti i veicoli nel DB
	 * </p>
	 * 
	 * @param
	 * @return List<Vehicle>
	 */
	public List<Vehicle> getVehicles(String operatoreLogistico) {
		Query opQuery = em
				.createQuery("select v from Vehicle v where v.operatoreLogistico = :operatoreLogistico");
		opQuery.setParameter("operatoreLogistico", operatoreLogistico);
		@SuppressWarnings("unchecked")
		List<Vehicle> vehicles = (List<Vehicle>) opQuery.getResultList();
		return vehicles;
	}

	/**
	 * <p>
	 * Method: getAllVehicle
	 * </p>
	 * <p>
	 * Description: restituisce una lista di tutti i veicoli nel DB
	 * </p>
	 * 
	 * @param
	 * @return List<Vehicle>
	 */
	public Vehicle getVehicleWithPlateNumber(String targa) {
		Query opQuery = em
				.createQuery("select v from Vehicle v where v.plateNumber = :targa");
		opQuery.setParameter("targa", targa);
		Vehicle vehicle = (Vehicle) opQuery.getSingleResult();
		return vehicle;
	}

}
