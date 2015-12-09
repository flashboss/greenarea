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
package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATINONCORRETTI;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.RichiestaMissioni;

public class CancellaFasciaOraria extends EmptyCancellaFasciaOraria {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		TimeSlot timeSlot = null;
		try {
			super.execute(execution);
			logger.info("CDI Cancella Fascia Oraria");
			FasciaOraria fasciaOraria = (FasciaOraria) execution.getVariable("fasciaoraria");

			Client client = newClient();

			Builder bldr = client.target(BASE_URI_RICHIESTE + "/getSintesiMissioni").request(APPLICATION_JSON);
			RichiestaMissioni richiesta = new RichiestaMissioni();
			richiesta.setFasciaOraria(fasciaOraria);
			List<Missione> response = bldr.post(entity(richiesta, APPLICATION_JSON), new GenericType<List<Missione>>() {
			});
			if (response.size() > 0) {
				Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
				messaggio.setCategoria(ERRORELIEVE);
				messaggio.setTipo(ERROREDATINONCORRETTI);
				execution.setVariable("messaggio", messaggio);
			} else {
				bldr = client.target(BASE_URI_TS + "/deleteTimeSlot").request(APPLICATION_JSON);
				timeSlot = bldr.post(entity(new TimeSlot(fasciaOraria.getId()), APPLICATION_JSON), TimeSlot.class);
			}
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorecancellazionefasciaoraria");
		}
		if (timeSlot != null) {
			Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorecancellazionefasciaoraria");
		}
	}
}
