package it.vige.greenarea.bpm.autista.gestioneconsegne;

import static org.slf4j.LoggerFactory.getLogger;

import org.activiti.engine.delegate.DelegateTask;
import org.slf4j.Logger;

public class AggiornaStatoInCarico extends EmptyAggiornaStatoInCarico {

	private static final long serialVersionUID = -7736374196753860784L;

	private Logger logger = getLogger(getClass());

	@Override
	public void notify(DelegateTask delegateTask) {
		super.notify(delegateTask);
		logger.info("CDI Aggiorna Stato in Carico");
	}

}
