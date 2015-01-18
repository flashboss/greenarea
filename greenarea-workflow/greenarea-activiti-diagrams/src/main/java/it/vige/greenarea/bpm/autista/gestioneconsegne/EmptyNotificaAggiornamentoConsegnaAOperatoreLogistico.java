package it.vige.greenarea.bpm.autista.gestioneconsegne;

import static org.slf4j.LoggerFactory.getLogger;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;

public class EmptyNotificaAggiornamentoConsegnaAOperatoreLogistico implements
		TaskListener {

	private static final long serialVersionUID = -8910912400842400764L;

	private Logger logger = getLogger(getClass());

	@Override
	public void notify(DelegateTask delegateTask) {
		logger.info("Notifica Aggiornamento Missione a Operatore Logistico");
	}

}
