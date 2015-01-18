package it.vige.greenarea.bpm.pa.simulazionemissioni;

import it.vige.greenarea.dto.Missione;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class TornaAElenco implements TaskListener {

	private static final long serialVersionUID = -3616982823679097784L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		Collection<Missione> missioni = (Collection<Missione>) delegateTask
				.getVariable("missioni");
		delegateTask.setVariable("missioniselezionate", missioni);
	}
}
