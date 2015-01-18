/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.control;

import static it.vige.greenarea.Conversioni.addDays;
import static it.vige.greenarea.Utilities.getPoligonoPerGA;
import static it.vige.greenarea.dto.Selezione.TUTTI;
import static it.vige.greenarea.geofencing.Distance.calcolateDistance;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.TapGroupData;
import it.vige.greenarea.cl.library.entities.TapOutData;
import it.vige.greenarea.cl.library.entities.TapParamData;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.dto.AccessiInGA;
import it.vige.greenarea.dto.RichiestaAccesso;
import it.vige.greenarea.dto.RichiestaPosizioneVeicolo;
import it.vige.greenarea.geofencing.GeoFencingAlgorithm;
import it.vige.greenarea.geofencing.Poligono;
import it.vige.greenarea.gtg.db.facades.MissionFacade;
import it.vige.greenarea.gtg.db.facades.TruckFacade;
import it.vige.greenarea.tap.facades.TapGroupDataFacade;
import it.vige.greenarea.tap.facades.TapOutDataFacade;
import it.vige.greenarea.tap.facades.TapParamDataFacade;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;

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
public class TAPControl {

	private Logger logger = getLogger(getClass());

	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;
	@EJB
	private TapOutDataFacade tapOutDataFacade;
	@EJB
	private TapGroupDataFacade tapGroupDataFacade;
	@EJB
	private TapParamDataFacade tapParamDataFacade;
	@EJB
	private TruckFacade truckFacade;
	@EJB
	private MissionFacade missionFacade;

	private Poligono poligono = getPoligonoPerGA();

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * <p>
	 * Method: getVeicoliInGA
	 * </p>
	 * <p>
	 * Description: Dato un idTimeSlot restituisce i parametri configurati per
	 * quella fascia oraria
	 * </p>
	 * 
	 * @param Poligono
	 *            poligono
	 * @return int
	 */
	public int getVeicoliInGA() {
		List<TapOutData> tapOutDatas = tapOutDataFacade.findAllAccess();
		Map<TapOutData, List<TapGroupData>> mapOutGroup = tapGroupDataFacade
				.findByOutData(tapOutDatas);
		Map<TapGroupData, List<TapParamData>> mapGroupParam = tapParamDataFacade
				.findAll(tapOutDatas);
		Map<String, List<TapOutData>> mappaDettaglio = new HashMap<String, List<TapOutData>>();
		for (TapOutData tapOutData : tapOutDatas) {
			String vin = tapOutData.getVin();
			List<TapOutData> dettaglioTap = mappaDettaglio.get(vin);
			if (dettaglioTap == null) {
				dettaglioTap = new ArrayList<TapOutData>();
				mappaDettaglio.put(vin, dettaglioTap);
			}
			mappaDettaglio.get(vin).add(tapOutData);
			logger.debug("vin prelevato: " + vin);
			logger.debug("id data prelevata: " + tapOutData.getId() + " - "
					+ tapOutData.getDate().getTime());
		}
		int veicoliInGA = 0;
		for (String vin : mappaDettaglio.keySet()) {
			List<TapOutData> tapOutDatass = mappaDettaglio.get(vin);
			if (tapOutDatass != null && tapOutDatass.size() > 0) {
				TapGroupData gpsData = findLastTapGroupData(tapOutDatass,
						mapOutGroup, mapGroupParam);
				if (isInGA(gpsData, mapGroupParam)) {
					veicoliInGA++;
					logger.debug("incremento il contatore: ");
				}
			}
		}
		return veicoliInGA;
	}

	private boolean isInGA(TapGroupData tapGroupData,
			Map<TapGroupData, List<TapParamData>> mapGroupParam) {
		String[] coordinate = prelevaCoordinate(tapGroupData, mapGroupParam)
				.split(",");
		GeoFencingAlgorithm geoFencingAlgorithm = new GeoFencingAlgorithm(
				new Double(coordinate[0]), new Double(coordinate[1]), poligono);
		return geoFencingAlgorithm.isInGA();
	}

