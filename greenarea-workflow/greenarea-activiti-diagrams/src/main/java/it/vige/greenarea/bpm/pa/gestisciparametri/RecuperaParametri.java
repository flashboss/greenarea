package it.vige.greenarea.bpm.pa.gestisciparametri;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.convertiParameterGensToParametri;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.dto.Parametro;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperaParametri extends EmptyRecuperaParametri {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Recupera Parametri");
			Client client = newClient();
			Builder bldr = client.target(BASE_URI_TS + "/findAllParameterGen")
					.request(APPLICATION_JSON);
			List<ParameterGen> parameterGens = bldr
					.get(new GenericType<List<ParameterGen>>() {
					});
			if (parameterGens == null || parameterGens.size() == 0) {
				Messaggio messaggio = (Messaggio) execution
						.getVariable("messaggio");
				messaggio.setCategoria(ERRORELIEVE);
				messaggio.setTipo(ERROREDATIMANCANTI);
				throw new BpmnError("errorerecuperoparametri");
			}
			@SuppressWarnings("unchecked")
			List<Parametro> parametri = (List<Parametro>) execution
					.getVariable("parametri");
			parametri.addAll(convertiParameterGensToParametri(parameterGens));
		} catch (BpmnError ex) {
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorerecuperoparametri");
		}
	}
}