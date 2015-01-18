package it.vige.greenarea.bpm.operatorelogistico.anagrafeveicoli;

import static it.vige.greenarea.Constants.BASE_URI_USER;
import static it.vige.greenarea.bpm.UserConverter.convertToGreenareaUser;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.Veicolo;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.identity.User;
import org.slf4j.Logger;

public class RichiediVeicoli extends EmptyRichiediVeicoli {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Richiedi Veicoli");
		@SuppressWarnings("unchecked")
		List<Veicolo> veicoli = (List<Veicolo>) execution
				.getVariableLocal("veicoli");
		String operatoreLogistico = (String) execution
				.getVariableLocal("currentUserId");
		IdentityService identityService = execution.getEngineServices()
				.getIdentityService();
		User user = identityService.createUserQuery()
				.userId(operatoreLogistico).singleResult();
		GreenareaUser greenareaUser = convertToGreenareaUser(user);
		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/findVehicles").request(
				APPLICATION_JSON);
		Veicolo veicolo = new Veicolo();
		veicolo.setOperatoreLogistico(new OperatoreLogistico(greenareaUser));
		List<Veicolo> result = bldr.post(entity(veicolo, APPLICATION_JSON),
				new GenericType<List<Veicolo>>() {
				});
		veicoli.addAll(result);
	}
}
