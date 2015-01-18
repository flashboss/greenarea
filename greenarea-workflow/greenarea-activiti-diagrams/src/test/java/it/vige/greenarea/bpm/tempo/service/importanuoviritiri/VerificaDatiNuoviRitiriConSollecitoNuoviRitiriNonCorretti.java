package it.vige.greenarea.bpm.tempo.service.importanuoviritiri;

import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATINONCORRETTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.importanuoviritiri.EmptyVerificaDatiNuoviRitiri;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class VerificaDatiNuoviRitiriConSollecitoNuoviRitiriNonCorretti extends
		EmptyVerificaDatiNuoviRitiri {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_verifica_dati_nuovi_ritiri_con_sollecito_nuovi_ritiri_eseguito");
		if (eseguito == null) {
			logger.info("Verifica Dati Nuovi Ritiri con Sollecito Nuovi Ritiri");
			execution
					.setVariable(
							"test_verifica_dati_nuovi_ritiri_con_sollecito_nuovi_ritiri_eseguito",
							"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERROREDATINONCORRETTI);
		}
	}

}
