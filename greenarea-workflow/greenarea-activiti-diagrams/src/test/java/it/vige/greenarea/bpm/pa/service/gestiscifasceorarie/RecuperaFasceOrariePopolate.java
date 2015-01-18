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

import static it.vige.greenarea.dto.AccessoVeicoli.GRATUITO;
import static it.vige.greenarea.dto.AccessoVeicoli.NEGATO;
import static it.vige.greenarea.dto.AccessoVeicoli.PREZZO_FISSO;
import static it.vige.greenarea.dto.AperturaRichieste._12_GIORNI_PRIMA;
import static it.vige.greenarea.dto.ChiusuraRichieste._4_ORE_PRIMA;
import static it.vige.greenarea.dto.Color.GIALLO;
import static it.vige.greenarea.dto.Color.ROSSO;
import static it.vige.greenarea.dto.Color.VERDE;
import static it.vige.greenarea.dto.Peso.BASSO;
import static it.vige.greenarea.dto.Peso.NESSUNO;
import static it.vige.greenarea.dto.Ripetizione.FESTIVI;
import static it.vige.greenarea.dto.Ripetizione.MAI;
import static it.vige.greenarea.dto.TipologiaClassifica.CLASSIFICA_STANDARD;
import static it.vige.greenarea.dto.TipologiaParametro.BOOLEANO;
import static it.vige.greenarea.dto.TipologiaParametro.CONTATORE;
import static it.vige.greenarea.dto.Tolleranza._30_PER_CENTO;
import static java.text.DateFormat.getDateInstance;
import static java.util.Arrays.asList;
import it.vige.greenarea.bpm.pa.gestiscifasceorarie.EmptyRecuperaFasceOrarie;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Prezzo;
import it.vige.greenarea.dto.GreenareaUser;

import java.util.Date;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperaFasceOrariePopolate extends EmptyRecuperaFasceOrarie {

	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<FasciaOraria> fasceorarie = (List<FasciaOraria>) execution
				.getVariable("fasceorarie");
		fasceorarie
				.add(new FasciaOraria(
						"ga1",
						getDateInstance().parse("2012-lug-02"),
						getDateInstance().parse("2013-lug-02"),
						new Date(),
						new Date(),
						_12_GIORNI_PRIMA.name(),
						_4_ORE_PRIMA.name(),
						MAI.name(),
						_30_PER_CENTO.name(),
						CLASSIFICA_STANDARD.name(),
						new GreenareaUser("patorino"),
						asList(new Parametro[] { new Parametro("nome3",
								"descrizione3", "unitaMisura3", CONTATORE
										.name(), true, 0.0, 2.3, NESSUNO.name()) }),
						asList(new Prezzo[] {
								new Prezzo(null, ROSSO, 10.26, 5.4, 7.9, NEGATO
										.name()),
								new Prezzo(null, GIALLO, 14.2, 7.4, 8.3,
										GRATUITO.name()),
								new Prezzo(null, VERDE, 16.2, 5.4, 7.6,
										PREZZO_FISSO.name()) })));
		fasceorarie.add(new FasciaOraria("ga2", getDateInstance().parse(
				"2014-lug-02"), getDateInstance().parse("2011-lug-02"),
				new Date(), new Date(), _12_GIORNI_PRIMA.name(), _4_ORE_PRIMA
						.name(), FESTIVI.name(), _30_PER_CENTO.name(),
				CLASSIFICA_STANDARD.name(), new GreenareaUser("patorino"),
				asList(new Parametro[] { new Parametro("nome4", "descrizione4",
						"unitaMisura4", BOOLEANO.name(), false, 1.0, 3.8, BASSO
								.name()) }), asList(new Prezzo[] {
						new Prezzo(null, ROSSO, 9.2, 7.4, 8.3, NEGATO.name()),
						new Prezzo(null, GIALLO, 15.2, 4.4, 5.3, PREZZO_FISSO
								.name()),
						new Prezzo(null, VERDE, 30.2, 56.4, 6.3, GRATUITO
								.name()) })));
	}
}
