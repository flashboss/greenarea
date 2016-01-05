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
package it.vige.greenarea.bpm.pa.richiediaccessiga;

import static it.vige.greenarea.Constants.BASE_URI_TAP;
import static it.vige.greenarea.Utilities.dayFormatter;
import static it.vige.greenarea.Utilities.ddMMyyyy;
import static it.vige.greenarea.Utilities.hourFormatter;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.AccessiInGA;
import it.vige.greenarea.dto.RichiestaAccesso;

public class RichiediAccessiGa extends EmptyRichiediAccessiGa {
	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Richiedi Accessi GA");
		try {
			Date dal = (Date) execution.getVariable("dal");
			Date al = (Date) execution.getVariable("al");
			String operatoreLogistico = (String) execution.getVariable("operatorelogistico");

			Client client = newClient();
			Builder bldr = client.target(BASE_URI_TAP + "/storicoAccessiInGA").request(APPLICATION_JSON);
			RichiestaAccesso richiestaAccesso = new RichiestaAccesso();
			richiestaAccesso.setDataInizio(dal);
			richiestaAccesso.setDataFine(al);
			richiestaAccesso.setOperatoriLogistici(asList(new String[] { operatoreLogistico }));
			Map<Date, AccessiInGA> response = bldr.post(entity(richiestaAccesso, APPLICATION_JSON),
					new GenericType<Map<Date, AccessiInGA>>() {
					});
			execution.setVariable("dal", ddMMyyyy.format(dal));
			execution.setVariable("al", ddMMyyyy.format(al));

			// TODO Mock data da sostituire con i valori della TAP
			Map<String, Number> reportData = new LinkedHashMap<String, Number>();

			if (ddMMyyyy.format(dal).equals(ddMMyyyy.format(al))) {
				for (int i = 0; i < 24; i++) {
					int conteggioAccessi = 0;
					reportData.put("" + i, 0);
					for (Date date : response.keySet()) {
						int hour = new Integer(hourFormatter.format(date));
						if (hour == i) {
							int conteggio = response.get(date) == null ? 0 : response.get(date).getAccessi();
							conteggioAccessi += conteggio;
							reportData.put("" + i, conteggioAccessi);
						}
					}
				}
			} else {
				Calendar ss = Calendar.getInstance();
				Calendar ee = Calendar.getInstance();

				ss.setTime(dal);
				ee.setTime(al);
				ee.add(Calendar.DATE, 1);// just incrementing end date by 1

				String day = "";

				while (!ss.equals(ee)) {
					int conteggioAccessi = 0;
					day = ddMMyyyy.format(ss.getTime()).substring(0, 2);

					reportData.put(day, 0);
					for (Date date : response.keySet()) {
						String dayOut = dayFormatter.format(date);
						if (dayOut.equals(day)) {
							int conteggio = response.get(date) == null ? 0 : response.get(date).getAccessi();
							conteggioAccessi += conteggio;
							reportData.put(day, conteggioAccessi);
						}
					}

					ss.add(Calendar.DATE, 1);
				}
			}
			execution.setVariable("reportData", reportData);

		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorerichiestaaccessiga");
		}
	}

	public Integer generateRandomValue() {
		int min = 1;
		int max = 80;
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}
}
