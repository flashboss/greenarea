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
package it.vige.greenarea.bpm.trasportatoreautonomo.richiediposizioneveicolo;

import static it.vige.greenarea.Constants.BASE_URI_TAP;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.TapOutData;

import java.util.List;
import java.util.Random;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RichiediPosizioneVeicolo extends EmptyRichiediPosizioneVeicolo {
	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Richiedi Posizione Veicolo");
		try {
			String targa = (String) execution.getVariable("targa");
			String idMissione = (String) execution.getVariable("idmissione");
			logger.error("da aggiungere targa", targa);
			logger.error("da aggiungere idMissione", idMissione);

			Client client = newClient();
			Builder bldr = client.target(BASE_URI_TAP + "/getAllTapOuts")
					.request(APPLICATION_JSON);
			List<TapOutData> response = bldr
					.get(new GenericType<List<TapOutData>>() {
					});
			logger.error("response tap da implementare", response);

			// TODO Mock data recuperare da TAP
			String position = generateRandomValue();

			execution.setVariable("reportData", position);

		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorerichiestaposizioneveicolotr");
		}
	}

	private String generateRandomValue() {
		String[] positions = { "7.683043480748315,45.07581834606302",
				"7.682409633927305,45.07487119952069",
				"7.681868783695185,45.07416077238863",
				"7.68151465838447,45.07343885277235",
				"7.680901990307398,45.07261376851078",
				"7.680420114419306,45.07198619888498",
				"7.679864871888833,45.07134610032295",
				"7.679476407127355,45.07062758119076",
				"7.679162358099192,45.07032811109049" };
		int idx = new Random().nextInt(positions.length);
		return positions[idx];
	}
}
