/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.facade;

import it.vige.greenarea.cl.library.entities.ShippingItem;
import it.vige.greenarea.cl.library.entities.ShippingItem_;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
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
public class ShippingItemFacade extends AbstractFacade<ShippingItem, String> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ShippingItemFacade() {
		super(ShippingItem.class);
	}

	@Override
	public String getId(ShippingItem entity) {
		return entity.getId();
	}

	@Override
	public void setId(ShippingItem entity, String id) {
		entity.setId(id);
	}

	public List<ShippingItem> findAll(ShippingOrder shippingOrder) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		javax.persistence.criteria.CriteriaQuery cq = builder.createQuery();
		Root<ShippingItem> personRoot = cq.from(ShippingItem.class);
		cq.select(personRoot);
		cq.where((builder.equal(personRoot.get(ShippingItem_.shippingOrder),
				shippingOrder)));
		return getEntityManager().createQuery(cq).getResultList();
	}

}
