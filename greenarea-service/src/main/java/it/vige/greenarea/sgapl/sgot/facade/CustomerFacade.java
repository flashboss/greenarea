/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.facade;

import it.vige.greenarea.cl.library.entities.Customer;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 
 * @author Administrator
 */
@Stateless
public class CustomerFacade extends AbstractFacade<Customer, Long> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public CustomerFacade() {
		super(Customer.class);
	}

	@Override
	public Long getId(Customer entity) {
		return entity.getId();
	}

	@Override
	public void setId(Customer entity, Long id) {
		entity.setId(id);
	}

}
