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
package it.vige.greenarea.bpm.tempo.costruiscimissioni;

import static it.vige.greenarea.Utilities.aggiungiValoriAMissione;
import static it.vige.greenarea.Utilities.associaFasciaOrariaARichiesta;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static it.vige.greenarea.dto.StatoMissione.WAITING;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.ValoriVeicolo;
import it.vige.greenarea.dto.Veicolo;

public class CostruzioneMissionePerIlVeicolo extends EmptyCostruzioneMissionePerIlVeicolo {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Costruzione Missione per il Veicolo");

		@SuppressWarnings("unchecked")
		List<Richiesta> richieste = (List<Richiesta>) execution.getVariableLocal("richieste");

		Veicolo veicolo = (Veicolo) execution.getVariableLocal("veicolo");

		@SuppressWarnings("unchecked")
		List<FasciaOraria> fasceOrarie = (List<FasciaOraria>) execution.getVariableLocal("fasceorarie");
		Missione missione = null;
		ValoriVeicolo valoriVeicolo = veicolo.getValori();
		Map<Richiesta, FasciaOraria> richiestePerFasciaOraria = associaFasciaOrariaARichiesta(richieste, fasceOrarie,
				veicolo);
		if (richiestePerFasciaOraria.size() > 0) {
			Richiesta primaRichiesta = richiestePerFasciaOraria.keySet().iterator().next();
			missione = new Missione(primaRichiesta.getFromName(), primaRichiesta.getFromName(),
					valoriVeicolo.getLenght() + "", valoriVeicolo.getCarico() + "", valoriVeicolo.getTappe() + "",
					valoriVeicolo.getEuro() + "", valoriVeicolo.getWeight() + "", WAITING,
					new ArrayList<Richiesta>(richiestePerFasciaOraria.keySet()), veicolo,
					new Timestamp(primaRichiesta.getOrarioInizio().getTime()), null);
			aggiungiValoriAMissione(missione, richiestePerFasciaOraria);
			execution.setVariableLocal("missione", missione);
		} else {
			Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
		}
	}

}
