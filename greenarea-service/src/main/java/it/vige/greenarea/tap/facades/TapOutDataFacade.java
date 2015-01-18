/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.tap.facades;

import it.vige.greenarea.cl.library.entities.TapOutData;
import it.vige.greenarea.cl.library.entities.TapOutData_;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 
 * @author 00917377
 */
@Stateless
public class TapOutDataFacade extends AbstractFacade<TapOutData, Integer> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public TapOutDataFacade() {
		super(TapOutData.class);
	}

	@Override
	public Integer getId(TapOutData entity) {
		return entity.getId();
	}

	@Override
	public void setId(TapOutData entity, Integer id) {
		entity.setId(id);
	}

	public List<TapOutData> findAll(String vin) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<TapOutData> trRoot = cq.from(TapOutData.class);

		Predicate wherePredicate = cb.and(cb.like(trRoot.get(TapOutData_.vin),
				vin));
		cq = cq.select(trRoot).where(wherePredicate);
		Query q = getEntityManager().createQuery(cq);
		return q.getResultList();

	}

	public List<TapOutData> findAllAccess() {
		CriteriaBuilder criteriaBuilder = getEntityManager()
				.getCriteriaBuilder();
		javax.persistence.criteria.CriteriaQuery cq = criteriaBuilder
				.createQuery();
		Root<TapOutData> root = cq.from(TapOutData.class);
		cq = cq.select(root);
		Predicate wherePredicate = criteriaBuilder.and(criteriaBuilder.like(
				root.get(TapOutData_.codeFunction), "IGNITION_%"));
		cq = cq.where(wherePredicate).orderBy(
				criteriaBuilder.asc(root.get(TapOutData_.date)));
		return getEntityManager().createQuery(cq).getResultList();
	}
}