	private Map<String, Map<Date, Set<TapGroupData>>> getAccessiPerVin(
			RichiestaAccesso richiestaAccesso,
			Map<TapGroupData, List<TapParamData>> mapGroupParam) {
		Date dataInizio = richiestaAccesso.getDataInizio();
		Date dataFine = null;
		if (richiestaAccesso.getDataFine() != null)
			dataFine = addDays(richiestaAccesso.getDataFine(), 1);
		String operatoreLogistico = richiestaAccesso.getOperatoriLogistici()
				.get(0);
		final Map<TapGroupData, List<TapParamData>> finalMapGroupParam = mapGroupParam;
		Set<TapGroupData> tapGroupDatas = new TreeSet<TapGroupData>(
				new Comparator<TapGroupData>() {

					@Override
					public int compare(TapGroupData o1, TapGroupData o2) {
						Date d1 = getDate(o1, finalMapGroupParam);
						Date d2 = getDate(o2, finalMapGroupParam);
						if (d1 == null || d2 == null)
							return 0;
						return d1.compareTo(d2);
					}
				});
		tapGroupDatas.addAll(mapGroupParam.keySet());
		Map<String, Vehicle> veicoli = new HashMap<String, Vehicle>();
		for (TapGroupData tapGroupData : tapGroupDatas) {
			if (!operatoreLogistico.equals(TUTTI.name())) {
				String vin = tapGroupData.getTapOutData().getVin();
				if (veicoli.get(vin) == null) {
					Vehicle vehicle = truckFacade.findByVin(vin);
					veicoli.put(vin, vehicle);
				}
			}
		}
		Map<String, Map<Date, Set<TapGroupData>>> mappaPerVin = new LinkedHashMap<String, Map<Date, Set<TapGroupData>>>();
		for (TapGroupData tapGroupData : tapGroupDatas) {
			String vin = tapGroupData.getTapOutData().getVin();
			if (mappaPerVin.get(vin) == null)
				mappaPerVin.put(vin,
						new LinkedHashMap<Date, Set<TapGroupData>>());
			for (TapGroupData tapGroupDataDate : tapGroupDatas) {
				String vinDate = tapGroupDataDate.getTapOutData().getVin();
				Date data = getDate(tapGroupDataDate, mapGroupParam);
				Vehicle vehicle = veicoli.get(vinDate);
				if (vehicle == null
						|| vehicle.getOperatoreLogistico() != null
						&& vehicle.getOperatoreLogistico().equals(
								operatoreLogistico)) {
					if (data != null
							&& (dataInizio == null || data.after(dataInizio))
							&& (dataFine == null || data.before(dataFine))) {
						Set<TapGroupData> dettaglioTap = mappaPerVin.get(
								vinDate).get(data);
						if (dettaglioTap == null) {
							dettaglioTap = new LinkedHashSet<TapGroupData>();
							mappaPerVin.get(vinDate).put(data, dettaglioTap);
						}
						mappaPerVin.get(vinDate).get(data)
								.add(tapGroupDataDate);
					}
				}
			}
		}
		return mappaPerVin;
	}

	/**
	 * <p>
	 * Method: getStoricoAccessiInGAPerVeicolo
	 * </p>
	 * <p>
	 * Description: Dato un idTimeSlot restituisce i parametri configurati per
	 * quella fascia oraria
	 * </p>
	 * 
	 * @param RichiestaAccesso
	 *            richiestaAccesso
	 * @return Map<Date, Integer>
	 */
	public Map<String, Map<Date, AccessiInGA>> getStoricoAccessiInGAPerVeicolo(
			RichiestaAccesso richiestaAccesso) {
		List<TapParamData> allGpsDataTapParams = tapParamDataFacade
				.findAll("GPS_DATA");
		Map<TapGroupData, List<TapParamData>> mapGroupParam = new HashMap<TapGroupData, List<TapParamData>>();
		for (TapParamData tapParamData : allGpsDataTapParams) {
			TapGroupData tapGroupData = tapParamData.getTapGroupData();
			if (mapGroupParam.get(tapGroupData) == null)
				mapGroupParam.put(tapGroupData, new ArrayList<TapParamData>());
			mapGroupParam.get(tapGroupData).add(tapParamData);
		}
		Map<String, Map<Date, Set<TapGroupData>>> mappaPerVin = getAccessiPerVin(
				richiestaAccesso, mapGroupParam);
		Map<String, Map<Date, AccessiInGA>> accessiInGAPerVin = new LinkedHashMap<String, Map<Date, AccessiInGA>>();

		for (String vinPerData : mappaPerVin.keySet()) {
			Map<Date, AccessiInGA> accessiInGA = new LinkedHashMap<Date, AccessiInGA>();
			Map<Date, Set<TapGroupData>> mappaPerDate = mappaPerVin
					.get(vinPerData);
			boolean accessoEseguito = false;
			Date tempo = null;
			String coordinate = null;
			for (Date data : mappaPerDate.keySet()) {
				AccessiInGA object = accessiInGA.get(data);
				if (object == null)
					object = new AccessiInGA();
				Set<TapGroupData> tapGroupDatass = mappaPerDate.get(data);
				if (tapGroupDatass != null && tapGroupDatass.size() > 0) {
					for (TapGroupData tapGroupData : tapGroupDatass) {
						boolean isInGA = isInGA(tapGroupData, mapGroupParam);
						if (!isInGA) {
							accessoEseguito = false;
						}
						if (isInGA) {
							if (!accessoEseguito) {
								accessoEseguito = true;
								int accessoInGA = object.getAccessi();
								object.setAccessi(accessoInGA + 1);
							}
							object.setKm(calcolateDistance(
									coordinate,
									prelevaCoordinate(tapGroupData,
											mapGroupParam)));
							if (tempo != null)
								object.setTempoTrascorso(data.getTime()
										- tempo.getTime());
							accessiInGA.put(data, object);
						}
						coordinate = prelevaCoordinate(tapGroupData,
								mapGroupParam);
						tempo = data;
					}
				}
			}
			accessiInGAPerVin.put(vinPerData, accessiInGA);
		}
		return accessiInGAPerVin;
	}

