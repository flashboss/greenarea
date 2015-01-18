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
		RuntimeService runtimeService = execution.getEngineServices()
				.getRuntimeService();
		String currentUserId = (String) execution
				.getVariableLocal("currentUserId");
		List<ProcessInstance> reportProcessInstances = runtimeService
				.createProcessInstanceQuery().variableValueEquals("report")
				.involvedUser(currentUserId).list();
		for (ProcessInstance reportProcessInstance : reportProcessInstances) {
			runtimeService.deleteProcessInstance(reportProcessInstance.getId(),
					"Solo un report nella pagina");
		}

	}

}
