package it.vige.greenarea.bpm.trasportatoreautonomo.aggiornastatoveicolo;

import static it.vige.greenarea.Constants.BASE_URI_USER;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.Veicolo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class AggiornaStato extends EmptyAggiornaStato {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Aggiorna Stato");
			Veicolo veicolo = (Veicolo) execution.getVariable("veicolo");
			Client client = newClient();
			Builder bldr = client.target(
					BASE_URI_USER + "/aggiornaStatoVeicolo").request(
					APPLICATION_JSON);
			veicolo = bldr.post(entity(veicolo, APPLICATION_JSON),
					Veicolo.class);
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroreAggiornaStato");
		}
	}

}
