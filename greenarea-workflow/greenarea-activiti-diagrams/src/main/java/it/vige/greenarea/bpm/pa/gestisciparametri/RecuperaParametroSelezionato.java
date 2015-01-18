package it.vige.greenarea.bpm.pa.gestisciparametri;

import static it.vige.greenarea.Constants.OPERAZIONE;
import static it.vige.greenarea.dto.Operazione.AGGIUNGI;
import it.vige.greenarea.dto.Parametro;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class RecuperaParametroSelezionato implements TaskListener {

	private static final long serialVersionUID = -3763205675232111273L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		String operazione = (String) delegateTask.getVariable(OPERAZIONE);
		if (!operazione.equals(AGGIUNGI.name())) {
			Collection<Parametro> parametri = (Collection<Parametro>) delegateTask
					.getVariable("parametriselezionati");
			delegateTask.setVariable("parametro", parametri.iterator().next());
		}
	}

}
