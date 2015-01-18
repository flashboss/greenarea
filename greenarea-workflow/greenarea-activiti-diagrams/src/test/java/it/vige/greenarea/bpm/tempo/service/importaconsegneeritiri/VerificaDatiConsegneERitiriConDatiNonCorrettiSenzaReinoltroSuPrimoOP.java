package it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATINONCORRETTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.importaconsegneeritiri.EmptyVerificaDatiConsegneERitiri;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class VerificaDatiConsegneERitiriConDatiNonCorrettiSenzaReinoltroSuPrimoOP
		extends EmptyVerificaDatiConsegneERitiri {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		Object isNull = execution
				.getVariable("test_verifica_dati_consegne_e_ritiri_con_dati_non_corretti_eseguito");
		int eseguito = isNull == null ? 0 : (int) isNull;
		if (eseguito < 2) {
			logger.info("Verifica Dati Consegne e Ritiri con Dati non corretti");
			execution
					.setVariable(
							"test_verifica_dati_consegne_e_ritiri_con_dati_non_corretti_eseguito",
							++eseguito);
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERROREDATINONCORRETTI);
		}
	}

}
