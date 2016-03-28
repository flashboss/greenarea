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
package it.vige.greenarea.bpm;

import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.runtime.ProcessInstance;

public class DeleteAllReports implements ExecutionListener {

	private static final long serialVersionUID = 7344481626773848566L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		RuntimeService runtimeService = execution.getEngineServices().getRuntimeService();
		String currentUserId = (String) execution.getVariableLocal("currentUserId");
		List<ProcessInstance> reportProcessInstances = runtimeService.createProcessInstanceQuery()
				.variableValueEquals("report").involvedUser(currentUserId).list();
		for (ProcessInstance reportProcessInstance : reportProcessInstances) {
			runtimeService.deleteProcessInstance(reportProcessInstance.getId(), "Solo un report nella pagina");
		}

	}

}
