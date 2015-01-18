/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.tap.facades;

import it.vige.greenarea.cl.library.entities.TapGroupData;
import it.vige.greenarea.cl.library.entities.TapOutData;
import it.vige.greenarea.cl.library.entities.TapParamData;
import it.vige.greenarea.gtg.db.facades.AbstractFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * 
 * @author 00917377
 */
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

	public Map<TapGroupData, List<TapParamData>> findAll(
			List<TapOutData> tapOutDatas) {
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
