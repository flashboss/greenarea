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
package it.vige.greenarea;

import static it.vige.greenarea.cl.library.entities.FreightType.ALTRO_TIPO;
import static it.vige.greenarea.cl.library.entities.FreightType.DOCUMENTI;
import static it.vige.greenarea.dto.StatoMissione.values;
import static it.vige.greenarea.dto.TipoParametro.DA_DECIDERE;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.Filter;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.FreightItemState;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.ShippingItem;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.cl.library.entities.Transport.TransportState;
import it.vige.greenarea.cl.library.entities.TruckServiceClass;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.dto.AccessoVeicoli;
import it.vige.greenarea.dto.Address;
import it.vige.greenarea.dto.AperturaRichieste;
import it.vige.greenarea.dto.ChiusuraRichieste;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Filtro;
import it.vige.greenarea.dto.Fuel;
import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.GeoLocationInterface;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.Indirizzo;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Pacco;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Peso;
import it.vige.greenarea.dto.Prezzo;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.Ripetizione;
import it.vige.greenarea.dto.Sched;
import it.vige.greenarea.dto.StatoMissione;
import it.vige.greenarea.dto.StatoVeicolo;
import it.vige.greenarea.dto.TipoRichiesta;
import it.vige.greenarea.dto.TipologiaClassifica;
import it.vige.greenarea.dto.TipologiaParametro;
import it.vige.greenarea.dto.Tolleranza;
import it.vige.greenarea.dto.ValoriVeicolo;
import it.vige.greenarea.dto.Veicolo;

public class Conversioni {

	private static Logger logger = getLogger(Conversioni.class);

	public static Request convertiMissioneToRequest(Missione missione) {
		Request mission = null;
		if (missione != null) {
			mission = new Request();
			mission.setCompany(missione.getCompagnia());
			mission.setIdMission(missione.getNome() != null ? new Integer(missione.getNome()) : 0);
			mission.setDateMiss(missione.getDataInizio());
			mission.setIdTimeSlot(missione.getFasciaOraria() != null ? missione.getFasciaOraria().getId() : 0);
			mission.setCarPlate(missione.getVeicolo() != null ? missione.getVeicolo().getTarga() : null);
		}
		return mission;
	}

	public static List<Request> convertiMissioniToRequests(List<Missione> missioni) {
		List<Request> missions = null;
		if (missioni != null) {
			missions = new ArrayList<Request>();
			for (Missione missione : missioni)
				missions.add(convertiMissioneToRequest(missione));
		}
		return missions;
	}

	public static Missione convertiRequestToMissione(Request mission) {
		Missione missione = null;
		if (mission != null) {
			missione = new Missione(mission.getIdMission() + "", mission.getCompany(), "", "", "", "", "", null, null,
					new Veicolo("", mission.getCarPlate()), new Timestamp(mission.getDateMiss().getTime()),
					new FasciaOraria(mission.getIdTimeSlot(), null));
		}
		return missione;
	}

	public static List<Missione> convertiRequestsToMissioni(List<Request> missions) {
		List<Missione> missioni = null;
		if (missions != null) {
			missioni = new ArrayList<Missione>();
			for (Request mission : missions)
				missioni.add(convertiRequestToMissione(mission));
		}
		return missioni;
	}

	public static Mission convertiMissioneToMission(Missione missione) {
		Mission mission = null;
		if (missione != null) {
			mission = new Mission();
			mission.setCompany(missione.getCompagnia());
			mission.setMissionState(missione.getStato());
			mission.setName(missione.getNome());
			mission.setOwnerUser(missione.getVeicolo().getOperatoreLogistico().getId());
			mission.setStartTime(missione.getDataInizio());
			mission.setTimeSlot(convertiFasciaOrariaToTimeSlot(missione.getFasciaOraria()));
			mission.setTruck(convertiVeicoloToVehicle(missione.getVeicolo()));
			ValueMission lunghezza = new ValueMission();
			lunghezza.setValuePar(missione.getLunghezza() == null || missione.getLunghezza().isEmpty() ? 0.0
					: new Double(missione.getLunghezza()));
			ValueMission euro = new ValueMission();
			euro.setValuePar(
					missione.getEuro() == null || missione.getEuro().isEmpty() ? 0.0 : new Double(missione.getEuro()));
			ValueMission peso = new ValueMission();
			peso.setValuePar(
					missione.getPeso() == null || missione.getPeso().isEmpty() ? 0.0 : new Double(missione.getPeso()));
			ValueMission tappe = new ValueMission();
			tappe.setValuePar(missione.getTappe() == null || missione.getTappe().isEmpty() ? 0.0
					: new Double(missione.getTappe()));
			mission.setValuesMission(asList(new ValueMission[] { lunghezza, euro, peso, tappe }));
		}
		return mission;
	}

	public static List<Mission> convertiMissioniToMissions(List<Missione> missioni) {
		List<Mission> missions = null;
		if (missioni != null) {
			missions = new ArrayList<Mission>();
			for (Missione missione : missioni)
				missions.add(convertiMissioneToMission(missione));
		}
		return missions;
	}

	public static Missione convertiMissionToMissione(Mission mission) {
		Missione missione = null;
		if (mission != null) {
			missione = new Missione(mission.getName(), mission.getCompany(), "", "", "", "", "",
					mission.getMissionState(), convertiTransportsToRichieste(mission.getTransports()),
					convertiVehicleToVeicolo(mission.getTruck()), mission.getStartTime(),
					convertiTimeSlotToFasciaOraria(mission.getTimeSlot(), asList(new ParameterTS[] {}),
							asList(new ParameterGen[] {}), asList(new Price[] {})));
		}
		return missione;
	}

	public static List<Missione> convertiMissionsToMissioni(List<Mission> missions) {
		List<Missione> missioni = null;
		if (missions != null) {
			missioni = new ArrayList<Missione>();
			for (Mission mission : missions)
				missioni.add(convertiMissionToMissione(mission));
		}
		return missioni;
	}

