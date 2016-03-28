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
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.dto.Missione;

public class AggiornamentoDellaMissione extends EmptyAggiornamentoDellaMissione {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Aggiornamento della Missione");
			Missione missione = (Missione) execution.getVariable("missione");
			Client client = newClient();
			Builder bldr = client.target(BASE_URI_TS + "/TimeSlot/buildMission").request(APPLICATION_JSON);
			Mission response = bldr.post(entity(missione, APPLICATION_JSON), Mission.class);
			if (response == null) {
				Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
				messaggio.setCategoria(ERRORELIEVE);
				messaggio.setTipo(ERRORESISTEMA);
			}
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
		}
	}
}
