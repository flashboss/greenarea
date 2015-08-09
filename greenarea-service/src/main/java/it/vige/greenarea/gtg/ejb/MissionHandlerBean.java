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
package it.vige.greenarea.gtg.ejb;

import static it.vige.greenarea.Conversioni.convertIntToFreightItemState;
import static it.vige.greenarea.Conversioni.convertIntToStatoMissione;
import static it.vige.greenarea.Conversioni.convertStringToTimestamp;
import static it.vige.greenarea.Conversioni.convertTimestampToString;
import static it.vige.greenarea.gtg.webservice.exceptions.GTGexception.GTGerrorCodes.ALLOCATE_MISSION_NOT_ALLOWED;
import static it.vige.greenarea.gtg.webservice.exceptions.GTGexception.GTGerrorCodes.CLOSE_MISSION_NOT_ALLOWED;
import static it.vige.greenarea.gtg.webservice.exceptions.GTGexception.GTGerrorCodes.NOTIFY_FREIGHT_STATUS_NOT_ALLOWED;
import static it.vige.greenarea.gtg.webservice.exceptions.GTGexception.GTGerrorCodes.NO_MISSION_ALLOCATED;
import static it.vige.greenarea.gtg.webservice.exceptions.GTGexception.GTGerrorCodes.UNKNOWN_FREIGHT_ID;
import static it.vige.greenarea.gtg.webservice.exceptions.GTGexception.GTGerrorCodes.UNKNOWN_FREIGHT_STATE;
import static it.vige.greenarea.gtg.webservice.exceptions.GTGexception.GTGerrorCodes.UNKNOWN_MISSION_ID;
import static it.vige.greenarea.gtg.webservice.exceptions.GTGexception.GTGerrorCodes.UNKNOWN_MISSION_STATE;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.Attachment;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.FreightItemState;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.dto.StatoMissione;
import it.vige.greenarea.gtg.db.demoData.InitDemoData;
import it.vige.greenarea.gtg.db.facades.ExchangeStopFacade;
import it.vige.greenarea.gtg.db.facades.FreightFacade;
import it.vige.greenarea.gtg.db.facades.MissionFacade;
import it.vige.greenarea.gtg.db.facades.TransportFacade;
import it.vige.greenarea.gtg.webservice.exceptions.GTGexception;
import it.vige.greenarea.gtg.webservice.wsdata.ExchangeStopItem;
import it.vige.greenarea.gtg.webservice.wsdata.FreightItemAction;
import it.vige.greenarea.gtg.webservice.wsdata.MissionItem;
import it.vige.greenarea.gtg.webservice.wsdata.TransportItem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.slf4j.Logger;

@Stateless
public class MissionHandlerBean {

	private Logger logger = getLogger(getClass());

	@EJB
	private TransportFacade transportFacade;
	@EJB
	private InitDemoData initDemoData;
	@EJB
	private ExchangeStopFacade exchangeStopFacade;
	@EJB
	private FreightFacade freightFacade;
	@EJB
	private MissionFacade missionFacade;
	boolean demo = false;

	public List<MissionItem> getMissions(String dateTime, String user) {
		if ((dateTime == null) || (dateTime.isEmpty())) {
			Date now = new Date();
			dateTime = convertTimestampToString(new Timestamp(now.getTime()));
		}
		List<Mission> allocatedMissions = missionFacade
				.findAllocatedMissions(user);
		List<Mission> avaliableMissions = missionFacade
				.findAvailableMissions(dateTime);
		logger.debug("ws missions executing demo: " + demo);
		logger.debug("ws missions executing dateTime: " + dateTime);
		logger.debug("ws missions executing user: " + user);
		if (demo) {
			return initDemoData.getMissions();
		}
		List<MissionItem> result = new ArrayList<MissionItem>();
		if ((allocatedMissions != null) && (!allocatedMissions.isEmpty())) {
			for (Mission m : allocatedMissions) {
				result.add(new MissionItem(m));
			}
		}
		if ((avaliableMissions != null) && (!avaliableMissions.isEmpty())) {
			for (Mission m : avaliableMissions) {
				result.add(new MissionItem(m));
			}
		}
		if (result != null) {
			for (MissionItem mission : result) {
				logger.debug("ws missions mission.getId() = " + mission.getId());
				List<TransportItem> transportItems = mission.getTransports();
				if (transportItems != null) {
					for (TransportItem transportItem : transportItems) {
						logger.debug("ws missions           transportItem.getCode() = "
								+ transportItem.getCode());
					}
				}
				List<ExchangeStopItem> exchangeStops = mission
						.getExchangeStops();
				if (exchangeStops != null) {
					for (ExchangeStopItem exchangeStopItem : exchangeStops) {
						logger.debug("ws missions           exchangeStop.getId() = "
								+ exchangeStopItem.getId());
						List<String> collectingItems = exchangeStopItem
								.getCollectingItems();
						if (collectingItems != null) {
							for (String collectingItem : collectingItems) {
								logger.debug("ws missions                      collectingItem = "
										+ collectingItem);

							}
						}
						List<String> deliveryItems = exchangeStopItem
								.getDeliveryItems();
						if (deliveryItems != null) {
							for (String deliveryItem : deliveryItems) {
								logger.debug("ws missions                      deliveryItem = "
										+ deliveryItem);

							}
						}
					}
				}
			}
		}
		return result;
	}

