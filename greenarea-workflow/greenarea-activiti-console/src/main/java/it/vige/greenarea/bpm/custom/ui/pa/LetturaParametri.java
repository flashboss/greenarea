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
package it.vige.greenarea.bpm.custom.ui.pa;

import static com.vaadin.ui.themes.Reindeer.LAYOUT_WHITE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.FASCE_ORARIE_PARAMETRI_SELEZIONA;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.navigation.ProcessNavigator.process_URI_PART;
import it.vige.greenarea.bpm.custom.ui.dettaglio.pa.letturaparametri.LetturaParametriPage;
import it.vige.greenarea.bpm.custom.ui.dettaglio.pa.letturaparametri.LetturaParametriPanel;
import it.vige.greenarea.bpm.custom.ui.form.GreenareaFormPropertiesForm;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.explorer.NotificationManager;
import org.activiti.explorer.navigation.UriFragment;
import org.activiti.explorer.ui.AbstractPage;
import org.activiti.explorer.ui.process.AbstractProcessDefinitionDetailPanel;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class LetturaParametri extends AbstractProcessDefinitionDetailPanel {

	private static final long serialVersionUID = -5903548585312958722L;

	protected transient RuntimeService runtimeService = getDefaultProcessEngine()
			.getRuntimeService();
	protected transient TaskService taskService = getDefaultProcessEngine()
			.getTaskService();
	protected NotificationManager notificationManager = get()
			.getNotificationManager();
	protected GreenareaFormPropertiesForm processDefinitionStartForm;

	private Label mainTitle;

	public LetturaParametri(String processDefinitionId,
			PADetailPanel detailPanel) {
		super(processDefinitionId, detailPanel.getParentPage());
		this.mainTitle = (Label) detailPanel.getMainPanel()
				.getComponentIterator().next();
		executeProcess();
	}

	@Override
	protected void initUi() {
		setSizeFull();
		addStyleName(LAYOUT_WHITE);

		detailPanelLayout = new VerticalLayout();
		detailPanelLayout.setWidth(100, UNITS_PERCENTAGE);
		setDetailContainer(detailPanelLayout);
	}

	@Override
	protected void initActions(AbstractPage parentPage) {
	}

	public void executeProcess() {
		// Just start the process-instance since it has no form.
		// TODO: Error handling
		ProcessInstance processInstance = runtimeService
				.startProcessInstanceById(processDefinition.getId());

		// Show notification of success
		notificationManager
				.showInformationNotification(FASCE_ORARIE_PARAMETRI_SELEZIONA);

		Task task = taskService.createTaskQuery()
				.processInstanceId(processInstance.getId()).singleResult();
		Component detailComponent = new LetturaParametriPanel(task,
				new LetturaParametriPage(processInstance.getId(), mainTitle));
		addComponent(detailComponent);
	}

	protected void showProcessDefinitionDetail(String processDefinitionId) {
		changeUrl(processDefinitionId);
	}

	protected void changeUrl(String processDefinitionId) {
		UriFragment processDefinitionFragment = new UriFragment(
				process_URI_PART, processDefinitionId);
		get().setCurrentUriFragment(processDefinitionFragment);
	}

	public Label getMainTitle() {
		return mainTitle;
	}

	public void setMainTitle(Label mainTitle) {
		this.mainTitle = mainTitle;
	}

}
