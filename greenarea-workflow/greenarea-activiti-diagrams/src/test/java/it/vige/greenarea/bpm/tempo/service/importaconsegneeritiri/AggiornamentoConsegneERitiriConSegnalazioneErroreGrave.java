package it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri;

import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.importaconsegneeritiri.EmptyAggiornamentoConsegneERitiri;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class AggiornamentoConsegneERitiriConSegnalazioneErroreGrave extends
		EmptyAggiornamentoConsegneERitiri {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_aggiornamento_consegne_e_ritiri_con_segnalazione_errore_eseguito");
		if (eseguito == null) {
			logger.info("Aggiornamento Consegne e Ritiri con Segnalazione Errore");
			execution
					.setVariable(
							"test_aggiornamento_consegne_e_ritiri_con_segnalazione_errore_eseguito",
							"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
		}
	}

}
