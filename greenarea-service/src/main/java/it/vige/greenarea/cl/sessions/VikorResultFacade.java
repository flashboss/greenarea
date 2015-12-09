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
package it.vige.greenarea.cl.sessions;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.vige.greenarea.cl.library.entities.VikorResult;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

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
