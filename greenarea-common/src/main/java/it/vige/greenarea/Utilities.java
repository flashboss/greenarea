package it.vige.greenarea;

import static de.micromata.opengis.kml.v_2_2_0.Kml.unmarshal;
import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.convertiPricesToPrezzi;
import static java.lang.Character.isLetter;
import static java.lang.Character.isWhitespace;
import static java.lang.Character.toLowerCase;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.dto.AccessiInGA;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.Pacco;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Prezzo;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.ValoriVeicolo;
import it.vige.greenarea.dto.Veicolo;
import it.vige.greenarea.geofencing.Poligono;
import it.vige.greenarea.geofencing.Punto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.slf4j.Logger;

import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;

public class Utilities {

	private static Logger logger = getLogger(Utilities.class);

	private static DateFormat formatter = new SimpleDateFormat(
			"ddMMyyyyhhmmssSSS");

	private static Poligono poligono;

	public static String createMockShippingId() {
		return formatter.format(new Date());
	}

	public static String uppercaseFirstLetters(String str) {
		boolean prevWasWhiteSp = true;
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (isLetter(chars[i])) {
				if (!prevWasWhiteSp) {
					chars[i] = toLowerCase(chars[i]);
				}
				prevWasWhiteSp = false;
			} else {
				prevWasWhiteSp = isWhitespace(chars[i]);
			}
		}
		return new String(chars);
	}

	public static void setDettaglio(TimeSlot timeSlot, Client client,
			FasciaOraria fasciaOraria) throws Exception {
		if (timeSlot != null) {
			int idTimeslot = timeSlot.getIdTS();
			logger.debug(idTimeslot + "");

			List<Parametro> listaTimeSlot_VisualizzaView = new ArrayList<Parametro>();
			Builder bldr = client.target(
					BASE_URI_TS + "/findParameterOfTimeSlot/" + idTimeslot)
					.request(APPLICATION_JSON);
			List<ParameterTS> listaTimeSlot_Visualizza = bldr
					.get(new GenericType<List<ParameterTS>>() {
					});
			bldr = client.target(BASE_URI_TS + "/findAllParameterGenAvailable")
					.request(APPLICATION_JSON);
			List<ParameterGen> listaParGen = bldr
					.get(new GenericType<List<ParameterGen>>() {
					});
			for (int i = 0; i < listaTimeSlot_Visualizza.size(); i++) {
				for (int y = 0; y < listaParGen.size(); y++) {
					if (listaParGen.get(y).getId() == listaTimeSlot_Visualizza
							.get(i).getParGen().getId()) {
						Parametro parameterTSView = new Parametro(
								listaParGen.get(y),
								listaTimeSlot_Visualizza.get(i), timeSlot);
						listaTimeSlot_VisualizzaView.add(parameterTSView);
					}
				}

			}
			bldr = client.target(
					BASE_URI_TS + "/getPriceOfTimeSlot/" + idTimeslot).request(
					APPLICATION_JSON);
			List<Price> prices = bldr.get(new GenericType<List<Price>>() {
			});

			List<Prezzo> prezzi = convertiPricesToPrezzi(prices);
			fasciaOraria.setParametri(listaTimeSlot_VisualizzaView);
			fasciaOraria.setPrezzi(prezzi);
		}
	}

	public static Map<Richiesta, FasciaOraria> associaFasciaOrariaARichiesta(
			List<Richiesta> richieste, List<FasciaOraria> fasceOrarie,
			Veicolo veicolo) {
		ValoriVeicolo valoriVeicolo = veicolo.getValori();
		Map<Richiesta, FasciaOraria> richiestePerFasciaOraria = new HashMap<Richiesta, FasciaOraria>();
		double totalWeight = 0.0;
		for (Richiesta richiesta : richieste) {
			double weight = 0.0;
			Pacco[] pacchi = richiesta.getPacchi();
			for (Pacco pacco : pacchi) {
				weight += new Double(pacco.getAttributi().get("Weight"));
			}
			totalWeight += weight;
			if (valoriVeicolo.getWeight() >= weight)
				for (FasciaOraria fasciaOraria : fasceOrarie) {
					if (richiesta.getDataMissione().compareTo(
							fasciaOraria.getDataInizio()) > 0
							&& richiesta.getDataMissione().compareTo(
									fasciaOraria.getDataFine()) < 0) {
						List<Parametro> parametri = fasciaOraria.getParametri();
						for (Parametro parametro : parametri)
							if (isApproved(veicolo, parametro)
									&& totalWeight <= valoriVeicolo.getWeight())
								richiestePerFasciaOraria.put(richiesta,
										fasciaOraria);
					}
				}
		}

		if (richiestePerFasciaOraria == null
				|| richiestePerFasciaOraria.isEmpty())
			logger.error("non sono state trovate fasce orarie da associare!!!!! ");
		return richiestePerFasciaOraria;
	}

	public static boolean isApproved(Veicolo veicolo, Parametro parametro) {
		boolean result = true;
		ValoriVeicolo valoriVeicolo = veicolo.getValori();
		String nomeParametro = parametro.getNome();
		switch (nomeParametro) {
		case ("emission"):
			if (parametro.getValoreMassimo() < valoriVeicolo.getEmission()
					|| parametro.getValoreMinimo() > valoriVeicolo
							.getEmission())
				result = false;
			break;
		case ("weight"):
			if (parametro.getValoreMassimo() < valoriVeicolo.getWeight()
					|| parametro.getValoreMinimo() > valoriVeicolo.getWeight())
				result = false;
			break;
		case ("euro"):
			if (parametro.getValoreMassimo() < new Integer(
					valoriVeicolo.getEuro())
					|| parametro.getValoreMinimo() > new Integer(
							valoriVeicolo.getEuro()))
				result = false;
			break;
		case ("lenght"):
			if (parametro.getValoreMassimo() < valoriVeicolo.getLenght()
					|| parametro.getValoreMinimo() > valoriVeicolo.getLenght())
				result = false;
			break;
		}
		if (!result)
			logger.debug("il veicolo " + valoriVeicolo.getBaseName()
					+ "non ?? adatto alla richiesta: " + nomeParametro + " - "
					+ parametro.getValoreMinimo() + " - "
					+ parametro.getValoreMassimo());
		return result;
	}

	public static void aggiungiValoriAMissione(Missione missioneEntry,
			Map<Richiesta, FasciaOraria> richiestePerFasciaOraria) {
		if (richiestePerFasciaOraria.size() > 0) {
			FasciaOraria fasciaOraria = richiestePerFasciaOraria.values()
					.iterator().next();
			missioneEntry.setFasciaOraria(fasciaOraria);
			List<Richiesta> richieste = missioneEntry.getRichieste();
			missioneEntry.setTappe(missioneEntry.getVeicolo().getValori()
					.getTappe()
					+ "");
			missioneEntry.setEuro(missioneEntry.getVeicolo().getValori()
					.getEuro());
			for (Richiesta richiesta : richieste) {
				Pacco[] pacchi = richiesta.getPacchi();
				for (Pacco pacco : pacchi) {
					String carico = missioneEntry.getCarico();
					missioneEntry
							.setCarico((carico != null ? new Double(carico)
									: 0.0)
									+ new Double(pacco.getAttributi().get(
											"Volume")) + "");
					missioneEntry.setLunghezza(missioneEntry.getVeicolo()
							.getValori().getLenght()
							+ "");
					String peso = missioneEntry.getPeso();
					missioneEntry.setPeso((peso != null ? new Double(peso)
							: 0.0)
							+ new Double(pacco.getAttributi().get("Weight"))
							+ "");
				}
			}
		}
	}

	public static List<Date> prelevaDateMissioni(
			List<Richiesta> richiesteDaMissione) {
		List<Date> allDates = new NoDuplicatesList<Date>();
		for (Richiesta richiesta : richiesteDaMissione)
			allDates.add(richiesta.getDataMissione());
		return allDates;
	}

	public static List<String> prelevaRoundCodesMissioni(
			List<Richiesta> richiesteDaMissione) {
		List<String> allCodes = new NoDuplicatesList<String>();
		for (Richiesta richiesta : richiesteDaMissione)
			allCodes.add(richiesta.getRoundCode());
		return allCodes;
	}

	public static double calcoloTotaleEmissioniPerCarburante(String carburante,
			List<Missione> missioni, Map<String, Double> mappaPerGA) {
		double totaleEmissioni = 0.0;
		for (String vin : mappaPerGA.keySet()) {
			Double numeroKm = mappaPerGA.get(vin);
			if (numeroKm == null)
				numeroKm = 0.0;
			double emission = 0.0;
			for (Missione missione : missioni) {
				Veicolo veicolo = missione.getVeicolo();
				String vinM = veicolo.getVin();
				if (vinM.equals(vin)
						&& carburante.equals(veicolo.getValori().getFuel())) {
					emission = veicolo.getValori().getEmission();
					break;
				}
			}
			totaleEmissioni += numeroKm * emission;
		}
		return totaleEmissioni;
	}

	public static double calcoloTotaleEmissioni(String compagnia,
			List<Missione> missioni, Map<Date, AccessiInGA> mappaPerGA) {
		double totaleEmissioni = 0.0;
		Double totnumeroKm = 0.0;
		Double othertotnumeroKm = 0.0;
		for (Missione missione : missioni) {
			if (missione.getCompagnia().equals(compagnia))
				for (Date date : mappaPerGA.keySet()) {
					Calendar calendar1 = new GregorianCalendar();
					calendar1.setTime(date);
					Calendar calendar2 = new GregorianCalendar();
					calendar2.setTime(missione.getDataInizio());
					othertotnumeroKm += mappaPerGA.get(date).getKm();
					if (calendar1.get(DAY_OF_MONTH) == calendar2
							.get(DAY_OF_MONTH)
							&& calendar1.get(YEAR) == calendar2.get(YEAR)
							&& calendar1.get(MONTH) == calendar2.get(MONTH)) {
						Double numeroKm = mappaPerGA.get(date).getKm();
						if (numeroKm == null)
							numeroKm = 0.0;
						totaleEmissioni += numeroKm
								* missione.getVeicolo().getValori()
										.getEmission();
						totnumeroKm += numeroKm;
					}
				}
		}
		return totaleEmissioni;
	}

	public static double calcoloKmPerMissione(Missione missione,
			Map<Date, AccessiInGA> accessiInGA) {
		Double numeroKm = 0.0;
		for (Date date : accessiInGA.keySet()) {
			Calendar calendar1 = new GregorianCalendar();
			calendar1.setTime(date);
			Calendar calendar2 = new GregorianCalendar();
			calendar2.setTime(missione.getDataInizio());
			if (calendar1.get(DAY_OF_MONTH) == calendar2.get(DAY_OF_MONTH)
					&& calendar1.get(YEAR) == calendar2.get(YEAR)
					&& calendar1.get(MONTH) == calendar2.get(MONTH)) {
				AccessiInGA accessi = accessiInGA.get(date);
				numeroKm = accessi.getKm();
				if (numeroKm == null)
					numeroKm = 0.0;
				numeroKm += numeroKm
						* missione.getVeicolo().getValori().getEmission();
			}
		}
		return numeroKm;
	}

	public static double calcoloTotaleEmissioniPerPeso(String compagnia,
			List<Missione> missioni, Map<Date, AccessiInGA> accessiInGA) {
		double totaleEmissioni = 0.0;
		double totalePesi = 0.0;
		for (Missione missione : missioni) {
			if (missione.getCompagnia().equals(compagnia)) {
				for (Richiesta richiesta : missione.getRichieste()) {
					for (Pacco pacco : richiesta.getPacchi())
						totalePesi += totalePesi
								+ new Double(pacco.getAttributi().get("Weight"));
				}
				for (Date date : accessiInGA.keySet()) {
					Calendar calendar1 = new GregorianCalendar();
					calendar1.setTime(date);
					Calendar calendar2 = new GregorianCalendar();
					calendar2.setTime(missione.getDataInizio());
					if (calendar1.get(DAY_OF_MONTH) == calendar2
							.get(DAY_OF_MONTH)
							&& calendar1.get(YEAR) == calendar2.get(YEAR)
							&& calendar1.get(MONTH) == calendar2.get(MONTH)) {
						AccessiInGA accessi = accessiInGA.get(date);
						Double numeroKm = accessi.getKm();
						if (numeroKm == null)
							numeroKm = 0.0;
						totaleEmissioni += numeroKm
								* missione.getVeicolo().getValori()
										.getEmission();
					}
				}
			}
		}
		double mediaEmissioniPerPeso = totaleEmissioni / totalePesi;
		return mediaEmissioniPerPeso;
	}

	public static double calcoloBonus(double totaleEmissioni,
			double emissioniPerMissione, double creditoMobilita) {
		double bonus = 0.0;
		if (emissioniPerMissione < totaleEmissioni / 100 * 10)
			bonus = creditoMobilita / 100 * 10;
		return bonus;
	}

	public static Poligono getPoligonoPerGA() {
		if (poligono == null) {
			Kml unmarshal = unmarshal(Utilities.class.getClassLoader()
					.getResourceAsStream("ztl-torino.kml"));
			Document doc = (Document) unmarshal.getFeature();
			Folder folder = (Folder) doc.getFeature().get(0);
			Folder folder2 = (Folder) folder.getFeature().get(0);
			Placemark placemark = (Placemark) folder2.getFeature().get(0);
			Polygon polygon = (Polygon) placemark.getGeometry();
			Boundary boundary = polygon.getOuterBoundaryIs();
			LinearRing linearRing = boundary.getLinearRing();
			List<Coordinate> coordinates = linearRing.getCoordinates();
			List<Punto> coords = new ArrayList<Punto>();
			for (Coordinate coordinate : coordinates) {
				coords.add(new Punto(new Double(coordinate.getLatitude()),
						new Double(coordinate.getLongitude())));
			}
			poligono = new Poligono(coords.toArray(new Punto[0]));
		}
		return poligono;
	}
}
