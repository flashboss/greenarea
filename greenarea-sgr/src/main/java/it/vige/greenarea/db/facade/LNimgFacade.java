/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.db.facade;

import it.vige.greenarea.db.entities.LNimg;
import it.vige.greenarea.db.entities.LNimg_;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * 
 * @author 00917308
 */
@Stateless
public class LNimgFacade extends AbstractFacade<LNimg> {
	
	@PersistenceContext(unitName = "SGRLserverPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public LNimgFacade() {
		super(LNimg.class);
	}

	public LNimg find(String name) {
		LNimg result = null;
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Object> cq = cb.createQuery();
		Root<LNimg> tRoot = cq.from(LNimg.class);
		cq.select(tRoot).where(cb.equal(tRoot.get(LNimg_.name), name));
		try {
			result = (LNimg) getEntityManager().createQuery(cq)
					.getSingleResult();
		} catch (Exception ex) {
		}
		return result;
	}
}
