package it.vige.greenarea.bpm.trasportatoreautonomo.aggiornastatoveicolo;

import static it.vige.greenarea.Constants.BASE_URI_USER;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.UserConverter;
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

public class RichiediVeicolo extends EmptyRichiediVeicolo {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Richiedi Veicolo");
		String autista = (String) execution.getVariableLocal("currentUserId");
		Veicolo veicolo = (Veicolo) execution.getVariableLocal("veicolo");
		IdentityService identityService = execution.getEngineServices()
				.getIdentityService();
		User user = identityService.createUserQuery().userId(autista)
				.singleResult();
		GreenareaUser greenareaUser = UserConverter.convertToGreenareaUser(user);
		veicolo.setAutista(greenareaUser);
		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/findVehicles").request(
				APPLICATION_JSON);
		List<Veicolo> veicoli = bldr.post(entity(veicolo, APPLICATION_JSON),
				new GenericType<List<Veicolo>>() {
				});
		execution.setVariableLocal("veicolo", veicoli.get(0));
	}

}
