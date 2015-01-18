package it.vige.greenarea.bpm.operatorelogistico.service.segnalanuoviritiri;

import static it.vige.greenarea.bpm.UserConverter.convertToGreenareaUser;
import static it.vige.greenarea.dto.TipoRichiesta.RITIRO;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.slf4j.Logger;

public class PopolamentoRitiri implements JavaDelegate {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		logger.info("Popolamento Ritiri");
		IdentityService identityService = execution.getEngineServices()
				.getIdentityService();
		UserQuery operatoriLogisticiQuery = identityService.createUserQuery();
		operatoriLogisticiQuery.memberOfGroup("operatorelogistico");
		List<User> users = operatoriLogisticiQuery.list();
		List<OperatoreLogistico> operatoriLogistici = new ArrayList<OperatoreLogistico>();
		List<Richiesta> ritiri = new ArrayList<Richiesta>();
		for (User user : users) {
			OperatoreLogistico operatoreLogistico = new OperatoreLogistico(
					convertToGreenareaUser(user));
			operatoriLogistici.add(operatoreLogistico);
			List<Richiesta> ritiriOP = new ArrayList<Richiesta>();
			Richiesta richiesta1 = new Richiesta(RITIRO.name());
			Richiesta richiesta2 = new Richiesta(RITIRO.name());
			ritiriOP.add(richiesta1);
			ritiriOP.add(richiesta2);
			operatoreLogistico.setRitiri(ritiriOP);
			ritiri.addAll(ritiriOP);
		}
		execution.setVariableLocal("operatorilogistici", operatoriLogistici);
		execution.setVariableLocal("ritiri", ritiri);
	}

}
