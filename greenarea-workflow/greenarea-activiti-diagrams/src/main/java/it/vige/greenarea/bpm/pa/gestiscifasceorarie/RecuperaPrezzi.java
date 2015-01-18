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
package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import it.vige.greenarea.dto.FasciaOraria;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class RecuperaPrezzi implements TaskListener {

	private static final long serialVersionUID = -1238187499465955160L;

	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		FasciaOraria fasciaOraria = (FasciaOraria) execution
				.getVariable("fasciaoraria");
		execution.setVariable("giallo", fasciaOraria.getPrezzi().get(0));
		execution.setVariable("rosso", fasciaOraria.getPrezzi().get(1));
		execution.setVariable("verde", fasciaOraria.getPrezzi().get(2));
	}

}