	public static Freight convertiPaccoToFreight(Pacco pacco) {
		Freight freight = null;
		if (pacco != null) {
			freight = new Freight();
			freight.setCodeId(pacco.getItemID());
			freight.setDescription(pacco.getDescrizione());
			String tipo = pacco.getAttributi().get("Type");
			freight.setFt(tipo.equals(DOCUMENTI.name()) ? DOCUMENTI : ALTRO_TIPO);
			freight.setVolume(new Double(pacco.getAttributi().get("Volume")));
			freight.setWeight(new Double(pacco.getAttributi().get("Weight")));
			freight.setWidth(new Double(pacco.getAttributi().get("Width")));
			freight.setLeng(new Double(pacco.getAttributi().get("Length")));
			freight.setHeight(new Double(pacco.getAttributi().get("Height")));
			freight.setStackable(new Boolean(pacco.getAttributi().get("Stackable")));
			freight.setKeepUpStanding(new Boolean(pacco.getAttributi().get("KeepUpStanding")));
			String state = pacco.getAttributi().get("State");
			if (state != null)
				freight.setFreightState(FreightItemState.valueOf(state));
		}
		return freight;
	}

	public static List<Freight> convertiPacchiToFreights(List<Pacco> pacchi) {
		List<Freight> freights = null;
		if (pacchi != null) {
			freights = new ArrayList<Freight>();
			for (Pacco pacco : pacchi)
				freights.add(convertiPaccoToFreight(pacco));
		}
		return freights;
	}

	public static ShippingItem convertiPaccoToShippingItem(Pacco pacco) {
		ShippingItem shippingItem = null;
		if (pacco != null) {
			shippingItem = new ShippingItem();
			shippingItem.setId(pacco.getItemID());
			shippingItem.setDescription(pacco.getDescrizione());
			HashMap<String, String> attributes = new HashMap<String, String>();
			shippingItem.setAttributes(attributes);
			attributes.put("Volume", pacco.getAttributi().get("Volume"));
			attributes.put("Weight", pacco.getAttributi().get("Weight"));
			attributes.put("Width", pacco.getAttributi().get("Width"));
			attributes.put("Length", pacco.getAttributi().get("Length"));
			attributes.put("Height", pacco.getAttributi().get("Height"));
			attributes.put("Stackable", pacco.getAttributi().get("Stackable"));
			attributes.put("KeepUpStanding", pacco.getAttributi().get("KeepUpStanding"));
			attributes.put("Type", pacco.getAttributi().get("Type"));
			attributes.put("State", pacco.getAttributi().get("State"));
		}
		return shippingItem;
	}

	public static List<ShippingItem> convertiPacchiToShippingItems(List<Pacco> pacchi) {
		List<ShippingItem> shippingItems = null;
		if (pacchi != null) {
			shippingItems = new ArrayList<ShippingItem>();
			for (Pacco pacco : pacchi)
				shippingItems.add(convertiPaccoToShippingItem(pacco));
		}
		return shippingItems;
	}

	public static Pacco convertiFreightToPacco(Freight freight) {
		Pacco pacco = null;
		if (freight != null) {
			Map<String, String> attributi = new HashMap<String, String>();
			attributi.put("Volume", freight.getVolume() + "");
			attributi.put("Weight", freight.getWeight() + "");
			attributi.put("Type", freight.getFt().name());
			attributi.put("Height", freight.getHeight() + "");
			attributi.put("Length", freight.getLeng() + "");
			attributi.put("Width", freight.getWidth() + "");
			attributi.put("Stackable", freight.isStackable() + "");
			attributi.put("KeepUpStanding", freight.isKeepUpStanding() + "");
			attributi.put("State", freight.getFreightState() != null ? freight.getFreightState().name() : "");
			pacco = new Pacco(freight.getCodeId(), freight.getDescription(), attributi);
		}
		return pacco;
	}

	public static List<Pacco> convertiFreightsToPacchi(List<Freight> freights) {
		List<Pacco> pacchi = null;
		if (freights != null) {
			pacchi = new ArrayList<Pacco>();
			for (Freight freight : freights)
				pacchi.add(convertiFreightToPacco(freight));
		}
		return pacchi;
	}

	public static TimeSlot convertiFasciaOrariaToTimeSlot(FasciaOraria fasciaOraria) {
		TimeSlot timeSlot = null;
		if (fasciaOraria != null) {
			timeSlot = new TimeSlot(fasciaOraria.getId());
			DateFormat giornata = new SimpleDateFormat("d-MM-yyyy");
			DateFormat orario = new SimpleDateFormat("HH:mm");
			timeSlot.setDayFinish(giornata.format(fasciaOraria.getDataFine()));
			timeSlot.setDayStart(giornata.format(fasciaOraria.getDataInizio()));
			timeSlot.setPa(fasciaOraria.getPa().getId());
			timeSlot.setStartTS(orario.format(fasciaOraria.getOrarioInizio()));
			timeSlot.setFinishTS(orario.format(fasciaOraria.getOrarioFine()));
			timeSlot.setTimeToAcceptRequest(AperturaRichieste.valueOf(fasciaOraria.getAperturaRichieste()));
			timeSlot.setTimeToRun(ChiusuraRichieste.valueOf(fasciaOraria.getChiusuraRichieste()));
			timeSlot.setTimeToStopRequest(ChiusuraRichieste.valueOf(fasciaOraria.getChiusuraRichieste()));
			timeSlot.setTollerance(Tolleranza.valueOf(fasciaOraria.getTolleranza()));
			timeSlot.setVikInd(TipologiaClassifica.valueOf(fasciaOraria.getTipologiaClassifica()));
			timeSlot.setWmy(Ripetizione.valueOf(fasciaOraria.getRipetitivitaPolicy()));
			timeSlot.setRoundCode(fasciaOraria.getGa());
		}
		return timeSlot;
	}

