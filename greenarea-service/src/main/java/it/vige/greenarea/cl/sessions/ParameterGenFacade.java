/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.sessions;

import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * 
 */
@Stateless
public class ParameterGenFacade extends AbstractFacade<ParameterGen, Integer> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	protected EntityManager getEntityManager() {
		return em;
	}

	public ParameterGenFacade() {
		super(ParameterGen.class);
	}

	@Override
	public Integer getId(ParameterGen entity) {
		return entity.getId();
	}

	@Override
	public void setId(ParameterGen entity, Integer id) {
		entity.setId(id);
	}

}
