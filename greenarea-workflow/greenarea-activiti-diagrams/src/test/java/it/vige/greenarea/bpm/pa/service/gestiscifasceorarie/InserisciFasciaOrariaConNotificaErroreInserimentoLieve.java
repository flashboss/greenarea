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
package it.vige.greenarea.bpm.pa.service.gestiscifasceorarie;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.pa.gestiscifasceorarie.EmptyInserisciFasciaOraria;
import it.vige.greenarea.bpm.risultato.Messaggio;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class InserisciFasciaOrariaConNotificaErroreInserimentoLieve extends
		EmptyInserisciFasciaOraria {

	private Logger logger = getLogger(getClass());

	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_inserisci_fascia_oraria_con_notifica_errore_eseguito");
		if (eseguito == null) {
			logger.info("Inserisci Fascia Oraria con Notifica Errore Inserimento");
			execution
					.setVariable(
							"test_inserisci_fascia_oraria_con_notifica_errore_eseguito",
							"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroreinserimentofasciaoraria");
		}
	}
}
