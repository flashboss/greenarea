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
package it.vige.greenarea.gtg.test.ws;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.convertiTimeSlotToFasciaOraria;
import static it.vige.greenarea.Utilities.sdfDestination;
import static it.vige.greenarea.cl.library.entities.FreightItemState.AVAILABLE;
import static it.vige.greenarea.cl.library.entities.FreightType.DOCUMENTI;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.waiting;
import static it.vige.greenarea.dto.AccessoVeicoli.NEGATO;
import static it.vige.greenarea.dto.AperturaRichieste._2_GIORNI_PRIMA;
import static it.vige.greenarea.dto.ChiusuraRichieste._1_GIORNO_PRIMA;
import static it.vige.greenarea.dto.Color.GIALLO;
import static it.vige.greenarea.dto.Fuel.BENZINA;
import static it.vige.greenarea.dto.Peso.BASSO;
import static it.vige.greenarea.dto.Ripetizione.FESTIVI;
import static it.vige.greenarea.dto.StatoMissione.WAITING;
import static it.vige.greenarea.dto.StatoVeicolo.IDLE;
import static it.vige.greenarea.dto.TipoParametro.DA_DECIDERE;
import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static it.vige.greenarea.dto.TipologiaClassifica.CLASSIFICA_STANDARD;
import static it.vige.greenarea.dto.TipologiaParametro.CONTATORE;
import static it.vige.greenarea.dto.Tolleranza._50_PER_CENTO;
import static java.util.Collections.singletonList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static javax.xml.ws.handler.MessageContext.HTTP_REQUEST_HEADERS;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.primefaces.util.Base64.encodeToString;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.xml.ws.BindingProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.Indirizzo;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Pacco;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.ValoriVeicolo;
import it.vige.greenarea.dto.Veicolo;
import it.vige.greenarea.gtg.webservice.FreightAction;
import it.vige.greenarea.gtg.webservice.GTGexception_Exception;
import it.vige.greenarea.gtg.webservice.GTGservice;
import it.vige.greenarea.gtg.webservice.GTGservice_Service;
import it.vige.greenarea.gtg.webservice.LDAPException_Exception;
import it.vige.greenarea.gtg.webservice.Mission;

public class MissioniTest {

	private Logger logger = getLogger(getClass());
	private String today;
	private TimeSlot timeSlot;
	private ParameterGen parameterGen;
	private List<String> alfacodes;

	@Before
	public void init() {
		Date td = new Date();
		today = sdfDestination.format(td);
		addTimeSlot();
		addParameterGen();
		addPrices();
		configParameterTsToTimeSlot();
		alfacodes = addTransports();
		buildMission();
	}

	@After
	public void close() {
		deleteTransports();
		deleteMissions();
		removeParameterTsToTimeSlot();
		deletePrices();
		deleteParameterGen();
		deleteTimeSlot();
	}

	@Test
	public void testMissioni() {
		GTGservice_Service service = new GTGservice_Service();
		GTGservice gtgService = service.getGTGservicePort();

		BindingProvider provider = (BindingProvider) gtgService;
		Map<String, Object> req_ctx = provider.getRequestContext();
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String credenziale = "prova:prova";
		String credenzialeCriptata = encodeToString(credenziale.getBytes(), false);
		headers.put("Authorization", singletonList("_____" + credenzialeCriptata));
		req_ctx.put(HTTP_REQUEST_HEADERS, headers);
		/*
		 * req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
		 * "http://82.107.53.122/greenarea-service/GTGservice");
		 */
		/*
		 * switch a localhost
		 */

		req_ctx.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/greenarea-service/GTGservice");

		try {
			gtgService.userLogin();
			List<Mission> missioni = gtgService.getMissions(today);
			logger.info("missioni = " + missioni);
		} catch (LDAPException_Exception e) {
			logger.error("missioni console", e);
			fail();
		}
	}

	@Test
	public void testCambioStatoRichiesta() {
		GTGservice_Service service = new GTGservice_Service();
		GTGservice gtgService = service.getGTGservicePort();

		BindingProvider provider = (BindingProvider) gtgService;
		Map<String, Object> req_ctx = provider.getRequestContext();
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String credenziale = "prova:prova";
		String credenzialeCriptata = encodeToString(credenziale.getBytes(), false);
		headers.put("Authorization", singletonList("_____" + credenzialeCriptata));
		req_ctx.put(HTTP_REQUEST_HEADERS, headers);
		/*
		 * req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
		 * "http://82.107.53.122/greenarea-service/GTGservice");
		 */
		/*
		 * switch a localhost
		 */

		req_ctx.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/greenarea-service/GTGservice");

		try {
			gtgService.userLogin();
			List<Mission> missioni = gtgService.getMissions(today);
			logger.info("missioni = " + missioni);
			for (Mission mission : missioni)
				try {
					gtgService.changeMissionState(mission.getId(), 1, 2, "cosà", today);
				} catch (GTGexception_Exception e) {
					fail();
				}
		} catch (LDAPException_Exception e) {
			logger.error("missioni console", e);
			fail();
		}
	}

