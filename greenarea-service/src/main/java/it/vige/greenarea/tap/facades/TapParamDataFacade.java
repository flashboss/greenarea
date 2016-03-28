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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.vige.greenarea.cl.library.entities.TapGroupData;
import it.vige.greenarea.cl.library.entities.TapOutData;
import it.vige.greenarea.cl.library.entities.TapParamData;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

@Stateless
public class TapParamDataFacade extends AbstractFacade<TapParamData, Integer> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public TapParamDataFacade() {
		super(TapParamData.class);
	}

	@Override
	public Integer getId(TapParamData entity) {
		return entity.getId();
	}

	@Override
	public void setId(TapParamData entity, Integer id) {
		entity.setId(id);
	}

	public List<TapParamData> findAll(TapGroupData tapGroupData) {
		String query = "select a from TapParamData a where a.tapGroupData.id = :id and (a.name = 'LATITUDE' or a.name='LONGITUDE' or a.name='TIMESTAMP')";
		Query q = getEntityManager().createQuery(query);
		q.setParameter("id", tapGroupData.getId());
		return (List<TapParamData>) q.getResultList();

	}

	public List<TapParamData> findAll(String typeGroup) {
		String query = "select a from TapParamData a where a.tapGroupData.name = :typeGroup and (a.name = 'LATITUDE' or a.name='LONGITUDE' or a.name='TIMESTAMP')";
		Query q = getEntityManager().createQuery(query);
		q.setParameter("typeGroup", typeGroup);
		return (List<TapParamData>) q.getResultList();

	}

	public Map<TapGroupData, List<TapParamData>> findAll(List<TapOutData> tapOutDatas) {
		String query = "select a from TapParamData a where a.tapGroupData.tapOutData in :id and a.tapGroupData.name = 'GPS_DATA' and (a.name = 'LATITUDE' or a.name='LONGITUDE' or a.name='TIMESTAMP')";
		Query q = getEntityManager().createQuery(query);
		q.setParameter("id", tapOutDatas);
		Map<TapGroupData, List<TapParamData>> mapGroupParam = new HashMap<TapGroupData, List<TapParamData>>();
		@SuppressWarnings("unchecked")
		List<TapParamData> listParams = (List<TapParamData>) q.getResultList();
		for (TapParamData tapParamData : listParams) {
			TapGroupData tapGroupData = tapParamData.getTapGroupData();
			if (mapGroupParam.get(tapGroupData) == null)
				mapGroupParam.put(tapGroupData, new ArrayList<TapParamData>());
			mapGroupParam.get(tapGroupData).add(tapParamData);
		}
		return mapGroupParam;
	}
}