	public static List<TimeSlot> convertiFasceOrarieToTimeSlots(List<FasciaOraria> fasceOrarie) {
		List<TimeSlot> timeSlots = null;
		if (fasceOrarie != null) {
			timeSlots = new ArrayList<TimeSlot>();
			for (FasciaOraria fasciaOraria : fasceOrarie)
				timeSlots.add(convertiFasciaOrariaToTimeSlot(fasciaOraria));
		}
		return timeSlots;
	}

	public static FasciaOraria convertiTimeSlotToFasciaOraria(TimeSlot timeSlot, List<ParameterTS> parameterTSs,
			List<ParameterGen> parameterGens, List<Price> prices) {
		FasciaOraria fasciaOraria = null;
		if (timeSlot != null) {
			DateFormat giornata = new SimpleDateFormat("d-MM-yyyy");
			DateFormat orario = new SimpleDateFormat("HH:mm");
			fasciaOraria = new FasciaOraria();
			fasciaOraria.setId(timeSlot.getIdTS());
			try {
				if (timeSlot.getDayFinish() != null)
					fasciaOraria.setDataFine(giornata.parse(timeSlot.getDayFinish()));
				if (timeSlot.getDayStart() != null)
					fasciaOraria.setDataInizio(giornata.parse(timeSlot.getDayStart()));
				if (timeSlot.getFinishTS() != null)
					fasciaOraria.setOrarioFine(orario.parse(timeSlot.getFinishTS()));
				if (timeSlot.getStartTS() != null)
					fasciaOraria.setOrarioInizio(orario.parse(timeSlot.getStartTS()));
			} catch (ParseException e) {
				logger.error("conversione date", e);
			}
			fasciaOraria.setTipologiaClassifica(timeSlot.getVikInd() == null ? null : timeSlot.getVikInd().name());
			fasciaOraria.setTolleranza(timeSlot.getTollerance() == null ? null : timeSlot.getTollerance().name());
			fasciaOraria.setRipetitivitaPolicy(timeSlot.getWmy() == null ? null : timeSlot.getWmy().name());
			fasciaOraria.setAperturaRichieste(
					timeSlot.getTimeToAcceptRequest() == null ? null : timeSlot.getTimeToAcceptRequest().name());
			fasciaOraria.setChiusuraRichieste(
					timeSlot.getTimeToStopRequest() == null ? null : timeSlot.getTimeToStopRequest().name());
			fasciaOraria.setGa("");
			fasciaOraria.setParametri(convertiParameterGensToParametri(parameterGens));
			fasciaOraria.setPrezzi(convertiPricesToPrezzi(prices, parameterTSs, parameterGens));
			fasciaOraria.setPa(new GreenareaUser(timeSlot.getPa()));
			fasciaOraria.setGa(timeSlot.getRoundCode());
		}
		return fasciaOraria;
	}

	public static FasciaOraria convertiTimeSlotToFasciaOraria(TimeSlot timeSlot) {
		FasciaOraria fasciaOraria = null;
		if (timeSlot != null) {
			DateFormat giornata = new SimpleDateFormat("d-MM-yyyy");
			DateFormat orario = new SimpleDateFormat("HH:mm");
			fasciaOraria = new FasciaOraria();
			fasciaOraria.setId(timeSlot.getIdTS());
			try {
				fasciaOraria.setDataFine(giornata.parse(timeSlot.getDayFinish() + " " + timeSlot.getFinishTS()));
				fasciaOraria.setDataInizio(giornata.parse(timeSlot.getDayStart() + " " + timeSlot.getStartTS()));
				fasciaOraria.setOrarioFine(orario.parse(timeSlot.getFinishTS()));
				fasciaOraria.setOrarioInizio(orario.parse(timeSlot.getStartTS()));
			} catch (ParseException e) {
				logger.error("conversione date", e);
			}
			fasciaOraria.setTipologiaClassifica(timeSlot.getVikInd().name());
			fasciaOraria.setTolleranza(timeSlot.getTollerance().name());
			fasciaOraria.setRipetitivitaPolicy(timeSlot.getWmy().name());
			fasciaOraria.setAperturaRichieste(timeSlot.getTimeToAcceptRequest().name());
			fasciaOraria.setChiusuraRichieste(timeSlot.getTimeToStopRequest().name());
			fasciaOraria.setGa("");
			fasciaOraria.setPa(new GreenareaUser(timeSlot.getPa()));
			fasciaOraria.setGa(timeSlot.getRoundCode());
		}
		return fasciaOraria;
	}

	public static List<FasciaOraria> convertiTimeSlotsToFasceOrarie(List<TimeSlot> timeSlots,
			List<ParameterTS> parameterTSs, List<ParameterGen> parameterGens, List<Price> prices) {
		List<FasciaOraria> fascieOrarie = null;
		if (timeSlots != null) {
			fascieOrarie = new ArrayList<FasciaOraria>();
			for (TimeSlot timeSlot : timeSlots)
				fascieOrarie.add(convertiTimeSlotToFasciaOraria(timeSlot, parameterTSs, parameterGens, prices));
		}
		return fascieOrarie;
	}

	public static List<FasciaOraria> convertiTimeSlotsToFasceOrarie(List<TimeSlot> timeSlots) {
		List<FasciaOraria> fascieOrarie = null;
		if (timeSlots != null) {
			fascieOrarie = new ArrayList<FasciaOraria>();
			for (TimeSlot timeSlot : timeSlots)
				fascieOrarie.add(convertiTimeSlotToFasciaOraria(timeSlot));
		}
		return fascieOrarie;
	}

