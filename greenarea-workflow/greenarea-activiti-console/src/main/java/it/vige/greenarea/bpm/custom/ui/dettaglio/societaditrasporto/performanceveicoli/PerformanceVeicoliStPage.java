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
package it.vige.greenarea.bpm.custom.ui.dettaglio.societaditrasporto.performanceveicoli;

import static org.activiti.explorer.ExplorerApp.get;

import org.activiti.engine.task.Task;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

import it.vige.greenarea.bpm.custom.GreenareaViewManager;
import it.vige.greenarea.bpm.custom.ui.dettaglio.DettaglioPage;

public class PerformanceVeicoliStPage extends DettaglioPage {

	private static final long serialVersionUID = 1L;

	public PerformanceVeicoliStPage(String processInstanceId, Label mainTitle) {
		super(processInstanceId, mainTitle);
	}

	@Override
	protected Component createDetailComponent(String id) {
		Task task = taskService.createTaskQuery().taskId(id).singleResult();
		Component detailComponent = new PerformanceVeicoliStPanel(task, this);
		return detailComponent;
	}

	@Override
	public void refreshSelectNext() {
		((GreenareaViewManager) get().getViewManager()).showHomePage("7");
	}

}
