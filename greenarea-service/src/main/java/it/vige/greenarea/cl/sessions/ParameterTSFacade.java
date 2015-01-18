/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.sessions;

import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.ParameterTS_;
import it.vige.greenarea.cl.library.entities.TimeSlot;
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
public class ParameterTSFacade extends AbstractFacade<ParameterTS, Integer> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	protected EntityManager getEntityManager() {
		return em;
	}

	public ParameterTSFacade() {
		super(ParameterTS.class);
	}

	@Override
	public Integer getId(ParameterTS entity) {
		return entity.getId();
	}

	@Override
	public void setId(ParameterTS entity, Integer id) {
	}

	public List<ParameterTS> findAll(TimeSlot timeSlot) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<ParameterTS> trRoot = cq.from(ParameterTS.class);
		Predicate wherePredicate = cb.and(cb.equal(trRoot.get(ParameterTS_.ts),
				timeSlot));
		cq = cq.select(trRoot).where(wherePredicate);
		return getEntityManager().createQuery(cq).getResultList();
	}

}
