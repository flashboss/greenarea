package it.vige.greenarea.bpm.tempo.acquisiscimissioni;

import static org.slf4j.LoggerFactory.getLogger;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperaDatiMissioni extends EmptyAggiornamentoDatiMissioni {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Recupera Dati Missioni");
	}
}