	public static Vehicle convertiVeicoloToVehicle(Veicolo veicolo) {
		Vehicle vehicle = null;
		if (veicolo != null) {
			vehicle = new Vehicle();
			vehicle.setAutista(veicolo.getAutista().getId());
			vehicle.setRoundCode(veicolo.getRoundCode());
			vehicle.setVin(veicolo.getVin());
			vehicle.setCodiceFiliale(veicolo.getCodiceFiliale());
			vehicle.setOperatoreLogistico(veicolo.getOperatoreLogistico().getId());
			vehicle.setPlateNumber(veicolo.getTarga());
			vehicle.setSocietaDiTrasporto(veicolo.getSocietaDiTrasporto().getId());
			vehicle.setState(StatoVeicolo.valueOf(veicolo.getStato()));
			ValoriVeicolo valori = veicolo.getValori();
			if (valori != null) {
				TruckServiceClass truckServiceClass = new TruckServiceClass();
				truckServiceClass.setDescription(valori.getDescription());
				truckServiceClass.setEmissionV(valori.getEmission());
				truckServiceClass.setEURO(valori.getEuro());
				truckServiceClass.setFuelV(Fuel.valueOf(valori.getFuel()));
				truckServiceClass.setId(valori.getId());
				truckServiceClass.setLenghtV(valori.getLenght());
				truckServiceClass.setMakeV(valori.getBaseName());
				truckServiceClass.setModelV(valori.getModel());
				truckServiceClass.setWeightV(valori.getWeight());
				truckServiceClass.setConsumption(valori.getConsumiPresunti());
				vehicle.setServiceClass(truckServiceClass);
			}
		}
		return vehicle;
	}

	public static List<Vehicle> convertiVeicoliToVehicles(List<Veicolo> veicoli) {
		List<Vehicle> vehicles = null;
		if (veicoli != null) {
			vehicles = new ArrayList<Vehicle>();
			for (Veicolo veicolo : veicoli)
				vehicles.add(convertiVeicoloToVehicle(veicolo));
		}
		return vehicles;
	}

	public static Veicolo convertiVehicleToVeicolo(Vehicle vehicle) {
		Veicolo veicolo = null;
		if (vehicle != null) {
			veicolo = new Veicolo();
			veicolo.setAutista(new GreenareaUser(vehicle.getAutista()));
			veicolo.setOperatoreLogistico(new OperatoreLogistico(new GreenareaUser(vehicle.getOperatoreLogistico())));
			veicolo.setRoundCode(vehicle.getRoundCode());
			veicolo.setCodiceFiliale(vehicle.getCodiceFiliale());
			veicolo.setVin(vehicle.getVin());
			veicolo.setSocietaDiTrasporto(new GreenareaUser(vehicle.getSocietaDiTrasporto()));
			veicolo.setStato(vehicle.getState().name());
			veicolo.setTarga(vehicle.getPlateNumber());
			TruckServiceClass truckServiceClass = vehicle.getServiceClass();
			if (truckServiceClass != null)
				veicolo.setValori(new ValoriVeicolo(truckServiceClass));
		}
		return veicolo;
	}

	public static List<Veicolo> convertiVehiclesToVeicoli(List<Vehicle> vehicles) {
		List<Veicolo> veicoli = null;
		if (vehicles != null) {
			veicoli = new ArrayList<Veicolo>();
			for (Vehicle vehicle : vehicles)
				veicoli.add(convertiVehicleToVeicolo(vehicle));
		}
		return veicoli;
	}

	public static GeoLocation convertiIndirizzoToGeoLocation(Indirizzo indirizzo) {
		GeoLocation geoLocation = new GeoLocation();
		geoLocation.setAdminAreaLevel1(indirizzo.getRegion());
		geoLocation.setAdminAreaLevel2(indirizzo.getProvince());
		geoLocation.setCity(indirizzo.getCity());
		geoLocation.setCountry(indirizzo.getCountry());
		geoLocation.setNumber(indirizzo.getNumber());
		geoLocation.setStreet(indirizzo.getStreet());
		geoLocation.setZipCode(indirizzo.getZipCode());
		geoLocation.setLatitude(indirizzo.getLatitude());
		geoLocation.setLongitude(indirizzo.getLongitude());
		return geoLocation;
	}

	public static List<GeoLocation> convertiIndirizziToGeoLocations(List<Indirizzo> indirizzi) {
		List<GeoLocation> geoLocations = null;
		if (indirizzi != null) {
			geoLocations = new ArrayList<GeoLocation>();
			for (Indirizzo indirizzo : indirizzi)
				geoLocations.add(convertiIndirizzoToGeoLocation(indirizzo));
		}
		return geoLocations;
	}

	public static Indirizzo convertiGeoLocationToIndirizzo(GeoLocation geoLocation) {
		Indirizzo indirizzo = new Indirizzo();
		indirizzo.setRegion(geoLocation.getAdminAreaLevel1());
		indirizzo.setProvince(geoLocation.getAdminAreaLevel2());
		indirizzo.setCity(geoLocation.getCity());
		indirizzo.setCountry(geoLocation.getCountry());
		indirizzo.setNumber(geoLocation.getNumber());
		indirizzo.setStreet(geoLocation.getStreet());
		indirizzo.setZipCode(geoLocation.getZipCode());
		indirizzo.setLatitude(geoLocation.getLatitude());
		indirizzo.setLongitude(geoLocation.getLongitude());
		return indirizzo;
	}

	public static List<Indirizzo> convertiGeoLocationsToIndirizzi(List<GeoLocation> geoLocations) {
		List<Indirizzo> indirizzi = null;
		if (geoLocations != null) {
			indirizzi = new ArrayList<Indirizzo>();
			for (GeoLocation geoLocation : geoLocations)
				indirizzi.add(convertiGeoLocationToIndirizzo(geoLocation));
		}
		return indirizzi;
	}

	public static Indirizzo convertiDBGeoLocationToIndirizzo(DBGeoLocation dbGeoLocation) {
		Indirizzo indirizzo = new Indirizzo();
		indirizzo.setRegion(dbGeoLocation.getAdminAreaLevel1());
		indirizzo.setProvince(dbGeoLocation.getAdminAreaLevel2());
		indirizzo.setCity(dbGeoLocation.getCity());
		indirizzo.setCountry(dbGeoLocation.getCountry());
		indirizzo.setNumber(dbGeoLocation.getNumber());
		indirizzo.setStreet(dbGeoLocation.getStreet());
		indirizzo.setZipCode(dbGeoLocation.getZipCode());
		indirizzo.setLatitude(dbGeoLocation.getLatitude());
		indirizzo.setLongitude(dbGeoLocation.getLongitude());
		return indirizzo;
	}

