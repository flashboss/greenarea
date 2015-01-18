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
package it.vige.greenarea.bpm.pa.simulazionemissioni;

import it.vige.greenarea.dto.Missione;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class TornaAElenco implements TaskListener {

	private static final long serialVersionUID = -3616982823679097784L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		Collection<Missione> missioni = (Collection<Missione>) delegateTask
				.getVariable("missioni");
		delegateTask.setVariable("missioniselezionate", missioni);
	}
}
