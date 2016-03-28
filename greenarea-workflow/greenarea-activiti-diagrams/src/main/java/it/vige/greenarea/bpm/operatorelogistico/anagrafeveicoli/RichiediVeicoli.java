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
package it.vige.greenarea.bpm.operatorelogistico.anagrafeveicoli;

import static it.vige.greenarea.Constants.BASE_URI_USER;
import static it.vige.greenarea.bpm.UserConverter.convertToGreenareaUser;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.identity.User;
import org.slf4j.Logger;

import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Veicolo;

public class RichiediVeicoli extends EmptyRichiediVeicoli {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Richiedi Veicoli");
		@SuppressWarnings("unchecked")
		List<Veicolo> veicoli = (List<Veicolo>) execution.getVariableLocal("veicoli");
		String operatoreLogistico = (String) execution.getVariableLocal("currentUserId");
		IdentityService identityService = execution.getEngineServices().getIdentityService();
		User user = identityService.createUserQuery().userId(operatoreLogistico).singleResult();
		GreenareaUser greenareaUser = convertToGreenareaUser(user);
		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/findVehicles").request(APPLICATION_JSON);
		Veicolo veicolo = new Veicolo();
		veicolo.setOperatoreLogistico(new OperatoreLogistico(greenareaUser));
		List<Veicolo> result = bldr.post(entity(veicolo, APPLICATION_JSON), new GenericType<List<Veicolo>>() {
		});
		veicoli.addAll(result);
	}
}
