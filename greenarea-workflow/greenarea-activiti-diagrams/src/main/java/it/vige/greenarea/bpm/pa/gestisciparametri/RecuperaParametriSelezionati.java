package it.vige.greenarea.bpm.pa.gestisciparametri;

import it.vige.greenarea.dto.Parametro;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class RecuperaParametriSelezionati implements TaskListener {

	private static final long serialVersionUID = -3763205675232111273L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		Collection<Parametro> parametri = (Collection<Parametro>) delegateTask
				.getVariable("parametri");
		delegateTask.setVariable("parametriselezionati", parametri);
	}

}
