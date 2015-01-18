package it.vige.greenarea.bpm.societaditrasporto.service.visualizzamissioniautorizzate;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.societaditrasporto.visualizzamissioniautorizzate.EmptyRecuperoDatiPolicy;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class NotificaErrorePolicyMancanti extends EmptyRecuperoDatiPolicy {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_notifica_dati_policy_mancanti_eseguito");
		if (eseguito == null) {
			logger.info("Recupero Dati Policy con Notifica Dati Mancanti");
			execution.setVariable(
					"test_notifica_dati_policy_mancanti_eseguito", "OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERROREDATIMANCANTI);
			throw new BpmnError("notificaerrorereperimentodatipolicy");
		}
	}

}
