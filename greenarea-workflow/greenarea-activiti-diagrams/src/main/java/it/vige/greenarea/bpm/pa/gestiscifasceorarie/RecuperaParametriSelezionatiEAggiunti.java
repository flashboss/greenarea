package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import it.vige.greenarea.dto.Parametro;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class RecuperaParametriSelezionatiEAggiunti implements TaskListener {

	private static final long serialVersionUID = -3957649983081713888L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		Collection<Parametro> parametri = (Collection<Parametro>) delegateTask
				.getVariable("parametrits");
		Collection<Parametro> parametriAggiunti = (Collection<Parametro>) delegateTask
				.getVariable("parametriaggiunti");
		delegateTask.setVariable("parametritsselezionati", parametri);
		delegateTask.setVariable("parametriaggiuntiselezionati",
				parametriAggiunti);
	}

}
