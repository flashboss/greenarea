package it.vige.greenarea.bpm.tempo.service.acquisiscimissioni;

import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.acquisiscimissioni.EmptyRecuperaDatiMissioni;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperaDatiMissioniConSollecitoDatiMissioneNonTrovati extends
		EmptyRecuperaDatiMissioni {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_recupera_dati_missioni_con_sollecito_dati_missione_eseguito");
		if (eseguito == null) {
			logger.info("Recupera Dati Missioni con Sollecito Dati Missione non trovati");
			execution
					.setVariable(
							"test_recupera_dati_missioni_con_sollecito_dati_missione_eseguito",
							"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERROREDATIMANCANTI);
		}
	}

}
