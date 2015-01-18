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
package it.vige.greenarea.bpm.operatorelogistico.monitoringmissioni;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.aborted;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.assigned;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.completed;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.incomplete;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.on_delivery;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.rejected;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.unknown;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.waiting;
import static it.vige.greenarea.dto.Selezione.TUTTE;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.RichiestaMissioni;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RichiediMissione extends EmptyRichiediMissione {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Richiedi Missioni");
		try {
			String id = (String) execution.getVariable("idmissione");
			RichiestaMissioni richiestaMissioni = new RichiestaMissioni();
			if (id != null && !id.equals(TUTTE.name())) {
				Long idMissione = new Long(id);
				richiestaMissioni.setId(idMissione);
			}
			Client client = newClient();
			Builder bldr = client.target(
					BASE_URI_RICHIESTE + "/getSintesiMissioni").request(
					APPLICATION_JSON);
			List<Missione> response = bldr.post(
					entity(richiestaMissioni, APPLICATION_JSON),
					new GenericType<List<Missione>>() {
					});
			if (response != null && response.size() > 0) {
				Missione missione = response.get(0);
				DateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy");
				execution.setVariable("dataMissione",
						dateFormat.format(missione.getDataInizio()));
				execution.setVariable("elencoStati",
						createElencoStati(missione));
			}
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorerichiestamissione");
		}
	}

	private Map<String, Number> createElencoStati(Missione missione) {
		List<Richiesta> richieste = missione.getRichieste();
		double abortedV = 0;
		double assignedV = 0;
		double completedV = 0;
		double incompleteV = 0;
		double on_deliveryV = 0;
		double rejectedV = 0;
		double unknownV = 0;
		double waitingV = 0;
		double total = richieste.size();
		for (Richiesta richiesta : richieste) {
			if (richiesta.getStato().equals(aborted.name()))
				abortedV++;
			else if (richiesta.getStato().equals(assigned.name()))
				assignedV++;
			else if (richiesta.getStato().equals(completed.name()))
				completedV++;
			else if (richiesta.getStato().equals(incomplete.name()))
				incompleteV++;
			else if (richiesta.getStato().equals(on_delivery.name()))
				on_deliveryV++;
			else if (richiesta.getStato().equals(rejected.name()))
				rejectedV++;
			else if (richiesta.getStato().equals(unknown.name()))
				unknownV++;
			else if (richiesta.getStato().equals(waiting.name()))
				waitingV++;
		}
		Map<String, Number> elencoStati = new HashMap<String, Number>();
		elencoStati.put(aborted.name(), abortedV / total * 100);
		elencoStati.put(assigned.name(), assignedV / total * 100);
		elencoStati.put(completed.name(), completedV / total * 100);
		elencoStati.put(incomplete.name(), incompleteV / total * 100);
		elencoStati.put(on_delivery.name(), on_deliveryV / total * 100);
		elencoStati.put(rejected.name(), rejectedV / total * 100);
		elencoStati.put(unknown.name(), unknownV / total * 100);
		elencoStati.put(waiting.name(), waitingV / total * 100);
		return elencoStati;
	}
}