	public static List<Indirizzo> convertiDBGeoLocationsToIndirizzi(List<DBGeoLocation> dbGeoLocations) {
		List<Indirizzo> indirizzi = null;
		if (dbGeoLocations != null) {
			indirizzi = new ArrayList<Indirizzo>();
			for (DBGeoLocation geoLocation : dbGeoLocations)
				indirizzi.add(convertiDBGeoLocationToIndirizzo(geoLocation));
		}
		return indirizzi;
	}

	public static Address convertiIndirizzoToAddress(Indirizzo indirizzo) {
		Address address = new Address();
		address.setCity(indirizzo.getCity());
		address.setCountry(indirizzo.getCountry());
		address.setNumber(indirizzo.getNumber());
		address.setProvince(indirizzo.getProvince());
		address.setRegion(indirizzo.getRegion());
		address.setStreet(indirizzo.getStreet());
		address.setZipCode(indirizzo.getZipCode());
		return address;
	}

	public static List<Address> convertiIndirizziToAddresses(List<Indirizzo> indirizzi) {
		List<Address> addresses = null;
		if (indirizzi != null) {
			addresses = new ArrayList<Address>();
			for (Indirizzo indirizzo : indirizzi)
				addresses.add(convertiIndirizzoToAddress(indirizzo));
		}
		return addresses;
	}

	public static DBGeoLocation convertiIndirizzoToDBGeoLocation(Indirizzo indirizzo) {
		DBGeoLocation geoLocation = new DBGeoLocation();
		geoLocation.setAdminAreaLevel1(indirizzo.getRegion());
		geoLocation.setAdminAreaLevel2(indirizzo.getProvince());
		geoLocation.setCity(indirizzo.getCity());
		geoLocation.setCountry(indirizzo.getCountry());
		geoLocation.setNumber(indirizzo.getNumber());
		geoLocation.setStreet(indirizzo.getStreet());
		geoLocation.setZipCode(indirizzo.getZipCode());
		geoLocation.setLatitude(indirizzo.getLatitude());
		geoLocation.setLongitude(indirizzo.getLongitude());
		return geoLocation;
	}

	public static List<DBGeoLocation> convertiIndirizziToDBGeoLocations(List<Indirizzo> indirizzi) {
		List<DBGeoLocation> dbGeoLocations = null;
		if (indirizzi != null) {
			dbGeoLocations = new ArrayList<DBGeoLocation>();
			for (Indirizzo indirizzo : indirizzi)
				dbGeoLocations.add(convertiIndirizzoToDBGeoLocation(indirizzo));
		}
		return dbGeoLocations;
	}

	public static Transport convertiRichiestaToTransport(Richiesta richiesta) {
		Transport transport = null;
		if (richiesta != null) {
			transport = new Transport();
			transport.setAlfacode(richiesta.getShipmentId());
			transport.setDestination(convertiIndirizzoToGeoLocation(richiesta.getToAddress()));
			transport.setDropdown(convertiIndirizzoToDBGeoLocation(richiesta.getToAddress()));
			transport.getDropdown().setName(richiesta.getToName());
			transport.setFreightItems(convertiPacchiToFreights(asList(richiesta.getPacchi())));
			transport.setPickup(convertiIndirizzoToDBGeoLocation(richiesta.getFromAddress()));
			transport.getPickup().setName(richiesta.getFromName());
			transport.setSource(convertiIndirizzoToGeoLocation(richiesta.getFromAddress()));
			transport.setTimeAccept(richiesta.getOrarioInizio());
			transport.setTimeClosing(richiesta.getOrarioFine());
			transport.setTimeRank(richiesta.getOrarioFine());
			transport.setTipo(TipoRichiesta.valueOf(richiesta.getTipo()));
			transport.setTransportState(TransportState.valueOf(richiesta.getStato()));
			if (transport.getFreightItems() != null) {
				double totalVolume = 0;
				for (Freight freight : transport.getFreightItems()) {
					totalVolume = totalVolume + freight.getVolume();
					transport.setTotalVolume(totalVolume);
					freight.setTransport(transport);
				}
			}
			transport.setDateMiss(richiesta.getDataMissione());
			if (richiesta.getOperatoreLogistico() != null)
				transport.setOperatoreLogistico(richiesta.getOperatoreLogistico().getId());
			transport.setCodiceFiliale(richiesta.getCodiceFiliale());
			transport.setRoundCode(richiesta.getRoundCode());
		}
		return transport;
	}

	public static List<Transport> convertiRichiesteToTransports(List<Richiesta> richieste) {
		List<Transport> transports = null;
		if (richieste != null) {
			transports = new ArrayList<Transport>();
			for (Richiesta richiesta : richieste)
				transports.add(convertiRichiestaToTransport(richiesta));
		}
		return transports;
	}

	public static ShippingOrder convertiRichiestaToShippingOrder(Richiesta richiesta) {
		ShippingOrder shippingOrder = null;
		if (richiesta != null) {
			shippingOrder = new ShippingOrder();
			shippingOrder.setId(richiesta.getShipmentId());
			DBGeoLocation destinatario = convertiIndirizzoToDBGeoLocation(richiesta.getToAddress());
			destinatario.setName(richiesta.getToName());
			shippingOrder.setDestinatario(destinatario);
			DBGeoLocation mittente = convertiIndirizzoToDBGeoLocation(richiesta.getFromAddress());
			mittente.setName(richiesta.getFromName());
			shippingOrder.setMittente(mittente);
			shippingOrder.setShippingItems(convertiPacchiToShippingItems(asList(richiesta.getPacchi())));
			if (richiesta.getOperatoreLogistico() != null)
				shippingOrder.setOperatoreLogistico(richiesta.getOperatoreLogistico().getId());
			if (richiesta.getOrarioInizio() != null)
				shippingOrder.setCreationTimestamp(new Timestamp(richiesta.getOrarioInizio().getTime()));
			shippingOrder.setRoundCode(richiesta.getRoundCode());
			shippingOrder.setCodiceFiliale(richiesta.getCodiceFiliale());
		}
		return shippingOrder;
	}

