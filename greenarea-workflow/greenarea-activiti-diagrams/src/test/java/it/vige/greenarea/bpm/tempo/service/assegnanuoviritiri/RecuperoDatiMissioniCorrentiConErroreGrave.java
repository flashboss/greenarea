package it.vige.greenarea.bpm.tempo.service.assegnanuoviritiri;

import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATINONCORRETTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.assegnanuoviritiri.EmptyRecuperoDatiMissioniCorrentiPerOperatoreLogistico;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperoDatiMissioniCorrentiConErroreGrave extends
		EmptyRecuperoDatiMissioniCorrentiPerOperatoreLogistico {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_recupero_missioni_con_errore_grave_eseguito");
		if (eseguito == null) {
			logger.info("Recupero Dati Missioni Correnti Per Operatore Logistico con Errore Grave");
			execution.setVariable(
					"test_recupero_missioni_con_errore_grave_eseguito", "OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERROREDATINONCORRETTI);
		}
	}

}
