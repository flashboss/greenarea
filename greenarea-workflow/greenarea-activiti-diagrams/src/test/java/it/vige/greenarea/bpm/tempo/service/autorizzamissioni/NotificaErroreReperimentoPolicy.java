package it.vige.greenarea.bpm.tempo.service.autorizzamissioni;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.autorizzamissioni.EmptyRecuperoDatiPolicy;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class NotificaErroreReperimentoPolicy extends EmptyRecuperoDatiPolicy {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_sollecito_policy_mancanti_eseguito");
		if (eseguito == null) {
			logger.info("Sollecito Policy Mancanti");
			execution.setVariable("test_sollecito_policy_mancanti_eseguito",
					"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
		}
	}
}