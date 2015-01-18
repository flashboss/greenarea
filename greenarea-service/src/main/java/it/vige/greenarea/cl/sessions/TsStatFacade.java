/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.sessions;

import it.vige.greenarea.cl.library.entities.TsStat;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 
 * @author MacRed
 */
@Stateless
public class TsStatFacade extends AbstractFacade<TsStat, Integer> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	protected EntityManager getEntityManager() {
		return em;
	}

	public TsStatFacade() {
		super(TsStat.class);
	}

	@Override
	public Integer getId(TsStat entity) {
		return entity.getIdTsStat();
	}

	@Override
	public void setId(TsStat entity, Integer id) {
	}

}
