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
package it.vige.greenarea.bpm.custom.ui.societaditrasporto;

import static com.vaadin.ui.themes.Reindeer.LABEL_H2;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;

import org.activiti.engine.RepositoryService;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.ui.AbstractTablePage;
import org.activiti.explorer.ui.custom.DetailPanel;

import com.vaadin.data.Item;
import com.vaadin.ui.Label;

public class SocietaDiTrasportoDetailPanel extends DetailPanel {

	private static final long serialVersionUID = 4842801758674679226L;

	private I18nManager i18nManager = get().getI18nManager();

	private transient RepositoryService repositoryService = getDefaultProcessEngine()
			.getRepositoryService();

	private AbstractTablePage parentPage;

	public SocietaDiTrasportoDetailPanel(Item item, AbstractTablePage parentPage) {
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
			String name = i18nManager
					.getMessage(MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME);
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
				String processDefinitionId = repositoryService
						.createProcessDefinitionQuery()
						.active()
						.processDefinitionKey(
								"aggiornaStatoVeicoliSocietaDiTrasporto")
						.latestVersion().singleResult().getId();
				addDetailComponent(new AggiornaStatoVeicoli(
						processDefinitionId, this));
				break;
			case "1-0":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery()
						.active()
						.processDefinitionKey("visualizzaMissioniAutorizzateSt")
						.latestVersion().singleResult().getId();
				addDetailComponent(new VisualizzaMissioniAutorizzate(
						processDefinitionId, this));
				break;
			case "1-1":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("monitoringMissioniSt")
						.latestVersion().singleResult().getId();
				addDetailComponent(new MonitoringMissioni(processDefinitionId,
						this));
				break;
			case "2-0":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("posizioneVeicoloSt")
						.latestVersion().singleResult().getId();
				addDetailComponent(new PosizioneVeicolo(processDefinitionId, this));
				break;
			case "2-1":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("performanceVeicoliSt")
						.latestVersion().singleResult().getId();
				addDetailComponent(new PerformanceVeicoli(processDefinitionId,
						this));
				break;
			}
		} else {
			addDetailComponent(new SocietaDiTrasportoHomePage());
		}
	}
}
