package it.vige.greenarea.bpm.tempo.costruiscimissioni;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.dto.Missione;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class AggiornamentoDellaMissione extends EmptyAggiornamentoDellaMissione {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Aggiornamento della Missione");
			Missione missione = (Missione) execution.getVariable("missione");
			Client client = newClient();
			Builder bldr = client
					.target(BASE_URI_TS + "/TimeSlot/buildMission").request(
							APPLICATION_JSON);
			Mission response = bldr.post(entity(missione, APPLICATION_JSON),
					Mission.class);
			if (response == null) {
				Messaggio messaggio = (Messaggio) execution
						.getVariable("messaggio");
				messaggio.setCategoria(ERRORELIEVE);
				messaggio.setTipo(ERRORESISTEMA);
			}
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
		}
	}
}
