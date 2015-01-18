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

import it.vige.greenarea.cl.library.entities.Filter;
import it.vige.greenarea.cl.library.entities.FilterKey;
import it.vige.greenarea.cl.library.entities.Filter_;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

@Stateless
public class FilterFacade extends AbstractFacade<Filter, FilterKey> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public FilterFacade() {
		super(Filter.class);
	}

	@Override
	public FilterKey getId(Filter entity) {
		return entity.getId();
	}

	@Override
	public void setId(Filter entity, FilterKey id) {
		entity.setId(id);
	}

	public List<Filter> findAll(String operatoreLogistico) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		javax.persistence.criteria.CriteriaQuery cq = builder.createQuery();
		Root<Filter> filterRoot = cq.from(Filter.class);
		cq.select(filterRoot);
		cq.where((builder.equal(filterRoot.get(Filter_.operatoreLogistico),
				operatoreLogistico)));
		return getEntityManager().createQuery(cq).getResultList();
	}

}
