package it.vige.greenarea.bpm.tempo.assegnanuoviritiri;

import static org.slf4j.LoggerFactory.getLogger;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class AggiornamentoMissioneCorrente extends
		EmptyAssegnazioneRitiroAMissioneCorrente {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Aggiornamento Missione Corrente");
	}

}
