package it.vige.greenarea.bpm.operatorelogistico.verificastatoconsegneeritiri;

import it.vige.greenarea.dto.Missione;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class RecuperaMissioniSelezionate implements TaskListener {

	private static final long serialVersionUID = -3763205675232111273L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		Collection<Missione> missioni = (Collection<Missione>) delegateTask
				.getVariable("missioni");
		delegateTask.setVariable("missioniselezionate", missioni);
	}

}