	public static List<ShippingOrder> convertiRichiesteToShippingOrders(List<Richiesta> richieste) {
		List<ShippingOrder> shippingOrders = null;
		if (richieste != null) {
			shippingOrders = new ArrayList<ShippingOrder>();
			for (Richiesta richiesta : richieste)
				shippingOrders.add(convertiRichiestaToShippingOrder(richiesta));
		}
		return shippingOrders;
	}

	public static Richiesta convertiTransportToRichiesta(Transport transport) {
		Richiesta richiesta = null;
		if (transport != null) {
			richiesta = new Richiesta();
			if (transport.getSource() != null)
				richiesta.setFromAddress(convertiGeoLocationToIndirizzo(transport.getSource()));
			if (transport.getPickup() != null)
				richiesta.setFromName(transport.getPickup().getName());
			if (transport.getDestination() != null)
				richiesta.setToAddress(convertiGeoLocationToIndirizzo(transport.getDestination()));
			if (transport.getDropdown() != null)
				richiesta.setToName(transport.getDropdown().getName());
			if (transport.getShippingOrder() != null)
				richiesta.setNote(transport.getShippingOrder().getNote());
			richiesta.setOrarioFine(transport.getTimeClosing());
			richiesta.setOrarioInizio(transport.getTimeAccept());
			if (transport.getFreightItems() != null)
				richiesta.setPacchi(convertiFreightsToPacchi(transport.getFreightItems()).toArray(new Pacco[0]));
			richiesta.setShipmentId(transport.getAlfacode());
			if (transport.getTransportState() != null)
				richiesta.setStato(transport.getTransportState().name());
			if (transport.getShippingOrder() != null)
				richiesta.setTerminiDiConsegna(transport.getShippingOrder().getDeliveryTerms());
			if (transport.getTipo() != null)
				richiesta.setTipo(transport.getTipo().name());
			richiesta.setDataMissione(transport.getDateMiss());
			richiesta.setOperatoreLogistico(
					new OperatoreLogistico(new GreenareaUser(transport.getOperatoreLogistico())));
			richiesta.setCodiceFiliale(transport.getCodiceFiliale());
			richiesta.setFasciaOraria(convertiTimeSlotToFasciaOraria(transport.getTimeSlot()));
			richiesta.setRoundCode(transport.getRoundCode());
		}
		return richiesta;
	}

	public static List<Richiesta> convertiTransportsToRichieste(List<Transport> transports) {
		List<Richiesta> richieste = null;
		if (transports != null) {
			richieste = new ArrayList<Richiesta>();
			for (Transport transport : transports)
				richieste.add(convertiTransportToRichiesta(transport));
		}
		return richieste;
	}

	public static Richiesta convertiShippingOrderToRichiesta(ShippingOrder shippingOrder) {
		Richiesta richiesta = null;
		if (shippingOrder != null) {
			richiesta = new Richiesta();
			if (shippingOrder.getMittente() != null) {
				richiesta.setFromAddress(convertiDBGeoLocationToIndirizzo(shippingOrder.getMittente()));
				richiesta.setFromName(shippingOrder.getMittente().getName());
			}
			richiesta.setNote(shippingOrder.getNote());
			if (shippingOrder.getShippingItems() != null)
				richiesta.setPacchi(
						convertiShippingItemsToPacchi(shippingOrder.getShippingItems()).toArray(new Pacco[0]));
			richiesta.setShipmentId(shippingOrder.getId());
			richiesta.setTerminiDiConsegna(shippingOrder.getDeliveryTerms());
			richiesta.setToAddress(convertiDBGeoLocationToIndirizzo(shippingOrder.getDestinatario()));
			if (shippingOrder.getDestinatario() != null)
				richiesta.setToName(shippingOrder.getDestinatario().getName());
			richiesta.setDataMissione(shippingOrder.getCreationTimestamp());
			richiesta.setOperatoreLogistico(
					new OperatoreLogistico(new GreenareaUser(shippingOrder.getOperatoreLogistico())));
			richiesta.setOrarioInizio(shippingOrder.getCreationTimestamp());
			richiesta.setOrarioFine(shippingOrder.getCreationTimestamp());
			richiesta.setCodiceFiliale(shippingOrder.getCodiceFiliale());
			richiesta.setRoundCode(shippingOrder.getRoundCode());
		}
		return richiesta;
	}

	public static List<Richiesta> convertiShippingOrdersToRichieste(List<ShippingOrder> shippingOrders) {
		List<Richiesta> richieste = null;
		if (shippingOrders != null) {
			richieste = new ArrayList<Richiesta>();
			for (ShippingOrder shippingOrder : shippingOrders)
				richieste.add(convertiShippingOrderToRichiesta(shippingOrder));
		}
		return richieste;
	}

	public static Sched convertiTransportToSched(Transport transport) {
		Sched sched = null;
		if (transport != null) {
			sched = new Sched();
			sched.setDateMiss(transport.getDateMiss());
			TimeSlot timeSlot = transport.getTimeSlot();
			if (timeSlot != null) {
				sched.setIdTimeSlot(timeSlot.getIdTS());
				String timeTS = timeSlot.getStartTS() + " " + timeSlot.getFinishTS();
				DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				String dateTs = fmt.format(addDays(transport.getDateMiss(), -1)) + " " + timeTS;
				sched.setTimeSlot(dateTs);
			}
			ShippingOrder shippingOrder = transport.getShippingOrder();
			if (shippingOrder != null)
				sched.setRequest(shippingOrder.getId());
			sched.setTimeAccept(transport.getTimeAccept());
			sched.setTimeClosing(transport.getTimeClosing());
			sched.setTimeRank(transport.getTimeRank());
			sched.setRoundCode(transport.getRoundCode());
		}
		return sched;
	}

