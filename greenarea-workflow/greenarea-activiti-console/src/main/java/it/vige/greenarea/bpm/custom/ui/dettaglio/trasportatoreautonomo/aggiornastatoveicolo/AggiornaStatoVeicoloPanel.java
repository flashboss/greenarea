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
package it.vige.greenarea.bpm.custom.ui.dettaglio.trasportatoreautonomo.aggiornastatoveicolo;

import it.vige.greenarea.bpm.custom.ui.dettaglio.DettaglioPage;
import it.vige.greenarea.bpm.custom.ui.dettaglio.DettaglioPanel;

import org.activiti.engine.task.Task;

public class AggiornaStatoVeicoloPanel extends DettaglioPanel {

	private static final long serialVersionUID = 144744131312540177L;

	public AggiornaStatoVeicoloPanel(Task task, DettaglioPage taskPage) {
		super(task, taskPage);
	}
}