package it.vige.greenarea.bpm.pa.service.gestisciparametri;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.pa.gestisciparametri.EmptyInserisciParametro;
import it.vige.greenarea.bpm.risultato.Messaggio;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class InserisciParametroConNotificaErroreInserimentoLieve extends
		EmptyInserisciParametro {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_inserisci_parametro_con_notifica_errore_eseguito");
		if (eseguito == null) {
			logger.info("Inserisci Parametro con Notifica Errore Inserimento");
			execution.setVariable(
					"test_inserisci_parametro_con_notifica_errore_eseguito",
					"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroreinserimentoparametro");
		}
	}
}