	/**
	 * <p>
	 * Method: getStoricoAccessiInGA
	 * </p>
	 * <p>
	 * Description: Dato un idTimeSlot restituisce i parametri configurati per
	 * quella fascia oraria
	 * </p>
	 * 
	 * @param RichiestaAccesso
	 *            richiestaAccesso
	 * @return Map<Date, Integer>
	 */
	public Map<Date, AccessiInGA> getStoricoAccessiInGA(
			RichiestaAccesso richiestaAccesso) {
		List<TapParamData> allGpsDataTapParams = tapParamDataFacade
				.findAll("GPS_DATA");
		Map<TapGroupData, List<TapParamData>> mapGroupParam = new HashMap<TapGroupData, List<TapParamData>>();
		for (TapParamData tapParamData : allGpsDataTapParams) {
			TapGroupData tapGroupData = tapParamData.getTapGroupData();
			if (mapGroupParam.get(tapGroupData) == null)
				mapGroupParam.put(tapGroupData, new ArrayList<TapParamData>());
			mapGroupParam.get(tapGroupData).add(tapParamData);
		}
		Map<String, Map<Date, Set<TapGroupData>>> mappaPerVin = getAccessiPerVin(
				richiestaAccesso, mapGroupParam);

		Map<Date, AccessiInGA> accessiInGA = new LinkedHashMap<Date, AccessiInGA>();
		for (String vinPerData : mappaPerVin.keySet()) {
			Map<Date, Set<TapGroupData>> mappaPerDate = mappaPerVin
					.get(vinPerData);
			boolean accessoEseguito = false;
			Date tempo = null;
			String coordinate = null;
			Set<Date> keys = new TreeSet<Date>(new Comparator<Date>() {

				@Override
				public int compare(Date o1, Date o2) {
					return o1.compareTo(o2);
				}
			});
			keys.addAll(mappaPerDate.keySet());
			for (Date data : keys) {
				AccessiInGA object = accessiInGA.get(data);
				if (object == null)
					object = new AccessiInGA();
				Set<TapGroupData> tapGroupDatass = mappaPerDate.get(data);
				if (tapGroupDatass != null && tapGroupDatass.size() > 0) {
					for (TapGroupData tapGroupData : tapGroupDatass) {
						boolean isInGA = isInGA(tapGroupData, mapGroupParam);
						if (!isInGA) {
							accessoEseguito = false;
						}
						if (isInGA) {
							if (!accessoEseguito) {
								accessoEseguito = true;
								int accessoInGA = object.getAccessi();
								object.setAccessi(accessoInGA + 1);
							}
							object.setKm(calcolateDistance(
									coordinate,
									prelevaCoordinate(tapGroupData,
											mapGroupParam)));
							if (tempo != null)
								object.setTempoTrascorso(data.getTime()
										- tempo.getTime());
							accessiInGA.put(data, object);
						}
						coordinate = prelevaCoordinate(tapGroupData,
								mapGroupParam);
						tempo = data;
					}
				}
			}
		}
		return accessiInGA;
	}

