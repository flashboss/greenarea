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
package it.vige.greenarea.test.rest;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.convertiTimeSlotToFasciaOraria;
import static it.vige.greenarea.Utilities.enFormatter;
import static it.vige.greenarea.Utilities.yyyyMMddNH;
import static it.vige.greenarea.dto.AccessoVeicoli.NEGATO;
import static it.vige.greenarea.dto.AperturaRichieste._2_GIORNI_PRIMA;
import static it.vige.greenarea.dto.ChiusuraRichieste._1_GIORNO_PRIMA;
import static it.vige.greenarea.dto.Color.GIALLO;
import static it.vige.greenarea.dto.Color.ROSSO;
import static it.vige.greenarea.dto.Color.VERDE;
import static it.vige.greenarea.dto.Fuel.BENZINA;
import static it.vige.greenarea.dto.Peso.BASSO;
import static it.vige.greenarea.dto.Ripetizione.FESTIVI;
import static it.vige.greenarea.dto.StatoMissione.WAITING;
import static it.vige.greenarea.dto.StatoVeicolo.IDLE;
import static it.vige.greenarea.dto.TipoParametro.DA_DECIDERE;
import static it.vige.greenarea.dto.TipologiaClassifica.CLASSIFICA_STANDARD;
import static it.vige.greenarea.dto.TipologiaParametro.CONTATORE;
import static it.vige.greenarea.dto.Tolleranza._50_PER_CENTO;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import it.vige.greenarea.Conversioni;
import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.bean.TimeSlotInfo;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.cl.library.entities.TransportServiceClass;
import it.vige.greenarea.cl.library.entities.TsStat;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.Sched;
import it.vige.greenarea.dto.ValoriVeicolo;
import it.vige.greenarea.dto.Veicolo;

public class TestTimeSlot {

	private Logger logger = getLogger(getClass());

	private TimeSlot timeSlot;
	private ParameterGen parameterGen;
	private String dateMission;

	@Before
	public void init() {
		addTimeSlot();
		addParameterGen();
		addPrices();
		configParameterTsToTimeSlot();
		buildCityLogisticsMission();
		buildMission();
		addShipping();
	}

	@After
	public void close() {
		deleteShipping();
		deleteMissions();
		removeParameterTsToTimeSlot();
		deletePrices();
		deleteParameterGen();
		deleteTimeSlot();
	}

	private void addTimeSlot() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/addTimeSlot").request(APPLICATION_JSON);
		TimeSlot timeSlot = new TimeSlot();
		timeSlot.setDayFinish("11-10-2000");
		timeSlot.setDayStart("2-10-2000");
		timeSlot.setFinishTS("10:23");
		timeSlot.setStartTS("11:21");
		timeSlot.setTimeToAcceptRequest(_2_GIORNI_PRIMA);
		timeSlot.setTimeToRun(_1_GIORNO_PRIMA);
		timeSlot.setTimeToStopRequest(_1_GIORNO_PRIMA);
		timeSlot.setTollerance(_50_PER_CENTO);
		timeSlot.setVikInd(CLASSIFICA_STANDARD);
		timeSlot.setWmy(FESTIVI);
		timeSlot.setPa("papomezia");
		TimeSlot response = bldr.post(entity(timeSlot, APPLICATION_JSON), TimeSlot.class);
		assertNotNull(response);

