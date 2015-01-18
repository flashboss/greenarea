package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import it.vige.greenarea.dto.FasciaOraria;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class RecuperaPrezzi implements TaskListener {

	private static final long serialVersionUID = -1238187499465955160L;

	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		FasciaOraria fasciaOraria = (FasciaOraria) execution
				.getVariable("fasciaoraria");
		execution.setVariable("giallo", fasciaOraria.getPrezzi().get(0));
		execution.setVariable("rosso", fasciaOraria.getPrezzi().get(1));
		execution.setVariable("verde", fasciaOraria.getPrezzi().get(2));
	}

}
