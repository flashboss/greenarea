package it.vige.greenarea.test.rest;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static it.vige.greenarea.dto.TipiRichiesta.CONSEGNE;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.dto.DettaglioMissione;
import it.vige.greenarea.dto.ImpattoAmbientale;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.PerformanceVeicoli;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.RichiestaMissioni;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.junit.Test;
import org.slf4j.Logger;

public class TestRichieste {

	private Logger logger = getLogger(getClass());

	@Test
	public void testGetRichieste() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_RICHIESTE + "/getRichieste")
				.request(APPLICATION_JSON);
		Richiesta richiesta = new Richiesta();
		List<Richiesta> response = bldr.post(
				entity(richiesta, APPLICATION_JSON),
				new GenericType<List<Richiesta>>() {
				});
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testGetRichiesteConTipo() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_RICHIESTE + "/getRichieste")
				.request(APPLICATION_JSON);
		Richiesta richiesta = new Richiesta();
		richiesta.setTipo(CONSEGNE.name());
		List<Richiesta> response = bldr.post(
				entity(richiesta, APPLICATION_JSON),
				new GenericType<List<Richiesta>>() {
				});
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testGetRichiesteConTuttiICampi() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_RICHIESTE + "/getRichieste")
				.request(APPLICATION_JSON);
		Richiesta richiesta = new Richiesta();
		richiesta.setTipo(CONSEGNE.name());
		richiesta.setOrarioInizio(new Date());
		richiesta.setRoundCode("01");
		List<Richiesta> response = bldr.post(
				entity(richiesta, APPLICATION_JSON),
				new GenericType<List<Richiesta>>() {
				});
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testMissioni() {

		Client client = newClient();
		Builder bldr = client
				.target(BASE_URI_RICHIESTE + "/getSintesiMissioni").request(
						APPLICATION_JSON);
		RichiestaMissioni richiesta = new RichiestaMissioni();
		List<Missione> response = bldr.post(
				entity(richiesta, APPLICATION_JSON),
				new GenericType<List<Missione>>() {
				});
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testDettaglioMissioni() {

		Client client = newClient();
		Builder bldr = client.target(
				BASE_URI_RICHIESTE + "/getDettaglioMissioni").request(
				APPLICATION_JSON);
		RichiestaMissioni richiesta = new RichiestaMissioni();
		SortedSet<DettaglioMissione> response = bldr.post(
				entity(richiesta, APPLICATION_JSON),
				new GenericType<SortedSet<DettaglioMissione>>() {
				});
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testImpattoAmbientale() {

		Client client = newClient();
		Builder bldr = client.target(
				BASE_URI_RICHIESTE + "/getImpattoAmbientale").request(
				APPLICATION_JSON);
		RichiestaMissioni richiesta = new RichiestaMissioni();
		List<ImpattoAmbientale> response = bldr.post(
				entity(richiesta, APPLICATION_JSON),
				new GenericType<List<ImpattoAmbientale>>() {
				});
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testPerformanceVeicoli() {

		Client client = newClient();
		Builder bldr = client.target(
				BASE_URI_RICHIESTE + "/getPerformanceVeicoli").request(
				APPLICATION_JSON);
		RichiestaMissioni richiesta = new RichiestaMissioni();
		List<PerformanceVeicoli> response = bldr.post(
				entity(richiesta, APPLICATION_JSON),
				new GenericType<List<PerformanceVeicoli>>() {
				});
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testMissioniConVeicoli() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_RICHIESTE + "/getMissioni")
				.request(APPLICATION_JSON);
		RichiestaMissioni richiesta = new RichiestaMissioni();
		richiesta
				.setVeicoli(asList(new String[] { "111SK", "556MK", "OP666" }));
		List<Missione> response = bldr.post(
				entity(richiesta, APPLICATION_JSON),
				new GenericType<List<Missione>>() {
				});
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testConsegne() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_RICHIESTE + "/getConsegne")
				.request(APPLICATION_JSON);
		RichiestaMissioni richiesta = new RichiestaMissioni();
		List<Richiesta> response = bldr.post(
				entity(richiesta, APPLICATION_JSON),
				new GenericType<List<Richiesta>>() {
				});
		assertNotNull(response);

		logger.info(response + "");

	}

	@Test
	public void testConsegneConVeicoli() {

		Client client = newClient();
		Builder bldr = client.target(BASE_URI_RICHIESTE + "/getConsegne")
				.request(APPLICATION_JSON);
		RichiestaMissioni richiesta = new RichiestaMissioni();
		richiesta
				.setVeicoli(asList(new String[] { "222DK", "557MK", "91GTK" }));
		List<Richiesta> response = bldr.post(
				entity(richiesta, APPLICATION_JSON),
				new GenericType<List<Richiesta>>() {
				});
		assertNotNull(response);

		logger.info(response + "");

	}

}
