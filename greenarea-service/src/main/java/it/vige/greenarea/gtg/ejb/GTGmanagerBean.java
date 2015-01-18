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

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Constants.BASE_URI_USER;
import static it.vige.greenarea.Conversioni.convertiShippingOrdersToRichieste;
import static it.vige.greenarea.Conversioni.convertiTimeSlotToFasciaOraria;
import static it.vige.greenarea.Conversioni.convertiTimeSlotsToFasceOrarie;
import static it.vige.greenarea.Conversioni.convertiTransportsToRichieste;
import static it.vige.greenarea.Conversioni.convertiVehicleToVeicolo;
import static it.vige.greenarea.Utilities.aggiungiValoriAMissione;
import static it.vige.greenarea.Utilities.associaFasciaOrariaARichiesta;
import static it.vige.greenarea.Utilities.setDettaglio;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.assigned;
import static it.vige.greenarea.dto.StatoMissione.WAITING;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.cl.library.entities.TransportServiceClass;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.cl.sessions.ValueMissionFacade;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.ValoriVeicolo;
import it.vige.greenarea.dto.Veicolo;
import it.vige.greenarea.gtg.db.demoData.InitDemoData;
import it.vige.greenarea.gtg.db.facades.ExchangeStopFacade;
import it.vige.greenarea.gtg.db.facades.FreightFacade;
import it.vige.greenarea.gtg.db.facades.MissionFacade;
import it.vige.greenarea.gtg.db.facades.TransportServiceClassFacade;
import it.vige.greenarea.sgapl.sgot.facade.ShippingItemFacade;
import it.vige.greenarea.sgapl.sgot.facade.ShippingOrderFacade;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.slf4j.Logger;

@Named(value = "gTGmanagerBean")
@Dependent
public class GTGmanagerBean {

	@EJB
	private ExchangeStopFacade exchangeStopFacade;
	@EJB
	private FreightFacade freightFacade;
	@EJB
	private ShippingOrderFacade shippingOrderFacade;
	@EJB
	private ShippingItemFacade shippingItemFacade;
	@EJB
	private MissionBuilderBean missionBuilderBean;
	@EJB
	private InitDemoData initDemoData;
	@EJB
	private MissionFacade missionFacade;
	@EJB
	private ValueMissionFacade valueMissionFacade;
	@EJB
	private TransportServiceClassFacade transportServiceClassFacade;

	private Logger logger = getLogger(getClass());

	/**
	 * Creates a new instance of GTGmanagerBean
	 */
	public GTGmanagerBean() {
	}

	public String caricaPolicy() {
		FacesContext context = FacesContext.getCurrentInstance();
		initDemoData.caricaPolicy();
		FacesMessage msg = new FacesMessage(
				"GTG database inizializzato correttamente");
		context.addMessage(null, msg);
		return "";
	}

	public String eseguiRanking() {
		FacesContext context = FacesContext.getCurrentInstance();
		initDemoData.eseguiRanking();
		FacesMessage msg = new FacesMessage(
				"GTG database inizializzato correttamente");
		context.addMessage(null, msg);
		return "";
	}

	public String caricaTrasporti() {
		FacesContext context = FacesContext.getCurrentInstance();
		initDemoData.caricaTrasporti();
		FacesMessage msg = new FacesMessage(
				"GTG database inizializzato correttamente");
		context.addMessage(null, msg);
		return "";
	}

	public String caricaTrasportiMassivo() {
		FacesContext context = FacesContext.getCurrentInstance();
		initDemoData.caricaTrasportiMassivo();
		FacesMessage msg = new FacesMessage(
				"GTG database inizializzato correttamente");
		context.addMessage(null, msg);
		return "";
	}

	public String cancellaTutto() {
		FacesContext context = FacesContext.getCurrentInstance();
		initDemoData.cancellaTutto();
		FacesMessage msg = new FacesMessage(
				"GTG database inizializzato correttamente");
		context.addMessage(null, msg);
		return "";
	}

	public String cancellaDatiTap() {
		FacesContext context = FacesContext.getCurrentInstance();
		initDemoData.cancellaDatiTap();
		FacesMessage msg = new FacesMessage(
				"GTG database inizializzato correttamente");
		context.addMessage(null, msg);
		return "";
	}

