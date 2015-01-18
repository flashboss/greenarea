package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import it.vige.greenarea.dto.FasciaOraria;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class RecuperaFasceOrarieSelezionate implements TaskListener {

	private static final long serialVersionUID = 5030963094574234298L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		Collection<FasciaOraria> fasceOrarie = (Collection<FasciaOraria>) delegateTask
				.getVariable("fasceorarie");
		delegateTask.setVariable("fasceorarieselezionate", fasceOrarie);
	}

}