	private String prelevaCoordinate(TapGroupData tapGroupData,
			Map<TapGroupData, List<TapParamData>> mapGroupParam) {
		String latitude = null;
		String longitude = null;
		List<TapParamData> tapParamDatas = mapGroupParam.get(tapGroupData);
		for (TapParamData tapParamData : tapParamDatas) {
			if (tapParamData.getName().equals("LATITUDE")) {
				latitude = tapParamData.getValue();
				logger.debug("LATITUDE: " + latitude);
			}
			if (tapParamData.getName().equals("LONGITUDE")) {
				longitude = tapParamData.getValue();
				logger.debug("LONGITUDE: " + longitude);
			}
		}
		return latitude + "," + longitude;
	}

	private Date getDate(TapGroupData tapGroupData,
			Map<TapGroupData, List<TapParamData>> paramGroup) {
		Date data = null;
		for (TapParamData tapParamData : paramGroup.get(tapGroupData)) {
			if (tapParamData.getName().equals("TIMESTAMP"))
				try {
					data = dateFormat.parse(tapParamData.getValue());
				} catch (ParseException e) {
					logger.error("errore sul parsing", e);
				}
		}
		return data;
	}

	public String richiediPosizioneVeicolo(
			RichiestaPosizioneVeicolo richiestaPosizioneVeicolo) {
		String latitude = "0";
		String longitude = "0";
		String targa = richiestaPosizioneVeicolo.getTarga();
		String idMissione = richiestaPosizioneVeicolo.getIdMissione();
		Vehicle vehicle = null;
		if (targa != null && !targa.isEmpty())
			vehicle = truckFacade.find(targa);
		else if (idMissione != null && !idMissione.isEmpty()) {
			Mission mission = missionFacade.find(new Long(idMissione));
			if (mission != null)
				vehicle = truckFacade.find(mission.getTruck().getPlateNumber());
		} else
			logger.error("Parametri non trovati");
		if (vehicle != null) {
			String vin = vehicle.getVin();
			List<TapOutData> tapOutDatass = tapOutDataFacade.findAll(vin);

			Map<TapOutData, List<TapGroupData>> mapOutGroup = tapGroupDataFacade
					.findByOutData(tapOutDatass);
			Map<TapGroupData, List<TapParamData>> mapGroupParam = tapParamDataFacade
					.findAll(tapOutDatass);
			if (tapOutDatass != null && tapOutDatass.size() > 0) {
				TapGroupData last = findLastTapGroupData(tapOutDatass,
						mapOutGroup, mapGroupParam);
				List<TapParamData> tapParamDatas = tapParamDataFacade
						.findAll(last);
				logger.debug("ultimo id gruppo trovato: " + last.getId());
				for (TapParamData tapParamData : tapParamDatas) {
					if (tapParamData.getName().equals("LATITUDE")) {
						latitude = tapParamData.getValue();
						logger.debug("calcolo latitudine: " + latitude);
					}
					if (tapParamData.getName().equals("LONGITUDE")) {
						longitude = tapParamData.getValue();
						logger.debug("calcolo longitudine: " + longitude);
					}
				}
			}
		}
		logger.debug("posizione trovata: " + longitude + "," + latitude);
		return longitude + "," + latitude;
	}

	private TapGroupData findLastTapGroupData(List<TapOutData> tapOutDatass,
			Map<TapOutData, List<TapGroupData>> mapOutGroup,
			Map<TapGroupData, List<TapParamData>> mapGroupParam) {
		TapGroupData lastGpsData = null;
		TapGroupData currentGpsData = null;
		for (TapOutData tapOutData : tapOutDatass) {
			List<TapGroupData> tapGroupDatass = mapOutGroup.get(tapOutData);
			for (TapGroupData tapGroupData : tapGroupDatass) {
				if (tapGroupData.getName().equals("GPS_DATA")) {
					List<TapParamData> tapParamDatass = mapGroupParam
							.get(tapGroupData);
					for (TapParamData tapParamData : tapParamDatass) {
						if (tapParamData.getName().equals("TIMESTAMP"))
							for (TapParamData tapParamData2 : tapParamDatass) {
								if (tapParamData2.getName().equals("TIMESTAMP"))
									try {
										if (dateFormat
												.parse(tapParamData2.getValue())
												.compareTo(
														dateFormat
																.parse(tapParamData
																		.getValue())) > 0)
											lastGpsData = tapGroupData;
										currentGpsData = tapGroupData;
									} catch (ParseException e) {
										logger.error("errore parsing data", e);
									}
							}
					}
				}
			}
		}
		if (lastGpsData == null)
			return currentGpsData;
		else
			return lastGpsData;
	}
}
