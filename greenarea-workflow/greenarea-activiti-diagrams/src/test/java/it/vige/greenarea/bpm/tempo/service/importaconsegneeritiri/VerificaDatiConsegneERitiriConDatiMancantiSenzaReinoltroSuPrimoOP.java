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
package it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.importaconsegneeritiri.EmptyVerificaDatiConsegneERitiri;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class VerificaDatiConsegneERitiriConDatiMancantiSenzaReinoltroSuPrimoOP
		extends EmptyVerificaDatiConsegneERitiri {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		Object isNull = execution
				.getVariable("test_verifica_dati_consegne_e_ritiri_con_dati_mancanti_eseguito");
		int eseguito = isNull == null ? 0 : (int) isNull;
		if (eseguito < 2) {
			logger.info("Verifica Dati Consegne e Ritiri con Dati mancanti");
			execution
					.setVariable(
							"test_verifica_dati_consegne_e_ritiri_con_dati_mancanti_eseguito",
							++eseguito);
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERROREDATIMANCANTI);
		}
	}

}
