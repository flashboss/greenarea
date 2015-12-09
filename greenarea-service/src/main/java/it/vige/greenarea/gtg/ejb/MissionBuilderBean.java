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

import static it.vige.greenarea.Constants.BASE_URI_USER;
import static it.vige.greenarea.Conversioni.convertiFasciaOrariaToTimeSlot;
import static it.vige.greenarea.Conversioni.convertiRichiestaToTransport;
import static it.vige.greenarea.Conversioni.convertiVeicoloToVehicle;
import static it.vige.greenarea.GTGsystem.olivetti;
import static it.vige.greenarea.cl.library.entities.FreightItemState.AVAILABLE;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.assigned;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.waiting;
import static it.vige.greenarea.cl.library.entities.TruckModels.FURGONATO;
import static it.vige.greenarea.cl.library.entities.TruckModels.directory;
import static it.vige.greenarea.dto.StatoVeicolo.IDLE;
import static it.vige.greenarea.geo.GisService.getDistance;
import static it.vige.greenarea.gtg.db.entities.supportclasses.DataTools.newExchangeStop;
import static it.vige.greenarea.gtg.db.entities.supportclasses.DataTools.newMission;
import static java.lang.String.format;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.cl.library.entities.TransportServiceClass;
import it.vige.greenarea.cl.library.entities.TruckModelInterface;
import it.vige.greenarea.cl.library.entities.TruckServiceClass;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.cl.scheduling.Scheduler;
import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.gtg.db.facades.MissionFacade;
import it.vige.greenarea.gtg.db.facades.TransportFacade;
import it.vige.greenarea.gtg.db.facades.TruckFacade;
import it.vige.greenarea.gtg.db.facades.TruckServiceClassFacade;

@Singleton
public class MissionBuilderBean {

	private Logger logger = getLogger(getClass());

	@EJB
	private TransportFacade transportFacade;
	@EJB
	private TruckServiceClassFacade truckServiceClassFacade;
	@EJB
	private MissionFacade missionFacade;
	@EJB
	private TruckFacade truckFacade;
	@EJB
	private Scheduler sc;

	private DateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy");

	private GeoLocation base = olivetti;

	public Timestamp puliziaMissioni(Date now) {
		Timestamp today = new Timestamp(now.getTime());
		// ripulisco le missioni:
		List<Mission> avaliableMissions = missionFacade.findAll();
		for (Mission m : avaliableMissions) {
			switch (m.getMissionState()) {
			case STARTED:
			case COMPLETED:
			case REJECTED:
				// non devo fare nulla

				break;
			case WAITING:
				// se e'scaduta la rilascio
				if (m.getExpireTime().before(today)) {
					missionFacade.completeMission(m);
				}
				break;
			}
		}
		return today;
	}

