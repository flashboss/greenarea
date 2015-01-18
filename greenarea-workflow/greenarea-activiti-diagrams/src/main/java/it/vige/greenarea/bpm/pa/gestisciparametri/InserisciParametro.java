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
package it.vige.greenarea.bpm.pa.gestisciparametri;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.convertiParameterGenToParametro;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.dto.TipologiaParametro;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class InserisciParametro extends EmptyInserisciParametro {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		ParameterGen result = null;
		try {
			super.execute(execution);
			logger.info("CDI Inserisci Parametro");
			ParameterGen parameterGen = new ParameterGen();
			parameterGen.setDescription((String) execution
					.getVariable("descrizione"));
			parameterGen.setTypePG(TipologiaParametro
					.valueOf((String) execution.getVariable("tipo")));
			parameterGen.setMeasureUnit((String) execution
					.getVariable("unitamisura"));
			parameterGen.setNamePG((String) execution.getVariable("nome"));
			parameterGen.setUseType((boolean) execution
					.getVariable("statoattivazione"));
			Client client = newClient();
			Invocation.Builder bldr = client.target(
					BASE_URI_TS + "/addParameterGen").request(APPLICATION_JSON);
			result = bldr.post(entity(parameterGen, APPLICATION_JSON),
					ParameterGen.class);
			execution.setVariable("parametro",
					convertiParameterGenToParametro(result));
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroreinserimentoparametro");
		}
		if (result == null) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroreinserimentoparametro");
		}
	}
}