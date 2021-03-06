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
package it.vige.greenarea.bpm.pa.service.gestisciparametri;

import static it.vige.greenarea.dto.Peso.BASSO;
import static it.vige.greenarea.dto.Peso.NESSUNO;
import static it.vige.greenarea.dto.TipologiaParametro.BENEFICIO;
import static it.vige.greenarea.dto.TipologiaParametro.COSTO;
import it.vige.greenarea.bpm.pa.gestisciparametri.EmptyRecuperaParametri;
import it.vige.greenarea.dto.Parametro;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperaParametriPopolati extends EmptyRecuperaParametri {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<Parametro> parametri = (List<Parametro>) execution
				.getVariable("parametri");
		parametri.add(new Parametro("nome1", "descrizione1", "unitaMisura1",
				BENEFICIO.name(), true, 4.1, 9.5, NESSUNO.name()));
		parametri.add(new Parametro("nome2", "descrizione2", "unitaMisura2",
				COSTO.name(), true, 2.8, 6.8, BASSO.name()));
	}
}
