package it.vige.greenarea.bpm.pa.service.gestiscifasceorarie;

import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.pa.gestiscifasceorarie.EmptyModificaFasciaOraria;
import it.vige.greenarea.bpm.risultato.Messaggio;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class ModificaFasciaOrariaConNotificaErroreModificaGrave extends
		EmptyModificaFasciaOraria {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_modifica_fascia_oraria_con_notifica_errore_eseguito");
		if (eseguito == null) {
			logger.info("Modifica Fascia Oraria con Notifica Errore Inserimento");
			execution
					.setVariable(
							"test_modifica_fascia_oraria_con_notifica_errore_eseguito",
							"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroremodificafasciaoraria");
		}
	}
}
