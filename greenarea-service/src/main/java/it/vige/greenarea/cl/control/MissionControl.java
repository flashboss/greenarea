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
package it.vige.greenarea.cl.control;

import static it.vige.greenarea.Conversioni.convertiMissionsToMissioni;
import static it.vige.greenarea.Conversioni.convertiTransportsToRichieste;
import static it.vige.greenarea.Utilities.calcoloBonus;
import static it.vige.greenarea.Utilities.calcoloKmPerMissione;
import static it.vige.greenarea.Utilities.calcoloTotaleEmissioni;
import static it.vige.greenarea.Utilities.calcoloTotaleEmissioniPerCarburante;
import static it.vige.greenarea.Utilities.calcoloTotaleEmissioniPerPeso;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.completed;
import static it.vige.greenarea.dto.Selezione.TUTTI;
import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static it.vige.greenarea.dto.TipoRichiesta.RITIRO;
import static java.util.Arrays.asList;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static javax.persistence.TemporalType.DATE;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;

import it.vige.greenarea.NoDuplicatesList;
import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.cl.library.entities.VikorResult;
import it.vige.greenarea.cl.sessions.VikorResultFacade;
import it.vige.greenarea.dto.AccessiInGA;
import it.vige.greenarea.dto.DettaglioMissione;
import it.vige.greenarea.dto.Fuel;
import it.vige.greenarea.dto.ImpattoAmbientale;
import it.vige.greenarea.dto.Indirizzo;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.PerformanceVeicoli;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.RichiestaAccesso;
import it.vige.greenarea.dto.RichiestaMissioni;
import it.vige.greenarea.dto.ValoriVeicolo;
import it.vige.greenarea.dto.Veicolo;
import it.vige.greenarea.gtg.db.facades.ExchangeStopFacade;
import it.vige.greenarea.gtg.db.facades.MissionFacade;

/**
 * <p>
 * Class: TimeSlotControl
 * </p>
 * <p>
 * Description: Questa classe ?? il core di cityLogistics, Gestisce tutte le
 * chiamate e risposte
 * </p>
 * 
 * @author City Logistics Team
 * @version 0.0
 */
@Stateless
public class MissionControl {

	private Logger logger = getLogger(getClass());

	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;
	@EJB
	private MissionFacade mc;
	@EJB
	private ExchangeStopFacade exsf;
	@EJB
	private VikorResultFacade vrf;
	@EJB
	private TAPControl tapControl;

	/**
	 * <p>
	 * Method: getMissioni
	 * </p>
	 * <p>
	 * Description: Dato un idTimeSlot restituisce i parametri configurati per
	 * quella fascia oraria
	 * </p>
	 * 
	 * @param int
	 *            idTimeSlot
	 * @return List<ParameterTS>
	 */
	public List<Missione> getMissioni(RichiestaMissioni richiesta) {
		List<ExchangeStop> allExchangeStops = new ArrayList<ExchangeStop>();
		List<Mission> missionList = getMissions(richiesta);
		for (Mission mission : missionList) {
			allExchangeStops.addAll(exsf.findAll(mission));
		}
		logger.debug("query done");
		List<Missione> missioni = convertiMissionsToMissioni(missionList);
		aggiungiIDStop(missioni, allExchangeStops);
		double totaleEmissioniPerPeso = 0.0;
		Map<Date, AccessiInGA> accessiInGA = null;
		if (missioni.size() > 0) {
			String compagnia = missioni.get(0).getCompagnia();
			RichiestaAccesso richiestaAccesso = new RichiestaAccesso();
			richiestaAccesso.setDataInizio(richiesta.getDataInizio());
			richiestaAccesso.setDataFine(richiesta.getDataFine());
			richiestaAccesso.setOperatoriLogistici(asList(new String[] { compagnia }));
			accessiInGA = tapControl.getStoricoAccessiInGA(richiestaAccesso);
			totaleEmissioniPerPeso = calcoloTotaleEmissioniPerPeso(compagnia, missioni, accessiInGA);
			List<VikorResult> vikorResults = vrf.findAll();
			if (vikorResults != null)
				for (VikorResult vikorResult : vikorResults) {
					for (Missione missione : missioni)
						if (missione.getNome().equals(vikorResult.getIdMission() + "")) {
							missione.setRanking(vikorResult.getColor());
							missione.setCreditoMobilita(vikorResult.getPrice());
							double totaleKm = 0;
							for (Date date : accessiInGA.keySet()) {
								Calendar calendar1 = new GregorianCalendar();
								calendar1.setTime(date);
								Calendar calendar2 = new GregorianCalendar();
								calendar2.setTime(missione.getDataInizio());
								if (calendar1.get(DAY_OF_MONTH) == calendar2.get(DAY_OF_MONTH)
										&& calendar1.get(YEAR) == calendar2.get(YEAR)
										&& calendar1.get(MONTH) == calendar2.get(MONTH)) {
									AccessiInGA accessoInGA = accessiInGA.get(date);
									totaleKm += accessoInGA.getKm();
									logger.debug("somma totaleKm dist = " + totaleKm);
								}
							}
							double emissioniPerMissione = totaleKm * missione.getVeicolo().getValori().getEmission();
							missione.setBonus(calcoloBonus(totaleEmissioniPerPeso, emissioniPerMissione,
									missione.getCreditoMobilita()));
						}
				}
		}
		return missioni;
	}