	public void changeMissionState(String user, Long missionId, int state,
			int cause, String note, String dateTime) throws GTGexception {
		logger.debug("ws mission state executing user: " + user);
		logger.debug("ws mission state executing missionId: " + missionId);
		logger.debug("ws mission state executing state: " + state);
		logger.debug("ws mission state executing note: " + note);
		logger.debug("ws mission state executing dateTime: " + dateTime);
		if (demo) {
			return;
		}

		Mission mission = missionFacade.find(missionId);
		if (mission == null) {
			ArrayList<String> param = new ArrayList<String>();
			param.add(missionId.toString());
			throw new GTGexception(UNKNOWN_MISSION_ID, param);
		}
		Timestamp t = convertStringToTimestamp(dateTime);
		// TODO bisogna cambiare lo stato della entity Mission....
		logger.debug("Mission ID: " + missionId + " modifico stato in " + state
				+ " @" + t.toString());
		StatoMissione newStatoMissione = convertIntToStatoMissione(state);
		// ATTENzione se il nuovo stato e' sconosciuto lascio quello vecchio
		if (newStatoMissione == null) {
			ArrayList<String> param = new ArrayList<String>();
			param.add(String.valueOf(state));

			throw new GTGexception(UNKNOWN_MISSION_STATE, param);
		}
		List<Mission> avaliableMissions = missionFacade
				.findAllocatedMissions(user);
		switch (newStatoMissione) {
		case STARTED:

			if ((avaliableMissions != null) && (!avaliableMissions.isEmpty())) {
				ArrayList<String> param = new ArrayList<String>();
				param.add(user);

				throw new GTGexception(ALLOCATE_MISSION_NOT_ALLOWED, param);
			}
			mission.setMissionState(newStatoMissione);
			mission.setOwnerUser(user);
			break;
		case COMPLETED:
			if (!(avaliableMissions.get(0).getId().equals(missionId))) {
				ArrayList<String> param = new ArrayList<String>();
				param.add(String.valueOf(missionId));
				param.add(user);

				throw new GTGexception(CLOSE_MISSION_NOT_ALLOWED, param);
			}
			// vado a vedere se tutti i trasporti sono stati completati,
			// quelli incompleti li metto in
			// waiting per il prossimo builder e rimettere available i freight
			// non consegnati
			// TODO gestire il caso di respinto

			missionFacade.completeMission(mission);

			break;
		case REJECTED:
			missionFacade.rejectMission(mission);

			break;
		case WAITING:
			break;
		}

		Attachment a = new Attachment();
		a.setName("Change Mission State");
		StringBuilder sb = new StringBuilder();
		sb.append("Mission state changed at ").append(dateTime);
		sb.append(" note: ").append(note);
		a.setContents(sb.toString());
		//mission.getAttachments().add(a);
		missionFacade.edit(mission);
	}

	public void notifyFreightItemActions(String user,
			List<FreightItemAction> freightItemsAction) throws GTGexception {
		logger.debug("ws notify state executing user: " + user);
		if (freightItemsAction != null)
			for (FreightItemAction freightItemAct : freightItemsAction) {
				logger.debug("ws notify state executing freightItemAct getDateTime: "
						+ freightItemAct.getDateTime());
				logger.debug("ws notify state executing freightItemAct getFreightItemCode: "
						+ freightItemAct.getFreightItemCode());
				logger.debug("ws notify state executing freightItemAct getNote: "
						+ freightItemAct.getNote());
				logger.debug("ws notify state executing freightItemAct getState: "
						+ freightItemAct.getState());
				logger.debug("ws notify state executing freightItemAct getCause: "
						+ freightItemAct.getCause());
				logger.debug("ws notify state executing freightItemAct getExchangeStopId: "
						+ freightItemAct.getExchangeStopId());
			}

		if (demo) {
			return;
		}
		List<Mission> avaliableMissions = missionFacade
				.findAllocatedMissions(user);
		if ((avaliableMissions == null) || (avaliableMissions.isEmpty())) {
			ArrayList<String> param = new ArrayList<String>();
			param.add(user);
			throw new GTGexception(NO_MISSION_ALLOCATED, param);
		}
		Long mID = avaliableMissions.get(0).getId();
		for (FreightItemAction fia : freightItemsAction) {

			Freight f = freightFacade.find(fia.getFreightItemCode());

			if (f == null) {
				ArrayList<String> param = new ArrayList<String>();
				param.add(fia.getFreightItemCode().toString());
				throw new GTGexception(UNKNOWN_FREIGHT_ID, param);
			}
			if (!f.getTransport().getMission().getId().equals(mID)) {
				ArrayList<String> param = new ArrayList<String>();
				param.add(mID.toString());
				param.add(user);
				throw new GTGexception(NOTIFY_FREIGHT_STATUS_NOT_ALLOWED, param);
			}
			FreightItemState newState = convertIntToFreightItemState(fia
					.getState());
			if (newState == null) {
				ArrayList<String> param = new ArrayList<String>();
				param.add(String.valueOf(fia.getState()));

				throw new GTGexception(UNKNOWN_FREIGHT_STATE, param);
			}
			Attachment a = new Attachment();
			a.setName("Change Freight State");
			StringBuilder sb = new StringBuilder();
			sb.append("Freight state changed at ").append(fia.getDateTime());
			sb.append(" in exchangeStop: ")
					.append(exchangeStopFacade.find(fia.getExchangeStopId())
							.toString());
			sb.append(" new state: ").append(newState.toString());
			if (fia.getCause() != null) {
				sb.append(" cause: ").append(fia.getCause());
			}
			sb.append(" notes: ").append(fia.getNote());
			a.setContents(sb.toString());
			//f.getAttachments().add(a);
			freightFacade.changeFreightStatus(f.getCodeId(), newState);
		}
	}
}
