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
package it.vige.greenarea.gtg.db.facades;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.TruckServiceClass;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.cl.library.entities.Vehicle_;
import it.vige.greenarea.dto.StatoVeicolo;

@Stateless
public class TruckFacade extends AbstractFacade<Vehicle, String> {

	private Logger logger = getLogger(getClass());

	@EJB
	private TruckServiceClassFacade truckServiceClassFacade;
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public TruckFacade() {
		super(Vehicle.class);
	}

	@Override
	public void create(Vehicle t) {
		if (t.getPlateNumber() == null) {
			throw new PersistenceException("Truck ID PlatNumber cannot be null");
		}
		super.create(t);
	}

	public List<Vehicle> findBySelection(StatoVeicolo state, String serviceClass, String roundCode,
			String operatoreLogistico) {
		String newRoundCode = "";
		if (roundCode != null && roundCode.length() == 1)
			newRoundCode = "0" + roundCode;
		else
			newRoundCode = roundCode;
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<Vehicle> trRoot = cq.from(Vehicle.class);
		List<TruckServiceClass> truckClassList = truckServiceClassFacade.findBySelection(serviceClass);
		if ((truckClassList == null) || (truckClassList.isEmpty())) {
			return null;
		}
		logger.debug(
				String.format("->Found %d classes for \"%s\" service class\n", truckClassList.size(), serviceClass));

		Predicate wherePredicate = cb.and(cb.equal(trRoot.get(Vehicle_.operatoreLogistico), operatoreLogistico),
				cb.equal(trRoot.get(Vehicle_.state), state), cb.equal(trRoot.get(Vehicle_.roundCode), newRoundCode));
		cq = cq.select(trRoot).where(wherePredicate);
		List<Vehicle> vehicles = getEntityManager().createQuery(cq).getResultList();
		List<Vehicle> newVehicles = new ArrayList<Vehicle>();
		for (Vehicle vehicle : vehicles) {
			if (vehicle.getServiceClass().getDescription().equals(truckClassList.get(0).getDescription()))
				newVehicles.add(vehicle);
		}
		return newVehicles;
	}

	public Vehicle findByVin(String vin) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<Vehicle> trRoot = cq.from(Vehicle.class);
		Predicate wherePredicate = cb.and(cb.like(trRoot.get(Vehicle_.vin), vin));
		cq = cq.select(trRoot).where(wherePredicate);
		List<Vehicle> vehicles = (List<Vehicle>) getEntityManager().createQuery(cq).getResultList();
		if (vehicles == null || vehicles.isEmpty())
			return null;
		else
			return vehicles.get(0);
	}

	@Override
	public String getId(Vehicle entity) {
		return entity.getPlateNumber();
	}

	@Override
	public void setId(Vehicle entity, String id) {
		entity.setPlateNumber(id);
	}
}
