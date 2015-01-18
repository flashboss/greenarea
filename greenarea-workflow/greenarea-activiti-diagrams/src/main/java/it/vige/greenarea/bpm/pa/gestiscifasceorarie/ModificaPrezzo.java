package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Task;

public class ModificaPrezzo implements TaskListener {

	private static final long serialVersionUID = -1238187466465955160L;

	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		TaskService taskService = execution.getEngineServices()
				.getTaskService();
		List<Task> tasks = taskService.createTaskQuery()
				.processInstanceId(execution.getProcessInstanceId())
				.taskDefinitionKey("inserimentoParametri").active().list();
		for (Task task : tasks) {
			taskService.complete(task.getId());
		}
	}

}
