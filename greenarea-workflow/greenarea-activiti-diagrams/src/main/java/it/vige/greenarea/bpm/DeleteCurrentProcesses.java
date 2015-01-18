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
		RuntimeService runtimeService = execution.getEngineServices()
				.getRuntimeService();
		TaskService taskService = execution.getEngineServices()
				.getTaskService();
		String currentUserId = (String) execution
				.getVariableLocal("currentUserId");
		List<ProcessInstance> processInstances = runtimeService
				.createProcessInstanceQuery()
				.processDefinitionKey(
						execution.getProcessDefinitionId().split(":")[0])
				.involvedUser(currentUserId).list();
		for (ProcessInstance processInstance : processInstances) {
			List<Task> tasks = taskService.createTaskQuery()
					.processInstanceId(processInstance.getId()).list();
			for (Task task : tasks) {
				taskService.resolveTask(task.getId());
			}
			runtimeService.deleteProcessInstance(processInstance.getId(),
					"Il processo deve essere rigenerato");
		}

	}
}
