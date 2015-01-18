package it.vige.greenarea.bpm;

import static it.vige.greenarea.bpm.risultato.Categoria.OK;
import static it.vige.greenarea.bpm.risultato.Tipo.NESSUNERRORE;
import it.vige.greenarea.bpm.risultato.Messaggio;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;

public class InitLocalEnvironment implements ExecutionListener {

	private static final long serialVersionUID = 7344481626773848566L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		IdentityService identityService = execution.getEngineServices()
				.getIdentityService();
		UserQuery amministratoreQuery = identityService.createUserQuery();
		User amministratore = amministratoreQuery.memberOfGroup(
				"admin").singleResult();
		execution.setVariableLocal("amministratore", amministratore);
		execution
				.setVariableLocal("messaggio", new Messaggio(NESSUNERRORE, OK));
	}

}
