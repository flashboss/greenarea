package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.convertiParameterGensToParametri;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Parametro;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperaParametriPerModifica extends
		EmptyRecuperaParametriPerModifica {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Recupera Parametri per Modifica");
			Client client = newClient();
			Builder bldr = client.target(
					BASE_URI_TS + "/findAllParameterGenAvailable").request(
					APPLICATION_JSON);
			List<ParameterGen> parameterGens = bldr
					.get(new GenericType<List<ParameterGen>>() {
					});
			@SuppressWarnings("unchecked")
			List<Parametro> parametri = (List<Parametro>) execution
					.getVariable("parametrits");
			parametri.addAll(convertiParameterGensToParametri(parameterGens));
			@SuppressWarnings("unchecked")
			List<Parametro> parametriAggiunti = (List<Parametro>) execution
					.getVariable("parametriaggiunti");
			FasciaOraria fasciaOraria = (FasciaOraria) execution
					.getVariable("fasciaoraria");
			if (fasciaOraria != null && fasciaOraria.getParametri() != null)
				for (Parametro parametro : fasciaOraria.getParametri())
					parametriAggiunti.add(parametro);
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorerecuperoparametri");
		}
	}
}
