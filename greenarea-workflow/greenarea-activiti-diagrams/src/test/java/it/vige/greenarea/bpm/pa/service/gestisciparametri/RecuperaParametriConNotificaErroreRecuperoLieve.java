package it.vige.greenarea.bpm.pa.service.gestisciparametri;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.pa.gestisciparametri.EmptyCancellaParametro;
import it.vige.greenarea.bpm.risultato.Messaggio;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperaParametriConNotificaErroreRecuperoLieve extends
		EmptyCancellaParametro {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_recupera_parametri_con_notifica_errore_eseguito");
		if (eseguito == null) {
			logger.info("Recupera Parametri con Notifica Errore Recupero");
			execution.setVariable(
					"test_recupera_parametri_con_notifica_errore_eseguito",
					"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERROREDATIMANCANTI);
			throw new BpmnError("errorerecuperoparametri");
		}
	}

}
