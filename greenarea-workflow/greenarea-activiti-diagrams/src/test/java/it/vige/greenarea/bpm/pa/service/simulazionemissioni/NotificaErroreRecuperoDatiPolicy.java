package it.vige.greenarea.bpm.pa.service.simulazionemissioni;

import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static org.slf4j.LoggerFactory.getLogger;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

import it.vige.greenarea.bpm.pa.simulazionemissioni.EmptyRecuperoDatiPolicy;
import it.vige.greenarea.bpm.risultato.Messaggio;

public class NotificaErroreRecuperoDatiPolicy extends EmptyRecuperoDatiPolicy {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_recupero_dati_policy_con_notifica_errore_eseguito");
		if (eseguito == null) {
			logger.info("Recupero Dati Policy con Notifica Errore");
			execution.setVariable(
					"test_recupero_dati_policy_con_notifica_errore_eseguito",
					"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("notificaerrorereperimentodatipolicy");
		}
	}

}
