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
package it.vige.greenarea.bpm.pa.simulazionemissioni;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static it.vige.greenarea.Conversioni.addDays;
import static it.vige.greenarea.Conversioni.convertiFasciaOrariaToTimeSlot;
import static it.vige.greenarea.Utilities.setDettaglio;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static it.vige.greenarea.dto.Selezione.TUTTI;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.RichiestaMissioni;

import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperoDatiMissioni extends EmptyRecuperoDatiMissioni {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Recupero Dati Missioni");
			Date dal = addDays(new Date(), 1);
			Date al = (Date) execution.getVariable("al");
			String operatoreLogistico = (String) execution
					.getVariable("operatorelogistico");
			Client client = newClient();
			Builder bldr = client.target(
					BASE_URI_RICHIESTE + "/getSintesiMissioni").request(
					APPLICATION_JSON);
			RichiestaMissioni richiestaMissioni = new RichiestaMissioni();
			richiestaMissioni.setDataInizio(dal);
			richiestaMissioni.setDataFine(al);
			if (operatoreLogistico != null
					&& !operatoreLogistico.trim().equals("")
					&& !operatoreLogistico.equals(TUTTI.name())) {
				String[] operatoriLogistici = new String[] { operatoreLogistico };
				richiestaMissioni
						.setOperatoriLogistici(asList(operatoriLogistici));
			}
			List<Missione> response = bldr.post(
					entity(richiestaMissioni, APPLICATION_JSON),
					new GenericType<List<Missione>>() {
					});
			@SuppressWarnings("unchecked")
			List<Missione> missioni = (List<Missione>) execution
					.getVariable("missioni");
			missioni.addAll(response);
			if (missioni.size() > 0) {
				FasciaOraria fasciaOraria = missioni.get(0).getFasciaOraria();
				execution.setVariable("policydetail", fasciaOraria);
				setDettaglio(convertiFasciaOrariaToTimeSlot(fasciaOraria),
						client, fasciaOraria);
			}
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("notificaerrorereperimentodatimissione");
		}
	}

}
