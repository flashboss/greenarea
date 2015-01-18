package it.vige.greenarea.bpm.pa.service.gestisciparametri;

import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.pa.gestisciparametri.EmptyCancellaParametro;
import it.vige.greenarea.bpm.risultato.Messaggio;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class CancellaParametroConNotificaErroreCancellazione extends
		EmptyCancellaParametro {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_cancella_parametro_con_notifica_errore_eseguito");
		if (eseguito == null) {
			logger.info("Cancella Parametro con Notifica Errore Cancellazione");
			execution.setVariable(
					"test_cancella_parametro_con_notifica_errore_eseguito",
					"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorecancellaparametro");
		}
	}

}
