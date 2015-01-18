package it.vige.greenarea.bpm.societaditrasporto.richiediposizioneveicolo;

import static it.vige.greenarea.Constants.BASE_URI_TAP;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.RichiestaPosizioneVeicolo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RichiediPosizioneVeicolo extends EmptyRichiediPosizioneVeicolo {
	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Richiedi Posizione Veicolo");
		try {
			String targa = (String) execution.getVariable("targa");
			String idMissione = (String) execution.getVariable("idmissione");
			RichiestaPosizioneVeicolo richiestaPosizioneVeicolo = new RichiestaPosizioneVeicolo();
			richiestaPosizioneVeicolo.setTarga(targa);
			richiestaPosizioneVeicolo.setIdMissione(idMissione);
			Client client = newClient();
			Builder bldr = client.target(BASE_URI_TAP + "/getLastPosition")
					.request(APPLICATION_JSON);
			String response = bldr.post(
					entity(richiestaPosizioneVeicolo, APPLICATION_JSON),
					String.class);
			logger.debug("coordinate calcolate: " + response);
			execution.setVariable("reportData", response);

		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorerichiestaposizioneveicolost");
		}
	}
}
