package it.vige.greenarea.bpm.amministratore.gestiscifiltri;

import static it.vige.greenarea.Constants.BASE_URI_ADMINISTRATOR;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.Filter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class InserisciFiltro extends EmptyInserisciFiltro {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Inserisci Filtro");
			Filter filter = new Filter(execution.getVariable("filtro") + "",
					execution.getVariable("operatorelogistico") + "");
			Client client = newClient();
			Builder bldr = client.target(BASE_URI_ADMINISTRATOR + "/addFilter")
					.request(APPLICATION_JSON);
			bldr.post(entity(filter, APPLICATION_JSON),
					Filter.class);
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroreinserimentofiltro");
		}
	}
}
