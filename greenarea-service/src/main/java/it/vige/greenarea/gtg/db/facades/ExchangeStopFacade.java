/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.db.facades;

import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Mission;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * 
 * @author 00917308
 */
@Stateless
public class ExchangeStopFacade extends AbstractFacade<ExchangeStop, Long> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ExchangeStopFacade() {
		super(ExchangeStop.class);
	}

	@Override
	public Long getId(ExchangeStop entity) {
		return entity.getId();
	}

	@Override
	public void setId(ExchangeStop entity, Long id) {
		entity.setId(id);
	}

	public List<ExchangeStop> findAll(Mission mission) {
		Query query = em
				.createQuery("select f.exchangeStops from Mission f where f = :mission");
		query.setParameter("mission", mission);
		return query.getResultList();
	}

}
