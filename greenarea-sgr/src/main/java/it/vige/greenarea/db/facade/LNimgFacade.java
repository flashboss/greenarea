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
package it.vige.greenarea.db.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import it.vige.greenarea.db.entities.LNimg;
import it.vige.greenarea.db.entities.LNimg_;

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
			result = (LNimg) getEntityManager().createQuery(cq).getSingleResult();
		} catch (Exception ex) {
		}
		return result;
	}
}
