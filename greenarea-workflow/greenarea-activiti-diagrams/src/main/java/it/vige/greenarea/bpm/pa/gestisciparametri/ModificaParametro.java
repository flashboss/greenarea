package it.vige.greenarea.bpm.pa.gestisciparametri;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.convertiParametroToParameterGen;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.dto.Parametro;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class ModificaParametro extends EmptyModificaParametro {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		ParameterGen parameterGen = null;
		try {
			super.execute(execution);
			logger.info("CDI Modifica Parametro");
			Parametro parametro = (Parametro) execution
					.getVariable("parametro");
			Client client = newClient();
			Invocation.Builder bldr = client.target(
					BASE_URI_TS + "/updateParameterGen").request(
					APPLICATION_JSON);
			parameterGen = bldr.post(
					entity(convertiParametroToParameterGen(parametro),
							APPLICATION_JSON), ParameterGen.class);
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroremodificaparametro");
		}
		if (parameterGen == null) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroremodificaparametro");
		}
	}
}