	public void buildCityLogisticsMission(Missione missione, Mission m, Timestamp today) {
		Timestamp tomorrow = new Timestamp(today.getTime() + 24 * 60 * 60 * 1000);
		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/findVehicles").request(APPLICATION_JSON);
		List<Vehicle> liVe = bldr.get(new GenericType<List<Vehicle>>() {
		});

		if (missione.getFasciaOraria() == null) {
			logger.error("La missione non ha fascia oraria!!!!! " + missione.getNome());
		}
		m.setTimeSlot(new TimeSlot(missione.getFasciaOraria().getId()));
		logger.info(missione.getDataInizio() + "");
		m.setStartTime(missione.getDataInizio());
		for (int i = 0; i < liVe.size(); i++)
			if (liVe.get(i).getPlateNumber().equals(missione.getVeicolo().getTarga()))
				m.setTruck(liVe.get(i));
		m.setName(missione.getNome());
		List<Parametro> arLiPa = missione.getFasciaOraria().getParametri();
		int idLu = 0;
		int idPe = 0;
		int idCa = 0;
		int idEm = 0;
		int idTa = 0;

		List<ValueMission> valuesMission = null;
		if (arLiPa.size() > 0)
			valuesMission = new ArrayList<ValueMission>();
		for (int i = 0; i < arLiPa.size(); i++) {

			if (arLiPa.get(i).getNome().equals("lunghezza")) {
				int id = 0;
				if (id == 0)
					idLu = arLiPa.get(i).getId();
				else
					idLu = id;
				ValueMission lunghezza = new ValueMission();
				lunghezza.setIdParameter(idLu);
				lunghezza.setValuePar(new Double(missione.getLunghezza()));
				valuesMission.add(lunghezza);

			} else if (arLiPa.get(i).getNome().equals("peso")) {
				int id = arLiPa.get(i).getIdGen();
				if (id == 0)
					idPe = arLiPa.get(i).getId();
				else
					idPe = id;
				ValueMission peso = new ValueMission();
				peso.setIdParameter(idPe);
				peso.setValuePar(new Double(missione.getPeso()));
				valuesMission.add(peso);

			} else if (arLiPa.get(i).getNome().equals("carico")) {
				int id = arLiPa.get(i).getIdGen();
				if (id == 0)
					idCa = arLiPa.get(i).getId();
				else
					idCa = id;
				ValueMission carico = new ValueMission();
				carico.setIdParameter(idCa);
				carico.setValuePar(new Double(missione.getCarico()));
				valuesMission.add(carico);

			} else if (arLiPa.get(i).getNome().equals("euro")) {
				int id = arLiPa.get(i).getIdGen();
				if (id == 0)
					idEm = arLiPa.get(i).getId();
				else
					idEm = id;
				ValueMission euro = new ValueMission();
				euro.setIdParameter(idEm);
				euro.setValuePar(new Double(missione.getEuro()));
				valuesMission.add(euro);

			} else if (arLiPa.get(i).getNome().equals("tappe")) {
				int id = arLiPa.get(i).getIdGen();
				if (id == 0)
					idTa = arLiPa.get(i).getId();
				else
					idTa = id;
				ValueMission tappe = new ValueMission();
				tappe.setIdParameter(idTa);
				tappe.setValuePar(new Double(missione.getTappe()));
				valuesMission.add(tappe);
			}
		}
		m.setValuesMission(valuesMission);
		m.setStartTime(new Timestamp(today.getTime()));
		m.setExpireTime(tomorrow);
	}

	public void buildSgaplMission(Missione missione, Mission mission, Timestamp today) {
		ExchangeStop baseForUpdate = newExchangeStop(base);
		ExchangeStop base_endForUpdate = newExchangeStop(base);
		Timestamp tomorrow = new Timestamp(today.getTime() + 24 * 60 * 60 * 1000);
		mission = updateTransports(missione, baseForUpdate, base_endForUpdate, mission);
		mission.setStartTime(today);
		mission.setExpireTime(tomorrow);
		mission.setCompany(missione.getVeicolo().getOperatoreLogistico().getId());
		mission.setTimeSlot(convertiFasciaOrariaToTimeSlot(missione.getFasciaOraria()));
	}

	public List<Transport> getTrasportiDaEseguire(TransportServiceClass transportSvcClass, Date date,
			String roundCode) {
		List<Transport> transports = transportFacade.findSySelection(waiting, transportSvcClass.getDescription());
		List<Transport> filteredTransports = new ArrayList<Transport>();
		/*
		 * secondo me qui manca un test per vedere se la lista e' vuota in tal
		 * caso salto tutto ilr esto e passo alla prossima classe... inserire:
		 */
		if ((transports == null) || (transports.isEmpty())) {
			return new ArrayList<Transport>();
		} else {
			try {
				for (Transport transport : transports) {
					String dateStr = dateFormat.format(transport.getDateMiss());
					if (dateFormat.parse(dateStr).compareTo(date) == 0 && transport.getRoundCode().equals(roundCode)) {
						filteredTransports.add(transport);
					}
				}
			} catch (ParseException e) {
				logger.error("formattazione della data", e);
			}
		}
		logger.debug(format("->Found %d transports for \"%s\" service class\n", transports.size(),
				transportSvcClass.getDescription()));
		return filteredTransports;
	}

