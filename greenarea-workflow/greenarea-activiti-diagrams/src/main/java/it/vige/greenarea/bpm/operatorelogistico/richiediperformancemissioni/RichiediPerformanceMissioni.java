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
package it.vige.greenarea.bpm.operatorelogistico.richiediperformancemissioni;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static it.vige.greenarea.Utilities.ddMyyyy;
import static it.vige.greenarea.Utilities.yyyyMMddNH;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.RichiestaMissioni;

public class RichiediPerformanceMissioni extends EmptyRichiediPerformanceMissioni {
	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Richiedi Performance Missioni");
		try {
			Date dal = (Date) execution.getVariable("dal");
			Date al = (Date) execution.getVariable("al");

			Client client = newClient();
			Builder bldr = client.target(BASE_URI_RICHIESTE + "/getSintesiMissioni").request(APPLICATION_JSON);
			RichiestaMissioni richiesta = new RichiestaMissioni();
			richiesta.setDataFine(al);
			List<Missione> missioni = bldr.post(entity(richiesta, APPLICATION_JSON), new GenericType<List<Missione>>() {
			});
			Map<String, List<Missione>> elencoMissioni = new HashMap<String, List<Missione>>();
			Map<String, Double> mappaCrediti = new HashMap<String, Double>();
			if (missioni != null)
				for (Missione missione : missioni) {
					String dataInizio = yyyyMMddNH.format(missione.getDataInizio());
					String di = ddMyyyy.format(missione.getDataInizio());
					if (elencoMissioni.get(di) == null)
						elencoMissioni.put(di, new ArrayList<Missione>());
					elencoMissioni.get(di).add(missione);
					if (missione.getDataInizio().compareTo(dal) >= 0) {
						double credito = missione.getCreditoMobilita();
						double creditoIniziale = mappaCrediti.get(dataInizio) == null ? 0.0
								: mappaCrediti.get(dataInizio);
						mappaCrediti.put(dataInizio, creditoIniziale + credito);
					}
				}
			execution.setVariable("dal", ddMyyyy.format(dal));
			execution.setVariable("al", ddMyyyy.format(al));

			// TODO Mock data da sostituire con i valori della TAP
			Map<String, Number> reportData = new LinkedHashMap<String, Number>();

			Calendar ss = Calendar.getInstance();
			Calendar ee = Calendar.getInstance();

			ss.setTime(dal);
			ee.setTime(al);
			ee.add(Calendar.DATE, 1);// just incrementing end date by 1

			String day = "";

			while (!ss.equals(ee)) {
				day = ddMyyyy.format(ss.getTime());
				String data = yyyyMMddNH.format(ss.getTime());
				Double value = mappaCrediti.get(data);
				if (value != null)
					reportData.put(day, value);

				ss.add(Calendar.DATE, 1);
			}
			execution.setVariable("reportData", reportData);
			execution.setVariable("elencoMissioni", elencoMissioni);

		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorerichiestaperformancemissioni");
		}
	}
}
