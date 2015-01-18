package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import it.vige.greenarea.dto.Parametro;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class ModificaParametro implements TaskListener {

	private static final long serialVersionUID = -1238187466469955160L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		List<Parametro> parametri = (List<Parametro>) execution
				.getVariable("parametriaggiunti");
		Parametro parametroLocal = (Parametro) execution
				.getVariableLocal("parametroaggiunto");
		for (Parametro parametro : parametri) {
			if (parametro.getIdGen() == parametroLocal.getIdGen()) {
				parametro.setValoreMinimo(parametroLocal.getValoreMinimo());
				parametro.setValoreMassimo(parametroLocal.getValoreMassimo());
				parametro.setPeso(parametroLocal.getPeso());
			}
		}

	}

}