		logger.info(response + "");
		this.timeSlot = response;
	}

	private void deleteTimeSlot() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/deleteTimeSlot").request(APPLICATION_JSON);
		TimeSlot response = bldr.post(entity(new TimeSlot(timeSlot.getIdTS()), APPLICATION_JSON), TimeSlot.class);
		assertNotNull(response);

		logger.info(response + "");

	}

	private void addParameterGen() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/addParameterGen").request(APPLICATION_JSON);
		ParameterGen parameterGen = new ParameterGen();
		parameterGen.setDescription("mia descrizione");
		parameterGen.setTypePG(CONTATORE);
		parameterGen.setMeasureUnit("euro");
		parameterGen.setNamePG("mio p g");
		parameterGen.setUseType(true);
		ParameterGen response = bldr.post(entity(parameterGen, APPLICATION_JSON), ParameterGen.class);
		assertNotNull(response);

		logger.info(response + "");
		this.parameterGen = response;
	}

	private void deleteParameterGen() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/deleteParameterGen").request(APPLICATION_JSON);
		ParameterGen response = bldr.post(entity(new ParameterGen(parameterGen.getId()), APPLICATION_JSON),
				ParameterGen.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	private void addPrices() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/addPrice").request(APPLICATION_JSON);
		Price price = new Price();
		price.setColor(GIALLO);
		price.setFixPrice(333);
		price.setMaxPrice(565555);
		price.setMinPrice(222);
		price.setTypeEntry(NEGATO);
		price.setTs(timeSlot);
		Price response = bldr.post(entity(price, APPLICATION_JSON), Price.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	private void deletePrices() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/deletePrice").request(APPLICATION_JSON);
		Price price = new Price();
		price.setColor(GIALLO);
		price.setFixPrice(333);
		price.setMaxPrice(565555);
		price.setMinPrice(222);
		price.setTypeEntry(NEGATO);
		price.setTs(timeSlot);
		Price response = bldr.post(entity(price, APPLICATION_JSON), Price.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	private void configParameterTsToTimeSlot() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/configParameterTS").request(APPLICATION_JSON);
		ParameterTS parameterTS = new ParameterTS();
		parameterTS.setParGen(parameterGen);
		parameterTS.setTs(timeSlot);
		parameterTS.setTypePar(DA_DECIDERE);
		parameterTS.setWeight(BASSO);
		parameterTS.setMaxValue(432234234);
		parameterTS.setMinValue(221);
		ParameterTS response = bldr.post(entity(parameterTS, APPLICATION_JSON), ParameterTS.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	private void removeParameterTsToTimeSlot() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/removeParametersTS").request(APPLICATION_JSON);
		TimeSlot response = bldr.post(entity(timeSlot, APPLICATION_JSON), TimeSlot.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	private void buildCityLogisticsMission() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/buildCityLogisticsMission").request(APPLICATION_JSON);
		Missione missione = new Missione("prova", WAITING);
		FasciaOraria fasciaOraria = Conversioni.convertiTimeSlotToFasciaOraria(timeSlot);
		fasciaOraria.setParametri(new ArrayList<Parametro>());
		missione.setFasciaOraria(fasciaOraria);
		Veicolo veicolo = new Veicolo(IDLE.name(), "44GU4");
		missione.setVeicolo(veicolo);
		Mission response = bldr.post(entity(missione, APPLICATION_JSON), Mission.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	private void buildMission() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/buildMission").request(APPLICATION_JSON);
		Missione missione = new Missione("prova", WAITING);
		FasciaOraria fasciaOraria = convertiTimeSlotToFasciaOraria(timeSlot);
		List<Parametro> parametri = new ArrayList<Parametro>();
		parametri.add(new Parametro(parameterGen.getId(), "lunghezza"));
		fasciaOraria.setParametri(parametri);
		missione.setFasciaOraria(fasciaOraria);
		Veicolo veicolo = new Veicolo(IDLE.name(), "44GU4");
		veicolo.setAutista(new GreenareaUser("user"));
		veicolo.setOperatoreLogistico(new OperatoreLogistico(new GreenareaUser("dhl")));
		veicolo.setSocietaDiTrasporto(new GreenareaUser("trambus"));
		ValoriVeicolo valori = new ValoriVeicolo();
		valori.setFuel(BENZINA.name());
		veicolo.setValori(valori);
		missione.setVeicolo(veicolo);
		missione.setRichieste(new ArrayList<Richiesta>());
		missione.setLunghezza("15.9");
		Mission response = bldr.post(entity(missione, APPLICATION_JSON), Mission.class);
		assertNotNull(response);

		logger.info(response + "");
		dateMission = enFormatter.format(response.getStartTime());
		timeSlot = response.getTimeSlot();
	}

	private void addShipping() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/addShipping").request(APPLICATION_JSON);
		ShippingOrder shippingOrder = new ShippingOrder("prova");
		ShippingOrder response = bldr.post(entity(shippingOrder, APPLICATION_JSON), ShippingOrder.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	private void deleteMissions() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/deleteMissions").request(APPLICATION_JSON);
		List<Mission> response = bldr.get(new GenericType<List<Mission>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	private void deleteShipping() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/deleteShipping").request(APPLICATION_JSON);
		ShippingOrder shippingOrder = new ShippingOrder("prova");
		ShippingOrder response = bldr.post(entity(shippingOrder, APPLICATION_JSON), ShippingOrder.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testUpdateTimeSlot() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/updateTimeSlot").request(APPLICATION_JSON);
		TimeSlot response = bldr.post(entity(timeSlot, APPLICATION_JSON), TimeSlot.class);
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testUpdateParameterGen() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/updateParameterGen").request(APPLICATION_JSON);
		parameterGen.setTypePG(CONTATORE);
		ParameterGen response = bldr.post(entity(parameterGen, APPLICATION_JSON), ParameterGen.class);
		assertNotNull(response);

		logger.info(response + "");

		bldr = client.target(BASE_URI_TS + "/updateParameterGen").request(APPLICATION_JSON);
		parameterGen.setTypePG(CONTATORE);
		response = bldr.post(entity(parameterGen, APPLICATION_JSON), ParameterGen.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testUpdatePrices() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/updatePrice").request(APPLICATION_JSON);
		Price price = new Price();
		price.setColor(ROSSO);
		price.setFixPrice(333);
		price.setMaxPrice(565555);
		price.setMinPrice(222);
		price.setTypeEntry(NEGATO);
		Price response = bldr.post(entity(price, APPLICATION_JSON), Price.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindTS() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/findTimeSlot/65").request(APPLICATION_JSON);
		TimeSlot response = bldr.get(TimeSlot.class);
		assertNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindAllTimeSlots() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/findAllTimeSlot").request(APPLICATION_JSON);
		List<TimeSlot> response = bldr.get(new GenericType<List<TimeSlot>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindAllTimeSlotsOfUser() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/findAllTimeSlot/paguidonia").request(APPLICATION_JSON);
		List<TimeSlot> response = bldr.get(new GenericType<List<TimeSlot>>() {
		});
		assertNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindAllParameterGenAvailable() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/findAllParameterGenAvailable").request(APPLICATION_JSON);
		List<ParameterGen> response = bldr.get(new GenericType<List<ParameterGen>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindAllParameterGen() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/findAllParameterGen").request(APPLICATION_JSON);
		List<ParameterGen> response = bldr.get(new GenericType<List<ParameterGen>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindParameterOfTimeSlot() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/findParameterOfTimeSlot/6").request(APPLICATION_JSON);
		List<ParameterTS> response = bldr.get(new GenericType<List<ParameterTS>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testSelectRequests() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/requests/Wed Oct 16 00:00:00 CEST 2013/23/67")
				.request(APPLICATION_JSON);
		List<Request> response = bldr.get(new GenericType<List<Request>>() {
		});
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testSimulRank() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/simulRank/64/Wed Oct 16 00:00:00 CEST 2013")
				.request(APPLICATION_JSON);
		List<Request> response = bldr.get(new GenericType<List<Request>>() {
		});
		assertNull(response);

		logger.info(response + "");
	}

	@Test
	public void testGetParameterOfTimeSlot() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/getParameterForRank/5").request(APPLICATION_JSON);
		List<ParameterTS> response = bldr.get(new GenericType<List<ParameterTS>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testGetInfoTimeSlot() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/getInfoTimeSlot/888").request(APPLICATION_JSON);
		TimeSlotInfo response = bldr.get(TimeSlotInfo.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testSimulSch() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/startScheduler").request(APPLICATION_JSON);
		Transport trasport = new Transport();
		ShippingOrder shippingOrder = new ShippingOrder("323L");
		trasport.setShippingOrder(shippingOrder);
		trasport.setTimeSlot(timeSlot);
		String response = bldr.post(entity(trasport, APPLICATION_JSON), String.class);
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testGetAllSchedules() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/getAllSchedules").request(APPLICATION_JSON);
		List<Sched> response = bldr.get(new GenericType<List<Sched>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testGetAllSchedulesWithParam() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/getSchedules/67").request(APPLICATION_JSON);
		List<Transport> response = bldr.get(new GenericType<List<Transport>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testGetrRank() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/getRank/" + timeSlot.getIdTS() + "/" + dateMission)
				.request(APPLICATION_JSON);
		List<Request> response = bldr.get(new GenericType<List<Request>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testUpdateVikor() {

		Request request = new Request();
		request.setCarPlate("44GU4");
		request.setCompany("dhl");
		try {
			request.setDateMiss(yyyyMMddNH.parse(dateMission));
		} catch (ParseException e) {
			logger.error("parte test", e);
		}
		request.setIdMission(33);
		request.setIdTimeSlot(timeSlot.getIdTS());
		request.setPrice(5.6);
		request.setColor(VERDE);
		request.setQu(new double[] { 3.2, 7.8, 9.9 });

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/updateVikor").request(APPLICATION_JSON);
		List<Request> response = bldr.post(entity(asList(new Request[] { request }), APPLICATION_JSON),
				new GenericType<List<Request>>() {
				});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testGetPriceOfTimeSlot() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/getPriceOfTimeSlot/22").request(APPLICATION_JSON);
		List<Price> response = bldr.get(new GenericType<List<Price>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testGetAllTsStats() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/getAllTsStats").request(APPLICATION_JSON);
		List<TsStat> response = bldr.get(new GenericType<List<TsStat>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testGetStoryBoard() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/getStoryBoard/101/Wed Oct 16 00:00:00 CEST 2013")
				.request(APPLICATION_JSON);
		List<Request> response = bldr.get(new GenericType<List<Request>>() {
		});
		assertNull(response);

		logger.info(response + "");
	}

	@Test
	public void testSimul() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/simul/573/Wed Oct 16 00:00:00 CEST 2013")
				.request(APPLICATION_JSON);
		List<Request> response = bldr.get(new GenericType<List<Request>>() {
		});
		assertNull(response);

		logger.info(response + "");
	}

	@Test
	public void testSimulaMissione() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/simulaCostruzioneMissioni").request(APPLICATION_JSON);
		Missione missione = new Missione("prova", WAITING);
		FasciaOraria fasciaOraria = new FasciaOraria();
		fasciaOraria.setId(0);
		missione.setFasciaOraria(fasciaOraria);
		List<Missione> response = bldr.post(entity(missione, APPLICATION_JSON), new GenericType<List<Missione>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}

	@Test
	public void testFindTransportServiceClass() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/findTransportServiceClass/FURGONATO").request(APPLICATION_JSON);
		List<TransportServiceClass> response = bldr.get(new GenericType<List<TransportServiceClass>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}
}
