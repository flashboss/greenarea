package it.vige.greenarea.bpm.tempo.service.costruiscimissioni;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.costruiscimissioni.EmptyRecuperoDellePolicy;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.identity.User;
import org.slf4j.Logger;

public class SollecitoPolicyMancanti extends EmptyRecuperoDellePolicy {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_sollecito_policy_mancanti_eseguito");
		if (eseguito == null) {
			logger.info("Sollecito Policy Mancanti");
			execution.setVariable("test_sollecito_policy_mancanti_eseguito",
					"OK");
			@SuppressWarnings("unchecked")
			List<User> pubblicheamministrazioni = (List<User>) execution
					.getVariable("pubblicheamministrazioni");
			IdentityService identityService = execution.getEngineServices()
					.getIdentityService();
			pubblicheamministrazioni.add(identityService.createUserQuery()
					.userId("patorino").singleResult());
			pubblicheamministrazioni.add(identityService.createUserQuery()
					.userId("pamilano").singleResult());
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERROREDATIMANCANTI);
		}
	}
}
