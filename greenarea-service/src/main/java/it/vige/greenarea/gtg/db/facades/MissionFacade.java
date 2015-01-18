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

import static it.vige.greenarea.Conversioni.convertStringToTimestamp;
import static it.vige.greenarea.dto.StatoMissione.COMPLETED;
import static it.vige.greenarea.dto.StatoMissione.REJECTED;
import static it.vige.greenarea.dto.StatoMissione.STARTED;
import static it.vige.greenarea.dto.StatoMissione.WAITING;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.FreightItemState;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.Mission_;
import it.vige.greenarea.cl.library.entities.Transport;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Stateless
public class MissionFacade extends AbstractFacade<Mission, Long> {

	@EJB
	private TransportFacade transportFacade;
	@EJB
	private FreightFacade freightFacade;
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public MissionFacade() {
		super(Mission.class);
	}

	@Override
	public Long getId(Mission entity) {
		return entity.getId();
	}

	@Override
	public void setId(Mission entity, Long id) {
		entity.setId(id);
	}

	@Override
	public void create(Mission m) {
		super.create(m);
		for (Transport t : m.getTransports()) {
			t.setMission(m);
			transportFacade.edit(t);
			for (Freight f : t.getFreightItems())
				freightFacade.edit(f);
		}
		m.setName(m.getId() + "");
		edit(m);
	}

	public List<Mission> findAvailableMissions(String dateTime) {
		if ((dateTime == null) || (dateTime.isEmpty())) {
			return null;
		}
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<Mission> trRoot = cq.from(Mission.class);
		Predicate wherePredicate = cb.and(cb.equal(
				trRoot.get(Mission_.missionState), WAITING), cb.greaterThan(
				trRoot.get(Mission_.expireTime),
				convertStringToTimestamp(dateTime)));
		cq = cq.select(trRoot).where(wherePredicate);
		return getEntityManager().createQuery(cq).getResultList();
	}

	public List<Mission> findAllocatedMissions(String owner) {

		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<Mission> trRoot = cq.from(Mission.class);
		Predicate wherePredicate = cb.and(
				cb.equal(trRoot.get(Mission_.missionState), STARTED),
				cb.equal(trRoot.get(Mission_.ownerUser), owner));
		cq = cq.select(trRoot).where(wherePredicate);
		return getEntityManager().createQuery(cq).getResultList();
	}

	public void completeMission(Mission mission) {
		mission.setMissionState(COMPLETED);
		for (Transport tr : mission.getTransports()) {
			if (!tr.getTransportState().equals(
					Transport.TransportState.completed)) {
				tr.setTransportState(Transport.TransportState.waiting);
				tr.setMission(null);
				transportFacade.edit(tr);
				for (Freight f : tr.getFreightItems()) {
					if (f.getFreightState().equals(FreightItemState.DELIVERED)) {
						continue;
					}
					f.setFreightState(FreightItemState.AVAILABLE);
					f.setPickUpPoint(null);
					f.setDropDownPoint(null);
					freightFacade.edit(f);
				}
			}
		}
		edit(mission);
	}

	public void rejectMission(Mission mission) {
		mission.setMissionState(REJECTED);
		for (Transport tr : mission.getTransports()) {
			if (!tr.getTransportState().equals(
					Transport.TransportState.completed)) {
				tr.setTransportState(Transport.TransportState.waiting);
				tr.setMission(null);
				transportFacade.edit(tr);
				for (Freight f : tr.getFreightItems()) {
					if (f.getFreightState().equals(FreightItemState.DELIVERED)) {
						continue;
					}
					f.setFreightState(FreightItemState.AVAILABLE);
					f.setPickUpPoint(null);
					f.setDropDownPoint(null);
					freightFacade.edit(f);
				}
			}
		}
		edit(mission);
	}
}
