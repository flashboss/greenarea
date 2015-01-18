package it.vige.greenarea.bpm.autista.gestioneconsegne;

import static org.slf4j.LoggerFactory.getLogger;

import org.activiti.engine.delegate.DelegateTask;
import org.slf4j.Logger;

public class AggiornaConsegna extends EmptyAggiornaConsegna {

	private static final long serialVersionUID = -3881841686637611879L;

    private Logger logger = getLogger(getClass());

	@Override
	public void notify(DelegateTask delegateTask) {
		super.notify(delegateTask);
		logger.info("CDI Aggiorna Consegna");
	}
}