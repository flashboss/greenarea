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
package it.vige.greenarea.tap.facades;

import it.vige.greenarea.cl.library.entities.TapGroupData;
import it.vige.greenarea.cl.library.entities.TapGroupData_;
import it.vige.greenarea.cl.library.entities.TapOutData;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Stateless
public class TapGroupDataFacade extends AbstractFacade<TapGroupData, Integer> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public TapGroupDataFacade() {
		super(TapGroupData.class);
	}

	@Override
	public Integer getId(TapGroupData entity) {
		return entity.getId();
	}

	@Override
	public void setId(TapGroupData entity, Integer id) {
		entity.setId(id);
	}

	public List<TapGroupData> findByName(String name) {
		CriteriaBuilder criteriaBuilder = getEntityManager()
				.getCriteriaBuilder();
		javax.persistence.criteria.CriteriaQuery cq = criteriaBuilder
				.createQuery();
		Root<TapGroupData> root = cq.from(TapGroupData.class);
		cq = cq.select(root);
		Predicate wherePredicate = criteriaBuilder.and(criteriaBuilder.equal(
				root.get(TapGroupData_.name), name));
		cq = cq.where(wherePredicate);
		return getEntityManager().createQuery(cq).getResultList();
	}

	public List<TapGroupData> findByOutData(TapOutData tapOutData) {
		CriteriaBuilder criteriaBuilder = getEntityManager()
				.getCriteriaBuilder();
		javax.persistence.criteria.CriteriaQuery cq = criteriaBuilder
				.createQuery();
		Root<TapGroupData> root = cq.from(TapGroupData.class);
		cq = cq.select(root);
		Predicate wherePredicate = criteriaBuilder.and(criteriaBuilder.equal(
				root.get(TapGroupData_.tapOutData), tapOutData));
		cq = cq.where(wherePredicate);
		return getEntityManager().createQuery(cq).getResultList();
	}

	public Map<TapOutData, List<TapGroupData>> findByOutData(
			List<TapOutData> tapOutDatas) {
		String query = "select a from TapGroupData a where a.tapOutData in :id and a.name = 'GPS_DATA'";
		Query q = getEntityManager().createQuery(query);
		q.setParameter("id", tapOutDatas);
		Map<TapOutData, List<TapGroupData>> mapOutGroup = new HashMap<TapOutData, List<TapGroupData>>();
		@SuppressWarnings("unchecked")
		List<TapGroupData> listGroups = (List<TapGroupData>) q.getResultList();
		for (TapGroupData tapGroupData : listGroups) {
			TapOutData tapOutData = tapGroupData.getTapOutData();
			if (mapOutGroup.get(tapOutData) == null)
				mapOutGroup.put(tapOutData, new ArrayList<TapGroupData>());
			mapOutGroup.get(tapOutData).add(tapGroupData);
		}
		return mapOutGroup;
	}
}