	private List<Mission> getMissions(RichiestaMissioni richiesta) {
		boolean where = false;
		String qu = "SELECT c FROM Mission c";
		if (richiesta.getId() != 0) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.id = :id";
			} else
				qu = qu + " and c.id = :id";
		}
		if (richiesta.getFasciaOraria() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.timeSlot.idTS = :fasciaOraria";
			} else
				qu = qu + " and c.timeSlot.idTS = :fasciaOraria";
		}
		if (richiesta.getDataInizio() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.startTime >= :dayStart";
			} else
				qu = qu + " and c.startTime >= :dayStart";
		}
		if (richiesta.getDataFine() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.startTime <= :dayFinish";
			} else
				qu = qu + " and c.startTime <= :dayFinish";
		}
		if (richiesta.getVeicoli() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.truck.plateNumber in :plateNumbers";
			} else
				qu = qu + " and c.truck.plateNumber in :plateNumbers";
		}
		if (richiesta.getOperatoriLogistici() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.company in :operatoriLogistici";
			} else
				qu = qu + " and c.company in :operatoriLogistici";
		}
		if (richiesta.getSocietaDiTrasporto() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.truck.societaDiTrasporto in :societaDiTrasporto";
			} else
				qu = qu + " and c.truck.societaDiTrasporto in :societaDiTrasporto";
		}
		if (richiesta.getAutisti() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.truck.autista in :autisti";
			} else
				qu = qu + " and c.truck.autista in :autisti";
		}
		Query query = em.createQuery(qu);
		if (richiesta.getId() != 0)
			query.setParameter("id", richiesta.getId());
		if (richiesta.getFasciaOraria() != null)
			query.setParameter("fasciaOraria", richiesta.getFasciaOraria().getId());
		if (richiesta.getDataInizio() != null)
			query.setParameter("dayStart", richiesta.getDataInizio(), DATE);
		if (richiesta.getDataFine() != null)
			query.setParameter("dayFinish", richiesta.getDataFine(), DATE);
		if (richiesta.getVeicoli() != null)
			query.setParameter("plateNumbers", richiesta.getVeicoli());
		if (richiesta.getOperatoriLogistici() != null)
			query.setParameter("operatoriLogistici", richiesta.getOperatoriLogistici());
		if (richiesta.getSocietaDiTrasporto() != null)
			query.setParameter("societaDiTrasporto", richiesta.getSocietaDiTrasporto());
		if (richiesta.getAutisti() != null)
			query.setParameter("autisti", richiesta.getAutisti());
		@SuppressWarnings("unchecked")
		List<Mission> missionList = query.getResultList();
		return missionList;
	}

	/**
	 * <p>
	 * Method: getMissioni
	 * </p>
	 * <p>
	 * Description: Dato un idTimeSlot restituisce i parametri configurati per
	 * quella fascia oraria
	 * </p>
	 * 
	 * @param int
	 *            idTimeSlot
	 * @return List<ParameterTS>
	 */
	public List<ImpattoAmbientale> getImpattoAmbientale(RichiestaMissioni richiesta) {
		List<Missione> missioni = getMissioni(richiesta);
		List<Missione> missioniAutorizzate = new ArrayList<Missione>();
		for (Missione missione : missioni)
			if (missione.getRanking() != null)
				missioniAutorizzate.add(missione);
		logger.debug("query done");
		List<ImpattoAmbientale> impattiAmbientali = new ArrayList<ImpattoAmbientale>();
		Map<String, ImpattoAmbientale> mappaDettaglio = new HashMap<String, ImpattoAmbientale>();
		double totaleKmPercorsiInGa = 0.0;
		RichiestaAccesso richiestaAccesso = new RichiestaAccesso();
		richiestaAccesso.setDataInizio(richiesta.getDataInizio());
		richiestaAccesso.setDataFine(richiesta.getDataFine());
		if (richiesta.getOperatoriLogistici() == null)
			richiestaAccesso.setOperatoriLogistici(asList(new String[] { TUTTI.name() }));
		else
			richiestaAccesso.setOperatoriLogistici(richiesta.getOperatoriLogistici());
		Map<String, Map<Date, AccessiInGA>> accessiInGA = tapControl.getStoricoAccessiInGAPerVeicolo(richiestaAccesso);
		Map<String, Double> mappaPerGA = new LinkedHashMap<String, Double>();
		for (String vin : accessiInGA.keySet()) {
			Map<Date, AccessiInGA> accessoInGA = accessiInGA.get(vin);
			double totalePerVin = 0.0;
			for (AccessiInGA accesso : accessoInGA.values()) {
				totaleKmPercorsiInGa += accesso.getKm();
				totalePerVin += accesso.getKm();
			}
			mappaPerGA.put(vin, totalePerVin);
		}
		double totaleEmissioni = 0.0;
		List<String> elencoVin = new NoDuplicatesList<String>();
		for (Missione missione : missioniAutorizzate) {
			Veicolo veicolo = missione.getVeicolo();
			ValoriVeicolo valoriVeicolo = veicolo.getValori();
			String carburante = valoriVeicolo.getFuel();
			ImpattoAmbientale dettaglioMissione = mappaDettaglio.get(carburante);
			if (dettaglioMissione == null) {
				totaleEmissioni = calcoloTotaleEmissioniPerCarburante(carburante, missioniAutorizzate, mappaPerGA);
				dettaglioMissione = new ImpattoAmbientale(carburante);
				mappaDettaglio.put(carburante, dettaglioMissione);
				impattiAmbientali.add(dettaglioMissione);
			}
			Double kmInGA = mappaPerGA.get(veicolo.getVin());
			if (kmInGA != null && !elencoVin.contains(veicolo.getVin())) {
				dettaglioMissione.setNumeroKmPercorsiInGA(dettaglioMissione.getNumeroKmPercorsiInGA() + kmInGA);
				double km = dettaglioMissione.getNumeroKmPercorsiInGA();
				if (km == 0.0)
					dettaglioMissione.setPercentualeKmPercorsiInGA(km);
				else
					dettaglioMissione.setPercentualeKmPercorsiInGA(
							dettaglioMissione.getNumeroKmPercorsiInGA() / totaleKmPercorsiInGa * 100);
				elencoVin.add(veicolo.getVin());
			}
			dettaglioMissione.setNumeroMissioni(dettaglioMissione.getNumeroMissioni() + 1);
			dettaglioMissione
					.setPercentualeMissioni(dettaglioMissione.getNumeroMissioni() / missioniAutorizzate.size() * 100);
			dettaglioMissione
					.setNumeroEmissioni(dettaglioMissione.getNumeroKmPercorsiInGA() * valoriVeicolo.getEmission());
			Double percentualeEmissioni = dettaglioMissione.getNumeroEmissioni() / totaleEmissioni * 100;
			if (percentualeEmissioni.isNaN())
				percentualeEmissioni = 0.0;
			dettaglioMissione.setPercentualeEmissioni(percentualeEmissioni);
			dettaglioMissione.setDal(richiesta.getDataInizio());
			dettaglioMissione.setAl(richiesta.getDataFine());

		}
		return impattiAmbientali;
	}

	/**
	 * <p>
	 * Method: getPerformanceVeicoli
	 * </p>
	 * <p>
	 * Description: Dato un idTimeSlot restituisce i parametri configurati per
	 * quella fascia oraria
	 * </p>
	 * 
	 * @param int
	 *            idTimeSlot
	 * @return List<ParameterTS>
	 */
	public List<PerformanceVeicoli> getPerformanceVeicoli(RichiestaMissioni richiesta) {
		List<Missione> missioni = getMissioni(richiesta);
		List<Missione> missioniAutorizzate = new ArrayList<Missione>();
		for (Missione missione : missioni)
			if (missione.getRanking() != null)
				missioniAutorizzate.add(missione);
		logger.debug("query done");
		List<PerformanceVeicoli> performanceVeicoli = new ArrayList<PerformanceVeicoli>();
		Map<String, PerformanceVeicoli> mappaDettaglio = new HashMap<String, PerformanceVeicoli>();
		RichiestaAccesso richiestaAccesso = new RichiestaAccesso();
		richiestaAccesso.setDataInizio(richiesta.getDataInizio());
		richiestaAccesso.setDataFine(richiesta.getDataFine());
		if (richiesta.getOperatoriLogistici() == null)
			richiestaAccesso.setOperatoriLogistici(asList(new String[] { TUTTI.name() }));
		else
			richiestaAccesso.setOperatoriLogistici(richiesta.getOperatoriLogistici());
		Map<String, Map<Date, AccessiInGA>> accessiInGA = tapControl.getStoricoAccessiInGAPerVeicolo(richiestaAccesso);
		Map<String, Double> mappaPerGA = new LinkedHashMap<String, Double>();
		for (String vin : accessiInGA.keySet()) {
			Map<Date, AccessiInGA> accessoInGA = accessiInGA.get(vin);
			double totalePerVin = 0.0;
			for (AccessiInGA accesso : accessoInGA.values()) {
				totalePerVin += accesso.getKm();
			}
			mappaPerGA.put(vin, totalePerVin);
		}
		for (Missione missione : missioniAutorizzate) {
			Veicolo veicolo = missione.getVeicolo();
			ValoriVeicolo valoriVeicolo = veicolo.getValori();
			String targa = veicolo.getTarga();
			PerformanceVeicoli dettaglioMissione = mappaDettaglio.get(targa);
			if (dettaglioMissione == null) {
				dettaglioMissione = new PerformanceVeicoli(targa);
				mappaDettaglio.put(targa, dettaglioMissione);
				performanceVeicoli.add(dettaglioMissione);
				Double km = mappaPerGA.get(veicolo.getVin());
				if (km == null)
					km = 0.0;
				dettaglioMissione.setNumeroKmPercorsiInGA(km);
				dettaglioMissione.setConsumoTotale(km * valoriVeicolo.getEmission());
			}
			dettaglioMissione.setTipoAlimentazione(Fuel.valueOf(valoriVeicolo.getFuel()));
			dettaglioMissione.setNumeroMissioni(dettaglioMissione.getNumeroMissioni() + 1);
			String classeEcologica = valoriVeicolo.getEuro();
			if (classeEcologica != null)
				dettaglioMissione.setClasseEcologica(new Integer(classeEcologica));
			List<Richiesta> richieste = missione.getRichieste();
			dettaglioMissione.setDal(richiesta.getDataInizio());
			dettaglioMissione.setAl(richiesta.getDataFine());
			double numeroTotaleConsegne = 0.0;
			for (Richiesta consegna : richieste)
				if (consegna.getTipo().equals(CONSEGNA.name()))
					numeroTotaleConsegne++;
			dettaglioMissione.setNumeroMedioConsegneAMissione((int) (dettaglioMissione.getNumeroMedioConsegneAMissione()
					+ numeroTotaleConsegne / dettaglioMissione.getNumeroMissioni()));
			double numeroTotaleRitiri = 0.0;
			for (Richiesta ritiro : richieste)
				if (ritiro.getTipo().equals(RITIRO.name()))
					numeroTotaleRitiri++;
			dettaglioMissione.setNumeroMedioDiRitiriAMissione((int) (dettaglioMissione.getNumeroMedioDiRitiriAMissione()
					+ numeroTotaleRitiri / dettaglioMissione.getNumeroMissioni()));
			double numeroConsegneABuonFine = 0.0;
			for (Richiesta consegna : richieste)
				if (consegna.getTipo().equals(CONSEGNA.name()) && consegna.getStato().equals(completed.name()))
					numeroConsegneABuonFine++;
			if (numeroTotaleConsegne > 0)
				dettaglioMissione
						.setPercentualeConsegneABuonFine((double) dettaglioMissione.getPercentualeConsegneABuonFine()
								+ numeroConsegneABuonFine / (double) numeroTotaleConsegne);
		}
		return performanceVeicoli;
	}

	/**
	 * <p>
	 * Method: getMissioni
	 * </p>
	 * <p>
	 * Description: Dato un idTimeSlot restituisce i parametri configurati per
	 * quella fascia oraria
	 * </p>
	 * 
	 * @param int
	 *            idTimeSlot
	 * @return List<ParameterTS>
	 */
	public SortedSet<DettaglioMissione> getDettaglioMissioni(RichiestaMissioni richiesta) {
		Map<String, List<ExchangeStop>> allExchangeStops = new LinkedHashMap<String, List<ExchangeStop>>();
		List<Mission> missionList = getMissions(richiesta);
		for (Mission mission : missionList) {
			String compagnia = mission.getTruck().getOperatoreLogistico();
			List<ExchangeStop> exchangeStops = allExchangeStops.get(compagnia);
			if (exchangeStops == null) {
				exchangeStops = new NoDuplicatesList<ExchangeStop>(exsf.findAll(mission));
			} else
				exchangeStops.addAll(exsf.findAll(mission));
			allExchangeStops.put(compagnia, exchangeStops);
		}
		logger.debug("query done");
		List<Missione> missioni = convertiMissionsToMissioni(missionList);
		for (List<ExchangeStop> exchangeStops : allExchangeStops.values())
			aggiungiIDStop(missioni, exchangeStops);
		List<VikorResult> vikorResults = vrf.findAll();
		if (vikorResults != null)
			for (VikorResult vikorResult : vikorResults) {
				for (Missione missione : missioni)
					if (missione.getNome().equals(vikorResult.getIdMission() + "")) {
						missione.setRanking(vikorResult.getColor());
						missione.setCreditoMobilita(vikorResult.getPrice());
						missione.setBonus(0.0);
					}
			}
		SortedSet<DettaglioMissione> dettaglioMissioni = new TreeSet<DettaglioMissione>();
		Map<String, DettaglioMissione> mappaDettaglio = new HashMap<String, DettaglioMissione>();
		Map<Date, AccessiInGA> accessiInGA = null;
		double totaleEmissioni = 0.0;
		double totaleEmissioniPerPeso = 0.0;
		for (Missione missione : missioni) {
			String compagnia = missione.getCompagnia();
			DettaglioMissione dettaglioMissione = mappaDettaglio.get(compagnia);
			if (dettaglioMissione == null) {
				dettaglioMissione = new DettaglioMissione(compagnia);
				mappaDettaglio.put(compagnia, dettaglioMissione);
				dettaglioMissioni.add(dettaglioMissione);
				RichiestaAccesso richiestaAccesso = new RichiestaAccesso();
				richiestaAccesso.setDataInizio(richiesta.getDataInizio());
				richiestaAccesso.setDataFine(richiesta.getDataFine());
				richiestaAccesso.setOperatoriLogistici(asList(new String[] { compagnia }));
				accessiInGA = tapControl.getStoricoAccessiInGA(richiestaAccesso);
				int totaleAccessi = 0;
				totaleEmissioniPerPeso = calcoloTotaleEmissioniPerPeso(compagnia, missioni, accessiInGA);
				totaleEmissioni = calcoloTotaleEmissioni(compagnia, missioni, accessiInGA);
				double totaleKm = 0;
				long tempoTrascorso = 0;
				for (AccessiInGA accessoInGA : accessiInGA.values()) {
					totaleAccessi += accessoInGA.getAccessi();
					totaleKm += accessoInGA.getKm();
					logger.debug("somma totaleKm dist = " + totaleKm);
					tempoTrascorso += accessoInGA.getTempoTrascorso();
				}
				dettaglioMissione.setAccessiInGATotale(totaleAccessi);
				dettaglioMissione.setAccessiInGAMedia((double) totaleAccessi / (double) missioni.size());
				dettaglioMissione.setKmPercorsiInGA(totaleKm);
				dettaglioMissione.setEmissioniTotali(totaleEmissioni);
				dettaglioMissione.setTempoTrascorsoInGA(tempoTrascorso / 1000 * 60);
				int allExchangeStopSize = 0;
				List<ExchangeStop> exchangeStops = allExchangeStops.get(compagnia);
				for (ExchangeStop exchangeStop : exchangeStops)
					allExchangeStopSize += exchangeStop.getDeliveryList().size();
				dettaglioMissione.setNumeroConsegneperStop(allExchangeStopSize);
				dettaglioMissione.setNumeroStop(exchangeStops.size());
			}
			dettaglioMissione
					.setCreditidiMobilita(dettaglioMissione.getCreditidiMobilita() + missione.getCreditoMobilita());
			double bonus = calcoloBonus(totaleEmissioniPerPeso,
					calcoloKmPerMissione(missione, accessiInGA) * missione.getVeicolo().getValori().getEmission(),
					missione.getCreditoMobilita());
			dettaglioMissione.setBonus(dettaglioMissione.getBonus() + bonus);
			dettaglioMissione.setMissioni(dettaglioMissione.getMissioni() + 1);
			dettaglioMissione.setDal(richiesta.getDataInizio());
			dettaglioMissione.setAl(richiesta.getDataFine());
		}
		DettaglioMissione totale = new DettaglioMissione("missioni_pa_sintesi_table_fields_totale");
		for (DettaglioMissione dettaglioMissione : mappaDettaglio.values()) {
			totale.setAccessiInGAMedia(totale.getAccessiInGAMedia() + dettaglioMissione.getAccessiInGAMedia());
			totale.setAccessiInGATotale(totale.getAccessiInGATotale() + dettaglioMissione.getAccessiInGATotale());
			totale.setBonus(totale.getBonus() + dettaglioMissione.getBonus());
			totale.setCreditidiMobilita(totale.getCreditidiMobilita() + dettaglioMissione.getCreditidiMobilita());
			totale.setEmissioniTotali(totale.getEmissioniTotali() + dettaglioMissione.getEmissioniTotali());
			totale.setKmPercorsiInGA(totale.getKmPercorsiInGA() + dettaglioMissione.getKmPercorsiInGA());
			totale.setMissioni(totale.getMissioni() + dettaglioMissione.getMissioni());
			totale.setNumeroConsegneperStop(
					totale.getNumeroConsegneperStop() + dettaglioMissione.getNumeroConsegneperStop());
			totale.setNumeroStop(totale.getNumeroStop() + dettaglioMissione.getNumeroStop());
			totale.setTempoTrascorsoInGA(totale.getTempoTrascorsoInGA() + dettaglioMissione.getTempoTrascorsoInGA());
		}
		dettaglioMissioni.add(totale);
		return dettaglioMissioni;
	}

	/**
	 * <p>
	 * Method: getConsegne
	 * </p>
	 * <p>
	 * Description: Dato un idTimeSlot restituisce i parametri configurati per
	 * quella fascia oraria
	 * </p>
	 * 
	 * @param int
	 *            idTimeSlot
	 * @return List<ParameterTS>
	 */
	public List<Richiesta> getConsegne(RichiestaMissioni richiesta) {
		boolean where = false;
		String qu = "SELECT c FROM Transport c";
		if (richiesta.getDataInizio() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.timeAccept >= :timeAccept";
			} else
				qu = qu + " and c.timeAccept >= :timeAccept";
		}
		if (richiesta.getDataFine() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.timeClosing >= :timeClosing";
			} else
				qu = qu + " and c.timeClosing >= :timeClosing";
		}
		if (richiesta.getVeicoli() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.mission.truck.plateNumber in :plateNumbers";
			} else
				qu = qu + " and c.mission.truck.plateNumber in :plateNumbers";
		}
		if (richiesta.getGas() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.timeSlot.roundCode in :gas";
			} else
				qu = qu + " and c.timeSlot.roundCode = :gas";
		}
		Query query = em.createQuery(qu);
		if (richiesta.getDataInizio() != null)
			query.setParameter("timeAccept", richiesta.getDataInizio());
		if (richiesta.getDataFine() != null)
			query.setParameter("timeClosing", richiesta.getDataFine());
		if (richiesta.getVeicoli() != null)
			query.setParameter("plateNumbers", richiesta.getVeicoli());
		if (richiesta.getGas() != null)
			query.setParameter("gas", richiesta.getGas());
		@SuppressWarnings("unchecked")
		List<Transport> transportList = query.getResultList();
		List<Richiesta> richieste = convertiTransportsToRichieste(transportList);
		return richieste;
	}

	private void aggiungiIDStop(List<Missione> missioni, List<ExchangeStop> exchangeStops) {
		for (Missione missione : missioni) {
			List<Richiesta> richieste = missione.getRichieste();
			for (Richiesta richiesta : richieste) {
				for (ExchangeStop exchangeStop : exchangeStops) {
					Indirizzo indirizzoRichiesta = null;
					if (richiesta.getTipo().equals(CONSEGNA))
						indirizzoRichiesta = richiesta.getToAddress();
					else
						indirizzoRichiesta = richiesta.getFromAddress();
					if (exchangeStop.getLocation().getLatitude() == indirizzoRichiesta.getLatitude()
							&& exchangeStop.getLocation().getLongitude() == indirizzoRichiesta.getLongitude())
						richiesta.setIdStop(exchangeStop.getId());
				}
			}
		}

	}
}
