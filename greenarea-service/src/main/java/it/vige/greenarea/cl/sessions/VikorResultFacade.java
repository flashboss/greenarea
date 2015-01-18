/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.sessions;

import it.vige.greenarea.cl.library.entities.VikorResult;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 
 * @author MacRed
 */
@Stateless
public class VikorResultFacade extends AbstractFacade<VikorResult, Integer> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	protected EntityManager getEntityManager() {
		return em;
	}

	public VikorResultFacade() {
		super(VikorResult.class);
	}

	@Override
	public Integer getId(VikorResult entity) {
		return entity.getId();
	}

	@Override
	public void setId(VikorResult entity, Integer id) {
	}

}
