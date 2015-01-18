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
package it.vige.greenarea.bpm.tempo.service.autorizzamissioni;

import static it.vige.greenarea.Constants.AUTISTA;
import static it.vige.greenarea.Constants.SOCIETA_DI_TRASPORTO;
import static it.vige.greenarea.bpm.UserConverter.convertToGreenareaUser;
import static it.vige.greenarea.dto.StatoMissione.STARTED;
import static it.vige.greenarea.dto.StatoMissione.WAITING;
import static it.vige.greenarea.dto.StatoRichiesta.ACCETTATO;
import static it.vige.greenarea.dto.StatoVeicolo.DELIVERING;
import static it.vige.greenarea.dto.StatoVeicolo.IDLE;
import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static java.util.Arrays.asList;
import it.vige.greenarea.bpm.tempo.autorizzamissioni.EmptyRecuperoDatiMissioniCorrenti;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.Veicolo;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;

public class RecuperoDatiMissioniCorrentiPopolate extends
		EmptyRecuperoDatiMissioniCorrenti {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		IdentityService identityService = execution.getEngineServices()
				.getIdentityService();
		@SuppressWarnings("unchecked")
		List<Missione> missioni = (List<Missione>) execution
				.getVariableLocal("missioni");
		List<User> users = identityService.createUserQuery()
				.memberOfGroup(AUTISTA).list();
		GreenareaUser user = convertToGreenareaUser(users.get(0));
		List<User> societaDiTrasportos = identityService.createUserQuery()
				.memberOfGroup(SOCIETA_DI_TRASPORTO).list();
		GreenareaUser societaDiTrasporto = convertToGreenareaUser(societaDiTrasportos
				.get(0));
		UserQuery operatoreLogisticoQuery = identityService.createUserQuery();
		operatoreLogisticoQuery.userId("tnt");
		GreenareaUser userOP = convertToGreenareaUser(operatoreLogisticoQuery
				.singleResult());
		OperatoreLogistico operatoreLogistico = new OperatoreLogistico(userOP);
		Richiesta consegna1 = new Richiesta(CONSEGNA.name(), user,
				ACCETTATO.name());
		consegna1.setMotivazione("bello");
		Richiesta consegna2 = new Richiesta(CONSEGNA.name(), user,
				ACCETTATO.name());
		consegna2.setMotivazione("mi piace");
		missioni.add(new Missione("missione1", STARTED,
				new Veicolo(DELIVERING.name(), "targa1", user,
						societaDiTrasporto, operatoreLogistico),
				asList(new Richiesta[] { consegna1, consegna2 })));
		Richiesta consegna3 = new Richiesta(CONSEGNA.name(), user,
				ACCETTATO.name());
		consegna3.setMotivazione("bello");
		Richiesta consegna4 = new Richiesta(CONSEGNA.name(), user,
				ACCETTATO.name());
		consegna4.setMotivazione("mi piace");
		missioni.add(new Missione("missione2", WAITING, new Veicolo(
				IDLE.name(), "targa2", user, societaDiTrasporto,
				operatoreLogistico), asList(new Richiesta[] { consegna3,
				consegna4 })));
	}

}
