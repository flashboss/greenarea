package it.vige.greenarea.bpm.tempo.service.costruiscimissioni;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.costruiscimissioni.EmptyRecuperoDelleConsegneEDeiRitiri;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class NotificaConsegneERitiriMancanti extends
		EmptyRecuperoDelleConsegneEDeiRitiri {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_notifica_consegne_e_ritiri_mancanti_eseguito");
		if (eseguito == null) {
			logger.info("Notifica Consegne e Ritiri mancanti");
			execution.setVariable(
					"test_notifica_consegne_e_ritiri_mancanti_eseguito", "OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERROREDATIMANCANTI);
		}
	}

}
