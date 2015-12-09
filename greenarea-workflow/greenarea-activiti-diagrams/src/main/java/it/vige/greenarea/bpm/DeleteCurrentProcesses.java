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
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class DeleteCurrentProcesses implements ExecutionListener {

	private static final long serialVersionUID = 7344481626773848566L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		RuntimeService runtimeService = execution.getEngineServices().getRuntimeService();
		TaskService taskService = execution.getEngineServices().getTaskService();
		String currentUserId = (String) execution.getVariableLocal("currentUserId");
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
				.processDefinitionKey(execution.getProcessDefinitionId().split(":")[0]).involvedUser(currentUserId)
				.list();
		for (ProcessInstance processInstance : processInstances) {
			List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
			for (Task task : tasks) {
				taskService.resolveTask(task.getId());
			}
			runtimeService.deleteProcessInstance(processInstance.getId(), "Il processo deve essere rigenerato");
		}

	}
}
