package it.vige.greenarea.bpm.amministratore.gestiscifiltri;

import it.vige.greenarea.dto.Filtro;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class RecuperaFiltriSelezionati implements TaskListener {

	private static final long serialVersionUID = -149045147855724872L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		Collection<Filtro> filtri = (Collection<Filtro>) delegateTask
				.getVariable("filtri");
		delegateTask.setVariable("filtriselezionati", filtri);
	}

}
