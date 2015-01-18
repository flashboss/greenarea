package it.vige.greenarea.bpm.tempo.service.assegnanuoviritiri;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperoNuoviRitiriConErroreLieve extends
		RecuperoNuoviRitiriPopolati {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_recupero_nuovi_ritiri_con_errore_lieve_eseguito");
		if (eseguito == null) {
			logger.info("Recupero Nuovi Ritiri con Errore Lieve");
			execution.setVariable(
					"test_recupero_nuovi_ritiri_con_errore_lieve_eseguito",
					"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERROREDATIMANCANTI);
		}
	}

}
