package it.vige.greenarea.bpm.tempo.service.acquisiscimissioni;

import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.acquisiscimissioni.EmptyAggiornamentoDatiMissioni;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class AggiornamentoDatiMissioniConSegnalazioneErrore extends
		EmptyAggiornamentoDatiMissioni {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_aggiornamento_dati_missioni_con_segnalazione_errore_eseguito");
		if (eseguito == null) {
			logger.info("Aggiornamento Dati Missioni con Segnalazione Errore");
			execution
					.setVariable(
							"test_aggiornamento_dati_missioni_con_segnalazione_errore_eseguito",
							"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
		}
	}

}
