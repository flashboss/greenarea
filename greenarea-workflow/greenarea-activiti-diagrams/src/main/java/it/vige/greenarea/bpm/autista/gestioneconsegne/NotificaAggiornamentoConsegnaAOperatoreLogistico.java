package it.vige.greenarea.bpm.autista.gestioneconsegne;

import static org.slf4j.LoggerFactory.getLogger;

import org.activiti.engine.delegate.DelegateTask;
import org.slf4j.Logger;

public class NotificaAggiornamentoConsegnaAOperatoreLogistico extends
		EmptyNotificaAggiornamentoConsegnaAOperatoreLogistico {

	private static final long serialVersionUID = -8910912400842400764L;

	private Logger logger = getLogger(getClass());

	@Override
	public void notify(DelegateTask delegateTask) {
		super.notify(delegateTask);
		logger.info("CDI Notifica Aggiornamento Consegna a Operatore Logistico");
	}

}
