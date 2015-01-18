/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.sessions;

import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.library.entities.ValueMission_;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * 
 */
@Stateless
public class ValueMissionFacade extends AbstractFacade<ValueMission, Integer> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	protected EntityManager getEntityManager() {
		return em;
	}

	public ValueMissionFacade() {
		super(ValueMission.class);
	}

	@Override
	public Integer getId(ValueMission entity) {
		return entity.getIdVM();
	}

	@Override
	public void setId(ValueMission entity, Integer id) {
	}

	public List<ValueMission> findAByMission(Mission mission) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<ValueMission> trRoot = cq.from(ValueMission.class);
		Predicate wherePredicate = cb.and(cb.equal(
				trRoot.get(ValueMission_.mission), mission));
		cq = cq.select(trRoot).where(wherePredicate);
		return getEntityManager().createQuery(cq).getResultList();
	}

}
