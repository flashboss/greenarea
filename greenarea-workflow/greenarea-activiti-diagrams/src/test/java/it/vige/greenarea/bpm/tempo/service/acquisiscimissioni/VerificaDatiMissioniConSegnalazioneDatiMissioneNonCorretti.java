package it.vige.greenarea.bpm.tempo.service.acquisiscimissioni;

import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATINONCORRETTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.acquisiscimissioni.EmptyVerificaDatiMissioni;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class VerificaDatiMissioniConSegnalazioneDatiMissioneNonCorretti extends
		EmptyVerificaDatiMissioni {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_verifica_dati_missioni_con_segnalazione_dati_missione_non_corretti_eseguito");
		if (eseguito == null) {
			logger.info("Verifica Dati Missioni con Segnalazione Dati Missione non corretti");
			execution
					.setVariable(
							"test_verifica_dati_missioni_con_segnalazione_dati_missione_non_corretti_eseguito",
							"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERROREDATINONCORRETTI);
		}
	}

}
