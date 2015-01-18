package it.vige.greenarea.bpm.operatorelogistico.visualizzamissioniautorizzate;

import it.vige.greenarea.dto.Missione;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class RecuperoMissioniSelezionate implements TaskListener {

	private static final long serialVersionUID = 7049543378297226582L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		Collection<Missione> missioni = (Collection<Missione>) delegateTask
				.getVariable("missioni");
		delegateTask.setVariable("missioniselezionate", missioni);
	}

}
