package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import it.vige.greenarea.dto.Parametro;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class InserisciParametro implements TaskListener {

	private static final long serialVersionUID = -1238187466465955160L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		List<Parametro> parametri = (List<Parametro>) execution
				.getVariable("parametrits");
		Parametro parametroLocal = (Parametro) execution
				.getVariableLocal("parametrots");
		List<Parametro> parametriAggiunti = (List<Parametro>) execution
				.getVariable("parametriaggiunti");
		for (Parametro parametro : parametri) {
			if (parametro.getIdGen() == parametroLocal.getIdGen()) {
				parametro.setValoreMinimo(parametroLocal.getValoreMinimo());
				parametro.setValoreMassimo(parametroLocal.getValoreMassimo());
				parametro.setPeso(parametroLocal.getPeso());
				parametriAggiunti.add(parametro);
			}
		}

	}

}