	public String caricaDatiTap() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			initDemoData.caricaDatiTap();
		} catch (Exception e) {
			logger.error("error init data", e);
		}
		FacesMessage msg = new FacesMessage(
				"GTG database inizializzato correttamente");
		context.addMessage(null, msg);
		return "";
	}

	public String buildLightMission() {
		Client clientTS = newClient();
		Builder bldrTs = clientTS.target(BASE_URI_TS + "/findAllTimeSlot")
				.request(APPLICATION_JSON);
		List<TimeSlot> timeSlots = bldrTs
				.get(new GenericType<List<TimeSlot>>() {
				});
		List<FasciaOraria> fasceOrarie = new ArrayList<FasciaOraria>();
		try {
			for (TimeSlot timeSlot : timeSlots) {
				FasciaOraria fasciaOraria = convertiTimeSlotToFasciaOraria(
						timeSlot, asList(new ParameterTS[] {}),
						asList(new ParameterGen[] {}), asList(new Price[] {}));
				setDettaglio(timeSlot, clientTS, fasciaOraria);
				fasceOrarie.add(fasciaOraria);
			}
		} catch (Exception e) {
			logger.error("errore nel recupero del dettaglio");
		}

		Client clientUser = newClient();
		Builder bldrUser = clientUser.target(
				BASE_URI_USER + "/getVehiclesForOP/tnt").request(
				APPLICATION_JSON);
		List<Vehicle> response = bldrUser.get(new GenericType<List<Vehicle>>() {
		});
		List<Veicolo> veicoli = new ArrayList<Veicolo>();
		if (response != null && response.size() > 0) {
			for (Vehicle vehicle : response) {
				ValoriVeicolo parametri = new ValoriVeicolo(
						vehicle.getServiceClass());
				veicoli.add(new Veicolo(vehicle.getState().name(), vehicle
						.getPlateNumber(),
						new GreenareaUser(vehicle.getAutista()), new GreenareaUser(
								vehicle.getSocietaDiTrasporto()),
						new OperatoreLogistico(new GreenareaUser("tnt")),
						parametri));
			}
		}
		List<ShippingOrder> shippingOrders = shippingOrderFacade.findAll("tnt");
		for (ShippingOrder shippingOrder : shippingOrders)
			shippingOrder.setShippingItems(shippingItemFacade
					.findAll(shippingOrder));
		List<Richiesta> richieste = convertiShippingOrdersToRichieste(shippingOrders);

		for (Veicolo veicolo : veicoli) {
			Missione missione = null;
			ValoriVeicolo valoriVeicolo = veicolo.getValori();
			Map<Richiesta, FasciaOraria> richiestePerFasciaOraria = associaFasciaOrariaARichiesta(
					richieste, convertiTimeSlotsToFasceOrarie(timeSlots),
					veicolo);
			if (richiestePerFasciaOraria.size() > 0) {
				Richiesta primaRichiesta = richiestePerFasciaOraria.keySet()
						.iterator().next();
				missione = new Missione(primaRichiesta.getFromName(),
						primaRichiesta.getFromName(), valoriVeicolo.getLenght()
								+ "", valoriVeicolo.getCarico() + "",
						valoriVeicolo.getTappe() + "", valoriVeicolo.getEuro()
								+ "", valoriVeicolo.getWeight() + "", WAITING,
						new ArrayList<Richiesta>(richiestePerFasciaOraria
								.keySet()), veicolo,
						new Timestamp(primaRichiesta.getOrarioInizio()
								.getTime()),
						richiestePerFasciaOraria.get(primaRichiesta));

				Client clientMission = newClient();
				Builder bldrMission = clientMission.target(
						BASE_URI_TS + "/TimeSlot/buildMission").request(
						APPLICATION_JSON);
				bldrMission.post(entity(missione, APPLICATION_JSON),
						Mission.class);
			}
		}
		return "";
	}

	public String costruisciMissioni() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			initDemoData.costruisciMissioni();
		} catch (Exception e) {
			logger.error("error init data", e);
		}
		FacesMessage msg = new FacesMessage(
				"GTG database inizializzato correttamente");
		context.addMessage(null, msg);
		return "";
	}

	public String spostamento1() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			initDemoData.spostamento1();
		} catch (Exception e) {
			logger.error("error init data", e);
		}
		FacesMessage msg = new FacesMessage(
				"GTG database inizializzato correttamente");
		context.addMessage(null, msg);
		return "";
	}

	public String spostamento2() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			initDemoData.spostamento2();
		} catch (Exception e) {
			logger.error("error init data", e);
		}
		FacesMessage msg = new FacesMessage(
				"GTG database inizializzato correttamente");
		context.addMessage(null, msg);
		return "";
	}

	public String buildMission() {
		return buildMission(new Date(), "01");
	}

	public String buildMission(Date date, String roundCode) {
		FacesContext context = FacesContext.getCurrentInstance();
		List<TransportServiceClass> transportSvcClassList = transportServiceClassFacade
				.findAll();
		List<Mission> result = new ArrayList<Mission>();
		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/findAllTimeSlot").request(
				APPLICATION_JSON);
		List<TimeSlot> timeSlots = bldr.get(new GenericType<List<TimeSlot>>() {
		});
		List<FasciaOraria> fasceOrarie = new ArrayList<FasciaOraria>();
		try {
			for (TimeSlot timeSlot : timeSlots) {
				FasciaOraria fasciaOraria = convertiTimeSlotToFasciaOraria(
						timeSlot, asList(new ParameterTS[] {}),
						asList(new ParameterGen[] {}), asList(new Price[] {}));
				setDettaglio(timeSlot, client, fasciaOraria);
				fasceOrarie.add(fasciaOraria);
			}
		} catch (Exception e) {
			logger.error("errore nel recupero del dettaglio");
		}
		for (TransportServiceClass trServiceClass : transportSvcClassList) {
			List<Transport> transportsToBeDone = missionBuilderBean
					.getTrasportiDaEseguire(trServiceClass, date, roundCode);
			String operatoreLogistico = "";
			if (!transportsToBeDone.isEmpty())
				operatoreLogistico = transportsToBeDone.get(0)
						.getOperatoreLogistico();
			Set<Vehicle> idleTrucks = missionBuilderBean.getVeicoliDisponibili(
					trServiceClass, roundCode, operatoreLogistico);
			Map<Transport, List<Freight>> allTransports = freightFacade
					.findAll(transportsToBeDone);
			for (Transport t : transportsToBeDone) {
				t.setFreightItems(allTransports.get(t));
			}
			List<Richiesta> richieste = convertiTransportsToRichieste(transportsToBeDone);
			logger.debug("inizio costruzione della missione sgapl");
			while (!idleTrucks.isEmpty() && !richieste.isEmpty()) {
				Vehicle truck = idleTrucks.iterator().next();
				Missione missioneEntry = new Missione();
				Veicolo veicolo = convertiVehicleToVeicolo(truck);
				missioneEntry.setVeicolo(veicolo);
				missioneEntry.setRichieste(richieste);
				Map<Richiesta, FasciaOraria> richiestePerFasciaOraria = associaFasciaOrariaARichiesta(
						richieste, fasceOrarie, veicolo);
				Mission missione = new Mission();
				aggiungiValoriAMissione(missioneEntry, richiestePerFasciaOraria);
				Timestamp timestamp = new Timestamp(date.getTime());
				missionBuilderBean.buildSgaplMission(missioneEntry, missione,
						timestamp);
				missionBuilderBean.buildCityLogisticsMission(missioneEntry,
						missione, timestamp);

				logger.debug(format("->Assigned %s for \"%s\" service class\n",
						missione, trServiceClass.getDescription()));
				List<ValueMission> valuesMission = missione.getValuesMission();
				missione.setValuesMission(null);
				if (valuesMission != null)
					for (ValueMission vm : valuesMission) {
						vm.setMission(missione);
						valueMissionFacade.create(vm);
					}
				missionFacade.create(missione);
				result.add(missione);
				idleTrucks.remove(truck);
				HashSet<Richiesta> trSet = new HashSet<Richiesta>();
				for (Richiesta t : richieste) {
					if (t.getStato().equals(assigned.name())) {
						trSet.add(t);
					}
				}
				richieste.removeAll(trSet);
			}
		}
		FacesMessage msg = new FacesMessage("Ho creato " + result.size()
				+ " missioni");
		context.addMessage(null, msg);
		return "";
	}
}