	@Test
	public void testNotifyFreightItemAction() {
		GTGservice_Service service = new GTGservice_Service();
		GTGservice gtgService = service.getGTGservicePort();

		BindingProvider provider = (BindingProvider) gtgService;
		Map<String, Object> req_ctx = provider.getRequestContext();
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String credenziale = "prova:prova";
		String credenzialeCriptata = encodeToString(credenziale.getBytes(), false);
		headers.put("Authorization", singletonList("_____" + credenzialeCriptata));
		req_ctx.put(HTTP_REQUEST_HEADERS, headers);
		/*
		 * req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
		 * "http://82.107.53.122/greenarea-service/GTGservice");
		 */
		/*
		 * switch a localhost
		 */

		req_ctx.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/greenarea-service/GTGservice");

		try {
			gtgService.userLogin();
			List<Mission> missioni = gtgService.getMissions(today);
			logger.info("missioni = " + missioni);
			List<FreightAction> freightItemsAction = new ArrayList<FreightAction>();
			FreightAction freightAction = new FreightAction();
			freightAction.setCause(2);
			freightAction.setDateTime(today);
			freightAction.setState(1);
			freightAction.setNote("prova");
			freightAction.setExchangeStopId(missioni.get(0).getExchangeStops().get(0).getId());
			freightAction.setCode(missioni.get(0).getFreights().get(0).getCode());
			freightItemsAction.add(freightAction);
			try {
				gtgService.notifyFreightItemAction(freightItemsAction);
			} catch (GTGexception_Exception e) {
				fail();
			}
		} catch (LDAPException_Exception e) {
			logger.error("missioni console", e);
			fail();
		}
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
		List<Richiesta> richieste = new ArrayList<Richiesta>();
		Indirizzo indirizzo = new Indirizzo();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("Type", DOCUMENTI.name());
		attributes.put("Volume", "44.5");
		attributes.put("Weight", "2.6");
		attributes.put("Width", "1.7");
		attributes.put("Length", "4.1");
		attributes.put("Height", "33.2");
		attributes.put("Stackable", "true");
		attributes.put("KeepUpStanding", "false");
		attributes.put("State", AVAILABLE.name());
		String alfaCode1 = alfacodes.get(0);
		Richiesta richiesta1 = new Richiesta(
				new Pacco[] { new Pacco(alfaCode1 + 1, "desc1", attributes),
						new Pacco(alfaCode1 + 2, "desc2", attributes) },
				new HashMap<String, String>(), "first request");
		richiesta1.setToAddress(indirizzo);
		richiesta1.setToName("destination1");
		richiesta1.setFromAddress(indirizzo);
		richiesta1.setFromName("source1");
		richiesta1.setTipo(CONSEGNA.name());
		richiesta1.setStato(waiting.name());
		richiesta1.setShipmentId(alfaCode1);
		String alfaCode2 = alfacodes.get(1);
		Richiesta richiesta2 = new Richiesta(new Pacco[] { new Pacco(alfaCode2 + 1, "desc3", attributes) },
				new HashMap<String, String>(), "second request");
		richiesta2.setToAddress(indirizzo);
		richiesta2.setToName("destination2");
		richiesta2.setFromAddress(indirizzo);
		richiesta2.setFromName("source2");
		richiesta2.setTipo(CONSEGNA.name());
		richiesta2.setStato(waiting.name());
		richiesta2.setShipmentId(alfaCode2);

		richieste.add(richiesta1);
		richieste.add(richiesta2);
		missione.setRichieste(richieste);
		missione.setLunghezza("15.9");
		it.vige.greenarea.cl.library.entities.Mission response = bldr.post(entity(missione, APPLICATION_JSON),
				it.vige.greenarea.cl.library.entities.Mission.class);
		assertNotNull(response);

		logger.info(response + "");
		timeSlot = response.getTimeSlot();
	}

	private List<String> addTransports() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/addTransports").request(APPLICATION_JSON);
		List<String> response = bldr.get(new GenericType<List<String>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
		return response;
	}

	private void deleteMissions() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/deleteMissions").request(APPLICATION_JSON);
		List<it.vige.greenarea.cl.library.entities.Mission> response = bldr
				.get(new GenericType<List<it.vige.greenarea.cl.library.entities.Mission>>() {
				});
		assertNotNull(response);

		logger.info(response + "");
	}

	private void deleteTransports() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_TS + "/deleteSchedulers").request(APPLICATION_JSON);
		List<Transport> response = bldr.get(new GenericType<List<Transport>>() {
		});
		assertNotNull(response);

		logger.info(response + "");
	}
}