	public static List<Sched> convertiTransportsToScheds(List<Transport> transports) {
		List<Sched> scheds = null;
		if (transports != null) {
			scheds = new ArrayList<Sched>();
			for (Transport transport : transports)
				scheds.add(convertiTransportToSched(transport));
		}
		return scheds;
	}

	public static String convertTimestampToString(Timestamp t) {

		String result;
		// create SimpleDateFormat object with desired date format
		SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyyMMddHHmm");
		// parse the date into another format
		result = sdfDestination.format(t);
		return result;
	}

	public static Timestamp convertStringToTimestamp(String s) {

		Timestamp result = null;
		// create SimpleDateFormat object with desired date format
		String format = "yyyyMMddHHmm";
		SimpleDateFormat sdfDestination = new SimpleDateFormat(format);
		while (s.length() < format.length()) {
			s = s + "0";
		}
		if (s.length() > format.length()) {
			s = s.substring(0, format.length());
		}
		try {
			// parse the date into another format
			Date d = sdfDestination.parse(s);
			result = new Timestamp(d.getTime());
		} catch (ParseException ex) {
			logger.error("convertStringToTimestamp", ex);
		}
		return result;
	}

	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}

	public static StatoMissione convertIntToStatoMissione(int i) {
		for (StatoMissione m : values()) {
			if (m.ordinal() == i) {
				return m;
			}
		}
		return null;
	}

	public static FreightItemState convertIntToFreightItemState(int i) {
		for (FreightItemState fs : FreightItemState.values()) {
			if (fs.ordinal() == i) {
				return fs;
			}
		}
		return null;
	}

	public static GeoLocation convertiGeoLocationInterfaceToGeoLocation(GeoLocationInterface add) {
		GeoLocation geoLoc = null;
		if (add != null) {
			geoLoc = new GeoLocation();
			geoLoc.setCity(add.getCity());
			geoLoc.setCountry(add.getCountry());
			geoLoc.setNumber(add.getNumber());
			geoLoc.setAdminAreaLevel1(add.getAdminAreaLevel1());
			geoLoc.setAdminAreaLevel2(add.getAdminAreaLevel2());
			geoLoc.setStreet(add.getStreet());
			geoLoc.setZipCode(add.getZipCode());
			geoLoc.setLatitude(add.getLatitude());
			geoLoc.setLongitude(add.getLongitude());
			geoLoc.setRadius(0);
		}
		return geoLoc;
	}

	public static Parametro convertiParameterGenToParametro(ParameterGen parameterGen) {
		Parametro parametro = null;
		if (parameterGen != null) {
			parametro = new Parametro();
			parametro.setDescrizione(parameterGen.getDescription());
			parametro.setIdGen(parameterGen.getId());
			parametro.setNome(parameterGen.getNamePG());
			parametro.setAttivo(parameterGen.isUseType());
			parametro.setUnitaMisura(parameterGen.getMeasureUnit());
			parametro.setTipo(parameterGen.getTypePG().name());
		}
		return parametro;
	}

	public static ParameterGen convertiParametroToParameterGen(Parametro parametro) {
		ParameterGen parameterGen = null;
		if (parametro != null) {
			parameterGen = new ParameterGen();
			parameterGen.setDescription(parametro.getDescrizione());
			parameterGen.setId(parametro.getIdGen());
			parameterGen.setTypePG(TipologiaParametro.valueOf(parametro.getTipo()));
			parameterGen.setMeasureUnit(parametro.getUnitaMisura());
			parameterGen.setNamePG(parametro.getNome());
			parameterGen.setUseType(parametro.isAttivo());
		}
		return parameterGen;
	}

	public static List<Parametro> convertiParameterGensToParametri(List<ParameterGen> parameterGens) {
		List<Parametro> parametri = null;
		if (parameterGens != null) {
			parametri = new ArrayList<Parametro>();
			for (ParameterGen parameterGen : parameterGens)
				parametri.add(convertiParameterGenToParametro(parameterGen));
		}
		return parametri;
	}

	public static List<ParameterGen> convertiParametriToParameterGens(List<Parametro> parametri) {
		List<ParameterGen> parameterGens = null;
		if (parametri != null) {
			parameterGens = new ArrayList<ParameterGen>();
			for (Parametro parametro : parametri)
				parameterGens.add(convertiParametroToParameterGen(parametro));
		}
		return parameterGens;
	}

	public static Prezzo convertiPriceToPrezzo(Price price, List<ParameterTS> parameterTSs,
			List<ParameterGen> parameterGens) {
		Prezzo prezzo = null;
		if (price != null) {
			prezzo = new Prezzo(convertiTimeSlotToFasciaOraria(price.getTs(), parameterTSs, parameterGens, null),
					price.getColor(), price.getMaxPrice(), price.getMinPrice(), price.getFixPrice(),
					price.getTypeEntry().name());
		}
		return prezzo;
	}

	public static Prezzo convertiPriceToPrezzo(Price price) {
		Prezzo prezzo = null;
		if (price != null) {
			prezzo = new Prezzo(convertiTimeSlotToFasciaOraria(price.getTs()), price.getColor(), price.getMaxPrice(),
					price.getMinPrice(), price.getFixPrice(), price.getTypeEntry().name());
		}
		return prezzo;
	}

	public static Price convertiPrezzoToPrice(Prezzo prezzo) {
		Price price = null;
		if (prezzo != null) {
			price = new Price();
			price.setColor(prezzo.getColor());
			price.setFixPrice(prezzo.getFixPrice());
			price.setTs(convertiFasciaOrariaToTimeSlot(prezzo.getFasciaOraria()));
			price.setMaxPrice(prezzo.getMaxPrice());
			price.setMinPrice(prezzo.getMinPrice());
			price.setTypeEntry(AccessoVeicoli.valueOf(prezzo.getTypeEntry()));
		}
		return price;
	}

	public static List<Prezzo> convertiPricesToPrezzi(List<Price> prices, List<ParameterTS> parameterTSs,
			List<ParameterGen> parameterGens) {
		List<Prezzo> prezzi = null;
		if (prices != null) {
			prezzi = new ArrayList<Prezzo>();
			for (Price price : prices)
				prezzi.add(convertiPriceToPrezzo(price, parameterTSs, parameterGens));
		}
		return prezzi;
	}

	public static List<Prezzo> convertiPricesToPrezzi(List<Price> prices) {
		List<Prezzo> prezzi = null;
		if (prices != null) {
			prezzi = new ArrayList<Prezzo>();
			for (Price price : prices)
				prezzi.add(convertiPriceToPrezzo(price));
		}
		return prezzi;
	}

	public static List<Price> convertiPrezziToPrices(List<Prezzo> prezzi) {
		List<Price> prices = null;
		if (prezzi != null) {
			prices = new ArrayList<Price>();
			for (Prezzo prezzo : prezzi)
				prices.add(convertiPrezzoToPrice(prezzo));
		}
		return prices;
	}

	public static ParameterTS convertiParametroToParameterTS(Parametro parametro, FasciaOraria fasciaOraria) {
		ParameterTS parameterTS = null;
		if (parametro != null) {
			parameterTS = new ParameterTS(parametro.getId());
			parameterTS.setWeight(Peso.valueOf(parametro.getPeso()));
			parameterTS.setMaxValue(parametro.getValoreMassimo());
			parameterTS.setMinValue(parametro.getValoreMinimo());
			parameterTS.setTs(convertiFasciaOrariaToTimeSlot(fasciaOraria));
			parameterTS.setTypePar(DA_DECIDERE);
			parameterTS.setParGen(convertiParametroToParameterGen(parametro));
		}
		return parameterTS;
	}

	public static List<ParameterTS> convertiParametriToParameterTSs(List<Parametro> parametri,
			FasciaOraria fasciaOraria) {
		List<ParameterTS> parameterTss = null;
		if (parametri != null) {
			parameterTss = new ArrayList<ParameterTS>();
			for (Parametro parametro : parametri)
				parameterTss.add(convertiParametroToParameterTS(parametro, fasciaOraria));
		}
		return parameterTss;
	}

	public static Filter convertiFiltroToFilter(Filtro filtro) {
		Filter filter = new Filter(filtro.getRoundCode(), filtro.getOperatoreLogistico());
		return filter;
	}

	public static Filtro convertiFilterToFiltro(Filter filter) {
		Filtro filtro = new Filtro(filter.getId().getRoundCode(), filter.getId().getOperatoreLogistico());
		return filtro;
	}

	public static List<Filter> convertiFiltriToFilters(List<Filtro> filtri) {
		List<Filter> filters = null;
		if (filtri != null) {
			filters = new ArrayList<Filter>();
			for (Filtro filtro : filtri)
				filters.add(convertiFiltroToFilter(filtro));
		}
		return filters;
	}

	public static List<Filtro> convertiFiltersToFiltri(List<Filter> filters) {
		List<Filtro> filtri = null;
		if (filters != null) {
			filtri = new ArrayList<Filtro>();
			for (Filter filter : filters)
				filtri.add(convertiFilterToFiltro(filter));
		}
		return filtri;
	}

	public static Freight convertiShippingItemToFreight(ShippingItem shippingItem) {
		Freight freight = new Freight(shippingItem.getId());
		freight.setDescription(shippingItem.getDescription());
		Map<String, String> attributi = shippingItem.getAttributes();
		freight.setHeight(new Double(attributi.get("Height")));
		freight.setLeng(new Double(attributi.get("Length")));
		freight.setWidth(new Double(attributi.get("Width")));
		freight.setWeight(new Double(attributi.get("Weight")));
		freight.setVolume(new Double(attributi.get("Volume")));
		freight.setFt(attributi.get("Type").equals("B") ? DOCUMENTI : ALTRO_TIPO);
		return freight;
	}

	public static ShippingItem convertiFreightToShippingItem(Freight freight) {
		ShippingItem shippingItem = new ShippingItem(freight.getCodeId());
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("Height", freight.getHeight() + "");
		attributes.put("Length", freight.getLeng() + "");
		attributes.put("Width", freight.getWidth() + "");
		attributes.put("Volume", freight.getVolume() + "");
		attributes.put("Weight", freight.getWeight() + "");
		attributes.put("Type", freight.getFt() == DOCUMENTI ? "B" : "C");
		shippingItem.setAttributes(attributes);
		shippingItem.setDescription(freight.getDescription());
		return shippingItem;
	}

	public static List<Freight> convertiShippingItemsToFreights(List<ShippingItem> shippingItems) {
		List<Freight> freights = null;
		if (shippingItems != null) {
			freights = new ArrayList<Freight>();
			for (ShippingItem shippingItem : shippingItems)
				freights.add(convertiShippingItemToFreight(shippingItem));
		}
		return freights;
	}

	public static Pacco convertiShippingItemToPacco(ShippingItem shippingItem) {
		Pacco pacco = new Pacco(shippingItem.getId(), shippingItem.getDescription(), shippingItem.getAttributes());
		pacco.setAttributi(shippingItem.getAttributes());
		return pacco;
	}

	public static List<Pacco> convertiShippingItemsToPacchi(List<ShippingItem> shippingItems) {
		List<Pacco> pacchi = null;
		if (shippingItems != null) {
			pacchi = new ArrayList<Pacco>();
			for (ShippingItem shippingItem : shippingItems)
				pacchi.add(convertiShippingItemToPacco(shippingItem));
		}
		return pacchi;
	}

	public static List<ShippingItem> convertiFreightsToShippingItems(List<Freight> freights) {
		List<ShippingItem> shippingItems = null;
		if (freights != null) {
			shippingItems = new ArrayList<ShippingItem>();
			for (Freight freight : freights)
				shippingItems.add(convertiFreightToShippingItem(freight));
		}
		return shippingItems;
	}
}
