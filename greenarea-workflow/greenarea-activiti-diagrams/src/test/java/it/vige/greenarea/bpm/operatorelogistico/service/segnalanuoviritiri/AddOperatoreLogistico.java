package it.vige.greenarea.bpm.operatorelogistico.service.segnalanuoviritiri;

import static it.vige.greenarea.bpm.UserConverter.convertToGreenareaUser;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.GreenareaUser;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.identity.UserQuery;

public class AddOperatoreLogistico implements ExecutionListener {

	private static final long serialVersionUID = 7344481626773848566L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		IdentityService identityService = execution.getEngineServices()
				.getIdentityService();
		UserQuery operatoreLogisticoQuery = identityService.createUserQuery();
		GreenareaUser operatoreLogistico = convertToGreenareaUser(operatoreLogisticoQuery
				.userId("pamilano").singleResult());
		execution.setVariableLocal("operatorelogistico",
				new OperatoreLogistico(operatoreLogistico));
	}

}
