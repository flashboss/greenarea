/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.greenarea.bpm.autista.service.gestioneconsegne;

import static it.vige.greenarea.bpm.UserConverter.convertToGreenareaUser;
import static it.vige.greenarea.dto.StatoMissione.STARTED;
import static it.vige.greenarea.dto.StatoRichiesta.ACCETTATO;
import static it.vige.greenarea.dto.StatoVeicolo.IDLE;
import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.Veicolo;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.identity.UserQuery;
import org.slf4j.Logger;

public class PopolamentoMissione implements JavaDelegate {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		logger.info("Popolamento Missioni");
		IdentityService identityService = execution.getEngineServices()
				.getIdentityService();
		UserQuery userQuery = identityService.createUserQuery();
		userQuery.userId("autista1");
		GreenareaUser user = convertToGreenareaUser(userQuery.singleResult());
		UserQuery operatoreLogisticoQuery = identityService.createUserQuery();
		operatoreLogisticoQuery.userId("tnt");
		GreenareaUser userOP = convertToGreenareaUser(operatoreLogisticoQuery
				.singleResult());
		OperatoreLogistico operatoreLogistico = new OperatoreLogistico(userOP);
		Missione missione = new Missione("missione1", STARTED, new Veicolo(
				IDLE.name(), "targa1", user, null, operatoreLogistico));
		Richiesta consegna1 = new Richiesta(CONSEGNA.name(), user,
				ACCETTATO.name());
		consegna1.setMotivazione("bello");
		Richiesta consegna2 = new Richiesta(CONSEGNA.name(), user,
				ACCETTATO.name());
		consegna2.setMotivazione("mi piace");
		missione.setRichieste(asList(new Richiesta[] { consegna1, consegna2 }));
		execution.setVariableLocal("missione", missione);
	}
}