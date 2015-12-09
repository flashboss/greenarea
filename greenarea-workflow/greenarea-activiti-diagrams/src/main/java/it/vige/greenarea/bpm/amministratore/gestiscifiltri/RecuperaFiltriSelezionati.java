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
package it.vige.greenarea.bpm.amministratore.gestiscifiltri;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import it.vige.greenarea.dto.Filtro;

public class RecuperaFiltriSelezionati implements TaskListener {

	private static final long serialVersionUID = -149045147855724872L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		Collection<Filtro> filtri = (Collection<Filtro>) delegateTask.getVariable("filtri");
		delegateTask.setVariable("filtriselezionati", filtri);
	}

}
