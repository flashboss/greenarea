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
package it.vige.greenarea.bpm.operatorelogistico.service.visualizzamissioniautorizzate;

import static it.vige.greenarea.dto.AperturaRichieste._12_GIORNI_PRIMA;
import static it.vige.greenarea.dto.ChiusuraRichieste._4_ORE_PRIMA;
import static it.vige.greenarea.dto.Ripetizione.MAI;
import static it.vige.greenarea.dto.Ripetizione.TUTTI_I_GIORNI;
import static it.vige.greenarea.dto.TipologiaClassifica.PREMIA_RISPOSTA_GLOBALE;
import static it.vige.greenarea.dto.TipologiaClassifica.PREMIA_RISPOSTA_LOCALE;
import static it.vige.greenarea.dto.Tolleranza._20_PER_CENTO;
import static java.util.Arrays.asList;
import it.vige.greenarea.bpm.operatorelogistico.visualizzamissioniautorizzate.EmptyRecuperoDatiPolicy;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Prezzo;
import it.vige.greenarea.dto.GreenareaUser;

import java.util.Date;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperoDatiPolicyPopolate extends EmptyRecuperoDatiPolicy {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<FasciaOraria> policy = (List<FasciaOraria>) execution
				.getVariable("policy");
		policy.add(new FasciaOraria("06", new Date(), new Date(), new Date(),
				new Date(), _12_GIORNI_PRIMA.name(), _4_ORE_PRIMA.name(),
				TUTTI_I_GIORNI.name(), _20_PER_CENTO.name(),
				PREMIA_RISPOSTA_LOCALE.name(), new GreenareaUser("paguidonia"),
				asList(new Parametro[] {}), asList(new Prezzo[] {})));
		policy.add(new FasciaOraria("02", new Date(), new Date(), new Date(),
				new Date(), _12_GIORNI_PRIMA.name(), _4_ORE_PRIMA.name(), MAI
						.name(), _20_PER_CENTO.name(), PREMIA_RISPOSTA_GLOBALE
						.name(), new GreenareaUser("palivorno"),
				asList(new Parametro[] {}), asList(new Prezzo[] {})));
	}
}
