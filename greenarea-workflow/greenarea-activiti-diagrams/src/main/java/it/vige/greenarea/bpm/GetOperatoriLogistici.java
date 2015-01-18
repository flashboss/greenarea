package it.vige.greenarea.bpm;

import static it.vige.greenarea.bpm.UserConverter.convertToGreenareaUser;
import it.vige.greenarea.dto.OperatoreLogistico;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;

public class GetOperatoriLogistici implements ExecutionListener {

	private static final long serialVersionUID = 7344481626773848566L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		IdentityService identityService = execution.getEngineServices()
				.getIdentityService();
		UserQuery operatoriLogisticiQuery = identityService.createUserQuery();
		operatoriLogisticiQuery.memberOfGroup("operatorelogistico");
		List<User> users = operatoriLogisticiQuery.list();
		List<OperatoreLogistico> operatoriLogistici = new ArrayList<OperatoreLogistico>();
		for (User user : users)
			operatoriLogistici.add(new OperatoreLogistico(
					convertToGreenareaUser(user)));
		execution.setVariableLocal("operatorilogistici", operatoriLogistici);

	}

}
