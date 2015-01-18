/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
