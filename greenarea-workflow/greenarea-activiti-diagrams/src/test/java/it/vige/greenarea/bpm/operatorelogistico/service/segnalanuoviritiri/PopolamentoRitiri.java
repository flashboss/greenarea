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
