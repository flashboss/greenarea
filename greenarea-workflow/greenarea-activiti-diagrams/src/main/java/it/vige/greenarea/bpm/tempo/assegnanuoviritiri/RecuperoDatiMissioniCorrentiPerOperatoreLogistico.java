package it.vige.greenarea.bpm.tempo.assegnanuoviritiri;

import static org.slf4j.LoggerFactory.getLogger;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperoDatiMissioniCorrentiPerOperatoreLogistico extends
		EmptyRecuperoDatiMissioniCorrentiPerOperatoreLogistico {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Recupero Dati Missioni Correnti per Operatore Logistico");
	}

}
