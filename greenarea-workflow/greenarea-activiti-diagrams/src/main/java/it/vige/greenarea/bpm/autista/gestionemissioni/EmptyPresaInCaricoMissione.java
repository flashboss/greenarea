package it.vige.greenarea.bpm.autista.gestionemissioni;

import static it.vige.greenarea.bpm.risultato.Categoria.OK;
import static it.vige.greenarea.bpm.risultato.Tipo.NESSUNERRORE;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.Missione;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;

public class EmptyPresaInCaricoMissione implements TaskListener {

	private static final long serialVersionUID = 1382234736647448083L;

	private Logger logger = getLogger(getClass());

	@Override
	public void notify(DelegateTask delegateTask) {
		logger.info("Presa in Carico Missione");
		DelegateExecution execution = delegateTask.getExecution();
		RuntimeService runtimeService = execution.getEngineServices()
				.getRuntimeService();
		Map<String, Object> variables = new HashMap<String, Object>();
		Missione missione = (Missione) execution.getVariableLocal("missione");
		variables.put("missione", missione);
		Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
		messaggio.setCategoria(OK);
		messaggio.setTipo(NESSUNERRORE);
		runtimeService.startProcessInstanceByMessage("missionepresaincarico",
				variables);
	}

}
