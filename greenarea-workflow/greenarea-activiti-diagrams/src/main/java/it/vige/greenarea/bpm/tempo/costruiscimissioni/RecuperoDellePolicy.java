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
package it.vige.greenarea.bpm.tempo.costruiscimissioni;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.convertiTimeSlotToFasciaOraria;
import static it.vige.greenarea.Utilities.setDettaglio;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.slf4j.Logger;

import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.dto.FasciaOraria;

public class RecuperoDellePolicy extends EmptyRecuperoDellePolicy {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Recupero delle Policy");
		IdentityService identityService = execution.getEngineServices().getIdentityService();
		UserQuery operatoriLogisticiQuery = identityService.createUserQuery();
		operatoriLogisticiQuery.memberOfGroup("pa");
		List<User> users = operatoriLogisticiQuery.list();
		Client client = newClient();
		@SuppressWarnings("unchecked")
		List<User> pubblicheamministrazioni = (List<User>) execution.getVariable("pubblicheamministrazioni");
		List<TimeSlot> allFasceOrarie = new ArrayList<TimeSlot>();
		try {
			for (User user : users) {
				Builder bldr = client.target(BASE_URI_TS + "/findAllTimeSlot/" + user.getId())
						.request(APPLICATION_JSON);
				List<TimeSlot> response = bldr.get(new GenericType<List<TimeSlot>>() {
				});
				if (response == null || response.size() == 0) {
					pubblicheamministrazioni.add(user);
				} else
					allFasceOrarie.addAll(response);
				logger.info(response + "");
			}
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
			return;
		}
		if (pubblicheamministrazioni.size() > 0) {
			Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERROREDATIMANCANTI);
		} else {
			@SuppressWarnings("unchecked")
			List<FasciaOraria> fasceOrarie = (List<FasciaOraria>) execution.getVariable("fasceorarie");
			for (TimeSlot timeSlot : allFasceOrarie) {
				FasciaOraria fasciaOraria = convertiTimeSlotToFasciaOraria(timeSlot, asList(new ParameterTS[] {}),
						asList(new ParameterGen[] {}), asList(new Price[] {}));
				setDettaglio(timeSlot, client, fasciaOraria);
				fasceOrarie.add(fasciaOraria);
			}
		}
	}

}