	public List<Transport> getTrasportiDaEseguire(TransportServiceClass transportSvcClass, List<Richiesta> richieste,
			Date date, String roundCode) {
		List<Transport> transports = transportFacade.findSySelection(null, transportSvcClass.getDescription());
		List<Transport> filteredTransports = new ArrayList<Transport>();
		/*
		 * secondo me qui manca un test per vedere se la lista e' vuota in tal
		 * caso salto tutto ilr esto e passo alla prossima classe... inserire:
		 */
		if ((transports == null) || (transports.isEmpty())) {
			return new ArrayList<Transport>();
		} else {
			for (Transport transport : transports) {
				for (Richiesta richiesta : richieste) {
					if (transport.getAlfacode().equals(richiesta.getShipmentId())
							&& transport.getDateMiss().compareTo(date) == 0
							&& transport.getRoundCode().equals(roundCode)) {
						filteredTransports.add(transport);
					}
				}
			}
		}
		logger.debug(format("->Found %d transports for \"%s\" service class\n", transports.size(),
				transportSvcClass.getDescription()));
		return filteredTransports;
	}

	public Set<Vehicle> getVeicoliDisponibili(TransportServiceClass transportSvcClass, String roundCode,
			String operatoreLogistico) {
		// todo da fare dopo....
		List<TruckServiceClass> truckSvcClassList = truckServiceClassFacade.findAll();
		// cerco nella lista delle truck sevice class quella con lo stesso
		// nome:
		TruckServiceClass truckClass = null;
		for (int i = 0; i < truckSvcClassList.size(); i++) {
			if (truckSvcClassList.get(i).getDescription().equals(transportSvcClass.getDescription())) {
				truckClass = truckSvcClassList.get(i);
				break;
			}
		}
		List<Vehicle> trucks = null;
		if (truckClass != null) {
			trucks = truckFacade.findBySelection(IDLE, truckClass.getDescription(), roundCode, operatoreLogistico);
		}

		logger.debug(format("->Found %d trucks available for \"%s\" service class\n",
				trucks == null ? 0 : trucks.size(), transportSvcClass.getDescription()));

		Set<Vehicle> idleTrucks = new HashSet<Vehicle>();
		if (trucks != null)
			idleTrucks.addAll(trucks);
		return idleTrucks;
	}

	private Mission updateTransports(Missione missione, ExchangeStop baseForUpdate, ExchangeStop base_endForUpdate,
			Mission mission) {

		Vehicle truck = convertiVeicoloToVehicle(missione.getVeicolo());
		List<Richiesta> transportsToBeDone = missione.getRichieste();
		TruckModelInterface tli = directory.get(truck.getServiceClass().getModelV());
		tli = (tli != null ? tli : directory.get(FURGONATO));
		mission = newMission(baseForUpdate, base_endForUpdate, truck, mission);
		for (Richiesta richiesta : transportsToBeDone) {
			Transport t = convertiRichiestaToTransport(richiesta);
			Transport oldTransport = transportFacade.find(richiesta.getShipmentId());
			t.setServiceClass(oldTransport.getServiceClass());
			t.setTimeSlot(oldTransport.getTimeSlot());
			/*
			 * inserisco nella missione gli ExchangeStop origine e destinazione
			 * del trasporto Nota che non sappiamo ancora se il veicolo ha
			 * abbastanza spazio per effettuare il carico
			 */
			ExchangeStop from = newExchangeStop(t.getPickup());
			int i = addStop(mission, 0, from);
			from = mission.getExchangeStops().get(i); // attenzione se la
														// spedizione parte
														// da via olivetti
														// restituisce 0
														// percio' non
														// iserisce il
														// trasporto:
														// avviene nel caso
														// di trasporto
														// numero4
			ExchangeStop to = newExchangeStop(t.getDropdown());
			int e = addStop(mission, i, to);
			to = mission.getExchangeStops().get(e);
			/*
			 * *************** adesso effettuo la verifica che il trasporto
			 * possa essere effettuato
			 */
			boolean canBeLoaded = true;
			int k = i; // ricordo in k il numero ordinale del XS di sorgente
			/*
			 * su tutti gli XS compresi tra il punto di carico (k) ed il punto
			 * di scarico (e) verifico che il veicolo non sia overloaded
			 */
			List<Freight> freights = t.getFreightItems();
			while ((k < e) && canBeLoaded) {
				ExchangeStop xs = mission.getExchangeStops().get(k++);
				for (Freight fi : freights) {
					if (fi.getFreightState().equals(AVAILABLE)) {
						if (!tli.canload(xs.getTruckLoad(), fi)) {
							canBeLoaded = false;
							break;
						}
					}
				}
			}
			if (canBeLoaded) {
				/*
				 * Se si puo' caricare aggiorno la lista dei collecting e
				 * delivery items in to(i) e from(e)
				 */
				k = i;
				while (k < e) {
					ExchangeStop xs = mission.getExchangeStops().get(k++);
					for (Freight fi : freights)
						if (fi.getFreightState().equals(AVAILABLE)) {
							tli.load(xs.getTruckLoad(), fi);
						}
				}
				collectInExchangeStop(freights, from);
				deliverInExchangeStop(freights, to);
				t.setTransportState(assigned);
				t.setTimeSlot(convertiFasciaOrariaToTimeSlot(missione.getFasciaOraria()));
				sc.editSchedule(t);
				richiesta.setStato(assigned.name());
				mission.getTransports().add(t);
			} else {
				List<ExchangeStop> xss = mission.getExchangeStops();
				if (i != 0 && from.getCollectingList().isEmpty() && from.getDeliveryList().isEmpty())
					xss.remove(from);
				if (e != xss.size() - 1 && to.getCollectingList().isEmpty() && to.getDeliveryList().isEmpty())
					xss.remove(to);
			}

		}
		return mission;
	}

