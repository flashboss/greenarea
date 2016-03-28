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
package it.vige.greenarea.bpm.custom.ui.admin;

import static com.vaadin.ui.themes.Reindeer.LABEL_H2;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;

import org.activiti.engine.RepositoryService;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.ui.AbstractTablePage;
import org.activiti.explorer.ui.custom.DetailPanel;

import com.vaadin.data.Item;
import com.vaadin.ui.Label;

public class AdminDetailPanel extends DetailPanel {

	private static final long serialVersionUID = 7170006501646549644L;

	private I18nManager i18nManager = get().getI18nManager();

	private transient RepositoryService repositoryService = getDefaultProcessEngine().getRepositoryService();

	private AbstractTablePage parentPage;

	public AdminDetailPanel(Item item, AbstractTablePage parentPage) {
		super();
		this.parentPage = parentPage;
		addTitle(item);
		addPage(item);
	}

	public AbstractTablePage getParentPage() {
		return parentPage;
	}

	public void setParentPage(AbstractTablePage parentPage) {
		this.parentPage = parentPage;
	}

	private void addTitle(Item item) {
		if (item != null) {
			String name = (String) item.getItemProperty("name").getValue();
			Label title = new Label(name.toUpperCase());
			title.addStyleName(LABEL_H2);
			addComponent(title);
		} else {
			String name = i18nManager.getMessage(MAIN_MENU_PA_HOME);
			Label title = new Label(name.toUpperCase());
			title.addStyleName(LABEL_H2);
			addComponent(title);
		}
	}

	private void addPage(Item item) {
		if (item != null) {
			String id = (String) item.getItemProperty("id").getValue();
			switch (id) {
			case "0-0":
				String processDefinitionId = repositoryService.createProcessDefinitionQuery().active()
						.processDefinitionKey("impostazioneFiltri").latestVersion().singleResult().getId();
				addDetailComponent(new DefinizioneFiltri(processDefinitionId, this));
				break;
			case "0-1":
				processDefinitionId = repositoryService.createProcessDefinitionQuery().active()
						.processDefinitionKey("variazioneFiltri").latestVersion().singleResult().getId();
				addDetailComponent(new LetturaFiltri(processDefinitionId, this));
				break;
			}
		} else {
			addDetailComponent(new AdminHomePage());
		}
	}
}
