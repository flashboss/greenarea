/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.greenarea.sgapl.sgot.facade;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.ShippingOrder_;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

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
		cq.where((builder.equal(personRoot.get(ShippingOrder_.operatoreLogistico), operatoreLogistico)));
		return getEntityManager().createQuery(cq).getResultList();
	}
}
