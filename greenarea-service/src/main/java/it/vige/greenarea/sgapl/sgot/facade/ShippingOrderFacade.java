/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.facade;

import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.ShippingOrder_;
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
public class ShippingOrderFacade extends AbstractFacade<ShippingOrder, String> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ShippingOrderFacade() {
		super(ShippingOrder.class);
	}

	@Override
	public String getId(ShippingOrder entity) {
		return entity.getId();
	}

	@Override
	public void setId(ShippingOrder entity, String id) {
		entity.setId(id);
	}

	public List<ShippingOrder> findAll(String operatoreLogistico) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		javax.persistence.criteria.CriteriaQuery cq = builder.createQuery();
		Root<ShippingOrder> personRoot = cq.from(ShippingOrder.class);
		cq.select(personRoot);
		cq.where((builder.equal(
				personRoot.get(ShippingOrder_.operatoreLogistico),
				operatoreLogistico)));
		return getEntityManager().createQuery(cq).getResultList();
	}
}
