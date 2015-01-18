/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.sessions;

import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 
 * @author MacRed
 */
@Stateless
public class TimeSlotFacade extends AbstractFacade<TimeSlot, Integer> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	protected EntityManager getEntityManager() {
		return em;
	}

	public TimeSlotFacade() {
		super(TimeSlot.class);
	}

	@Override
	public Integer getId(TimeSlot entity) {
		return entity.getIdTS();
	}

	@Override
	public void setId(TimeSlot entity, Integer id) {
	}

}
