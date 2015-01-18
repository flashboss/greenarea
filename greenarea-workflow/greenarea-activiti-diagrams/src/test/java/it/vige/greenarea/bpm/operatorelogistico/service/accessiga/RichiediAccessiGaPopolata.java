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
package it.vige.greenarea.bpm.operatorelogistico.service.accessiga;

import static it.vige.greenarea.bpm.risultato.Categoria.OK;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static it.vige.greenarea.bpm.risultato.Tipo.NESSUNERRORE;
import static it.vige.greenarea.dto.StatoMissione.STARTED;
import it.vige.greenarea.bpm.pa.richiediaccessiga.EmptyRichiediAccessiGa;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.Missione;

import org.activiti.engine.delegate.DelegateExecution;

public class RichiediAccessiGaPopolata extends EmptyRichiediAccessiGa {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		execution.setVariable("missione", new Missione("missione1", STARTED));
		Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
		messaggio.setCategoria(OK);
		messaggio.setTipo(ERRORESISTEMA);
		messaggio.setTipo(NESSUNERRORE);
	}

}
