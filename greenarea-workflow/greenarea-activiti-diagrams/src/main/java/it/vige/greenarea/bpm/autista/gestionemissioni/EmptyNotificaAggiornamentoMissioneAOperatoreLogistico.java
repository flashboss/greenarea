package it.vige.greenarea.bpm.autista.gestionemissioni;

import static org.slf4j.LoggerFactory.getLogger;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;

public class EmptyNotificaAggiornamentoMissioneAOperatoreLogistico implements
		TaskListener {

	private static final long serialVersionUID = 1382234736647448083L;

	private Logger logger = getLogger(getClass());

	@Override
	public void notify(DelegateTask delegateTask) {
		logger.info("Notifica Aggiornamento Missione a Operatore Logistico");
	}

}
