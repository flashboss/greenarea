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
package it.vige.greenarea.gtg.db.facades;

import static it.vige.greenarea.cl.library.entities.FreightType.DOCUMENTI;
import it.vige.greenarea.cl.library.entities.Attachment;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.FreightItemState;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.itseasy.lib.mqData.MqFreightData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

@Stateless
public class FreightFacade extends AbstractFacade<Freight, String> {

	@EJB
	private TransportFacade transportFacade;
	// char[]> {
	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public FreightFacade() {
		super(Freight.class);
	}

	@Override
	public void create(Freight entity) {

		if (entity.getCodeId() == null) {
			throw new PersistenceException("Freight ID code cannot be null");
		}
		super.create(entity);
	}

	public List<Freight> findAll(Transport transport) {
		Query query = em
				.createQuery("select f from Freight f where f.transport = :transport");
		query.setParameter("transport", transport);
		return query.getResultList();
	}

	public Map<Transport, List<Freight>> findAll(List<Transport> transports) {
		Map<Transport, List<Freight>> resultFr = new HashMap<Transport, List<Freight>>();
		if (transports.size() > 0) {
			Query query = em
					.createQuery("select f from Freight f where f.transport in :transports");
			query.setParameter("transports", transports);
			List<Freight> resultTr = query.getResultList();
			for (Freight freight : resultTr) {
				Transport transport = freight.getTransport();
				if (resultFr.get(transport) == null) {
					List<Freight> freightList = new ArrayList<Freight>();
					resultFr.put(transport, freightList);
				}
				resultFr.get(transport).add(freight);
			}
		}
		return resultFr;
	}

	public Freight create(MqFreightData mqF) {
		if (mqF.getItemID() == null) {
			throw new PersistenceException("Freight ID code cannot be null");
		}
		Freight f = new Freight();
		f.setCodeId(mqF.getItemID());
		f.setDescription(mqF.getDescrizione());
		f.setFt(DOCUMENTI);
		f.setHeight((mqF.getHeight() == null) ? 0 : mqF.getHeight());
		f.setKeepUpStanding((mqF.getKeepUpStanding() == null) ? false : mqF
				.getKeepUpStanding());
		f.setLeng((mqF.getLenght() == null) ? 0 : mqF.getLenght());
		f.setStackable((mqF.getStackable() == null) ? false : mqF
				.getStackable());
		f.setVolume(((mqF.getVolume() == null) || (mqF.getVolume() == 0)) ? 10
				: mqF.getVolume());
		f.setWeight((mqF.getWeight() == null) ? 0 : mqF.getWeight());
		f.setWidth((mqF.getWidth() == null) ? 0 : mqF.getWidth());
		// creo degli attachment con gli attributi???
		Attachment att;
		if (mqF.getAttributi() != null) {
			for (String s : mqF.getAttributi().keySet()) {
				att = new Attachment();
				att.setName(s);
				att.setContents(mqF.getAttributi().get(s));
				f.getAttachments().add(att);
			}
		}
		create(f);
		return f;
	}

	@Override
	public String getId(Freight entity) {
		return entity.getCodeId();
	}

	@Override
	public void setId(Freight entity, String id) {
		entity.setCodeId(id);
	}

	public void changeFreightStatus(String id, FreightItemState newStatus) {
		Freight f = find(id);
		if (f == null) {
			return;
		}
		f.setFreightState(newStatus);
		edit(f);

		switch (newStatus) {
		case LOADED:
			transportFacade.notifyLoaded(f.getTransport());
			break;
		case DELIVERED:
			transportFacade.notifyDelivery(f.getTransport());
			break;
		case AVAILABLE:
			break;
		case NOTAVAILABLE:
			break;
		case NOTDELIVERED:
			break;
		case NOTLOADED:
			break;
		default:
			break;
		}
	}
}
