/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.facade;

import it.vige.greenarea.cl.library.entities.Filter;
import it.vige.greenarea.cl.library.entities.FilterKey;
import it.vige.greenarea.cl.library.entities.Filter_;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

/**
 * 
 * @author Administrator
 */
@Stateless
public class FilterFacade extends AbstractFacade<Filter, FilterKey> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public FilterFacade() {
		super(Filter.class);
	}

	@Override
	public FilterKey getId(Filter entity) {
		return entity.getId();
	}

	@Override
	public void setId(Filter entity, FilterKey id) {
		entity.setId(id);
	}

	public List<Filter> findAll(String operatoreLogistico) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		javax.persistence.criteria.CriteriaQuery cq = builder.createQuery();
		Root<Filter> filterRoot = cq.from(Filter.class);
		cq.select(filterRoot);
		cq.where((builder.equal(filterRoot.get(Filter_.operatoreLogistico),
				operatoreLogistico)));
		return getEntityManager().createQuery(cq).getResultList();
	}

}
