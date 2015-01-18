package it.vige.greenarea.bpm.autista.gestionemissioni;

import org.activiti.engine.delegate.DelegateTask;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class AggiornaMissione extends EmptyAggiornaMissione {

	private static final long serialVersionUID = -6673101393886579178L;

    private Logger logger = getLogger(getClass());

	@Override
	public void notify(DelegateTask delegateTask) {
		super.notify(delegateTask);
		logger.info("CDI Aggiorna Missione");
	}

}
