package it.vige.greenarea.bpm.pa.richiedireportmissioni;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static java.util.Collections.sort;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.DettaglioMissione;
import it.vige.greenarea.dto.RichiestaMissioni;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperaMissioni extends EmptyRecuperaMissioni {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Recupera Missioni");
			Date dal = (Date) execution.getVariable("dal");
			Date al = (Date) execution.getVariable("al");
			Client client = newClient();
			Builder bldr = null;
			bldr = client.target(BASE_URI_RICHIESTE + "/getDettaglioMissioni")
					.request(APPLICATION_JSON);
			RichiestaMissioni richiestaMissioni = new RichiestaMissioni();
			richiestaMissioni.setDataInizio(dal);
			richiestaMissioni.setDataFine(al);
			SortedSet<DettaglioMissione> response = bldr.post(
					entity(richiestaMissioni, APPLICATION_JSON),
					new GenericType<SortedSet<DettaglioMissione>>() {
					});
			@SuppressWarnings("unchecked")
			List<DettaglioMissione> missioni = (List<DettaglioMissione>) execution
					.getVariable("missioni");
			missioni.addAll(response);
			sort(missioni);
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroreRecuperoMissioni");
		}
	}

}
