package it.vige.greenarea.bpm;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.identity.User;

public class GetInitiator implements ExecutionListener {

	private static final long serialVersionUID = 7344481626773848566L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		IdentityService identityService = execution.getEngineServices()
				.getIdentityService();
		String currentUserId = (String) execution.getVariable("currentUserId");
		User user = identityService.createUserQuery().userId(currentUserId)
				.singleResult();
		execution.setVariableLocal("initiator", user);

	}

}
