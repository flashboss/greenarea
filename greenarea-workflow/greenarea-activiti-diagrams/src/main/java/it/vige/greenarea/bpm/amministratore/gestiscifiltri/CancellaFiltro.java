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
package it.vige.greenarea.bpm.amministratore.gestiscifiltri;

import static it.vige.greenarea.Constants.BASE_URI_ADMINISTRATOR;
import static it.vige.greenarea.Conversioni.convertiFiltroToFilter;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.Filter;
import it.vige.greenarea.dto.Filtro;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class CancellaFiltro extends EmptyCancellaFiltro {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Cancella Filtro");
			Filtro filtro = (Filtro) execution.getVariable("filtro");
			Client client = newClient();
			Invocation.Builder bldr = client.target(
					BASE_URI_ADMINISTRATOR + "/deleteFilter").request(
					APPLICATION_JSON);
			bldr.post(entity(convertiFiltroToFilter(filtro), APPLICATION_JSON),
					Filter.class);
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorecancellazionefiltro");
		}
	}
}
