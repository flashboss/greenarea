/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
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
