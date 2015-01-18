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
package it.vige.greenarea.bpm.operatorelogistico.service.verificastatoconsegneeritiri;

import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static it.vige.greenarea.dto.TipoRichiesta.RITIRO;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.operatorelogistico.verificastatoconsegneeritiri.EmptyRichiediStati;
import it.vige.greenarea.dto.Richiesta;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RichiediStatiPopolati extends EmptyRichiediStati {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("Richiedi stati popolati");
		@SuppressWarnings("unchecked")
		List<Richiesta> stati = (List<Richiesta>) execution
				.getVariableLocal("stati");
		stati.add(new Richiesta(CONSEGNA.name()));
		stati.add(new Richiesta(RITIRO.name()));
	}

}
