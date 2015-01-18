/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.sessions;

import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.Price_;
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
 * @author MacRed
 */
@Stateless
public class PriceFacade extends AbstractFacade<Price, Integer> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	protected EntityManager getEntityManager() {
		return em;
	}

	public PriceFacade() {
		super(Price.class);
	}

	@Override
	public Integer getId(Price entity) {
		return entity.getId();
	}

	@Override
	public void setId(Price entity, Integer id) {
	}

	public List<Price> findAll(TimeSlot timeSlot) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<Price> trRoot = cq.from(Price.class);
		Predicate wherePredicate = cb.and(cb.equal(trRoot.get(Price_.ts),
				timeSlot));
		cq = cq.select(trRoot).where(wherePredicate);
		return getEntityManager().createQuery(cq).getResultList();
	}

}