	private int addStop(Mission m, int start, ExchangeStop xs) {
		List<ExchangeStop> mxs = m.getExchangeStops();
		if (xs == null || start < 0 || !(start < mxs.size())) {
			throw new IllegalArgumentException();
		}
		boolean isColocated = false;
		int j = 0; // indice dell'ExchangeStop prima del quale inserire il nuovo
					// ExchangeStop
		int minDistance = Integer.MAX_VALUE; // memoria del valore minimo della
												// distanza
		int i;
		for (i = start; i < mxs.size() - 1; i++) {
			if (getDistance(mxs.get(i).getLocation().getLatitude(), mxs.get(i).getLocation().getLongitude(),
					xs.getLocation().getLatitude(), xs.getLocation().getLongitude()) < Mission.COLOCATED) {
				// ho trovato un ExchangeStop colocato con xs
				isColocated = true;
				break;
			}
			// Calcolo l'incremento del percorso tra i e i+1 se si inserisce XS
			int d = getDistance(xs.getLocation().getLatitude(), xs.getLocation().getLongitude(),
					mxs.get(i).getLocation().getLatitude(), mxs.get(i).getLocation().getLongitude())
					+ getDistance(xs.getLocation().getLatitude(), xs.getLocation().getLongitude(),
							mxs.get(i + 1).getLocation().getLatitude(), mxs.get(i + 1).getLocation().getLongitude())
					- getDistance(mxs.get(i).getLocation().getLatitude(), mxs.get(i).getLocation().getLongitude(),
							mxs.get(i + 1).getLocation().getLatitude(), mxs.get(i + 1).getLocation().getLongitude());
			if (d < minDistance) {
				// ha distanza minore rispetto a tutte le precedenti valutazioni
				minDistance = d;
				j = i + 1;
			}
		}
		/*
		 * verifico che il mezzo di trasporo sia in grado di farsi carico di
		 * questo nuovo trasporto (si noti che il problema puo' nascere solo nel
		 * caso di CollectingList, ovvero per le operazioni di carico
		 * 
		 * 
		 * Ora sistemo i dati: se si tratta di un ExchangeStop colocato faccio
		 * il merge delle operazioni di carico e scarico, altrimenti inserisco
		 * il nuovo ExchangeStop nel punto di costo minimo. In entranbi i casi
		 * aggiorno la situazione di carico del veicolo (TruckLoad)
		 */
		if (isColocated) {
			return i;
		} else {
			mxs.add(j, xs);
			return j;
		}
	}

	public void collectInExchangeStop(List<Freight> fs, ExchangeStop xs) {
		if (fs == null || xs == null)
			throw new IllegalArgumentException();
		for (Freight f : fs) {
			f.setPickUpPoint(xs);
			xs.getCollectingList().add(f);
		}
	}

	public void deliverInExchangeStop(List<Freight> fs, ExchangeStop xs) {
		if (fs == null || xs == null)
			throw new IllegalArgumentException();
		for (Freight f : fs) {
			f.setDropDownPoint(xs);
			xs.getDeliveryList().add(f);
		}
	}
}
