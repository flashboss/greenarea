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
package it.vige.greenarea.bpm.custom.ui.operatorelogistico;

import static com.vaadin.ui.themes.Reindeer.LAYOUT_WHITE;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.navigation.ProcessNavigator.process_URI_PART;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.explorer.NotificationManager;
import org.activiti.explorer.navigation.UriFragment;
import org.activiti.explorer.ui.AbstractPage;
import org.activiti.explorer.ui.form.FormPropertiesEventListener;
import org.activiti.explorer.ui.form.FormPropertiesForm.FormPropertiesEvent;
import org.activiti.explorer.ui.process.AbstractProcessDefinitionDetailPanel;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import it.vige.greenarea.bpm.custom.ui.dettaglio.operatorelogistico.visualizzamissioniautorizzate.VisualizzaMissioniAutorizzateOpPage;
import it.vige.greenarea.bpm.custom.ui.dettaglio.operatorelogistico.visualizzamissioniautorizzate.VisualizzaMissioniAutorizzateOpPanel;
import it.vige.greenarea.bpm.custom.ui.form.GreenareaFormPropertiesForm;

public class VisualizzaMissioniAutorizzate extends AbstractProcessDefinitionDetailPanel {

	private static final long serialVersionUID = -5903548585312958722L;

	protected transient RuntimeService runtimeService = getDefaultProcessEngine().getRuntimeService();
	protected transient TaskService taskService = getDefaultProcessEngine().getTaskService();
	protected NotificationManager notificationManager = get().getNotificationManager();
	protected GreenareaFormPropertiesForm processDefinitionStartForm;

	private Label mainTitle;

	public VisualizzaMissioniAutorizzate(String processDefinitionId, OperatoreLogisticoDetailPanel detailPanel) {
		super(processDefinitionId, detailPanel.getParentPage());
		this.mainTitle = (Label) detailPanel.getMainPanel().getComponentIterator().next();
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

	public void showProcessStartForm(StartFormData startFormData) {
		if (processDefinitionStartForm == null) {
			processDefinitionStartForm = new GreenareaFormPropertiesForm();
			processDefinitionStartForm.setMainTitle(mainTitle);

			// When form is submitted/cancelled, show the info again
			processDefinitionStartForm.addListener(new FormPropertiesEventListener() {
				private static final long serialVersionUID = 1L;

				protected void handleFormSubmit(FormPropertiesEvent event) {
					formService.submitStartFormData(processDefinition.getId(), event.getFormProperties());
					goToDettaglio();
				}

				protected void handleFormCancel(FormPropertiesEvent event) {
					processDefinitionStartForm.clear();
				}
			});
		}
		processDefinitionStartForm.setFormProperties(startFormData.getFormProperties());
		addComponent(processDefinitionStartForm);
	}

	public void goToDettaglio() {
		// Just start the process-instance since it has no form.
		// TODO: Error handling
		String userId = get().getLoggedInUser().getId();
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().active().involvedUser(userId)
				.processDefinitionId(processDefinition.getId()).singleResult();

		Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		Component detailComponent = new VisualizzaMissioniAutorizzateOpPanel(task,
				new VisualizzaMissioniAutorizzateOpPage(processInstance.getId(), mainTitle));
		mainTitle.setPropertyDataSource(
				new ObjectProperty<String>(mainTitle.getValue() + " > " + task.getName(), String.class));
		mainPanel.getContent().removeAllComponents();
		addComponent(processDefinitionStartForm);
		addComponent(detailComponent);
	}

	public void executeProcess() {
		// Check if process-definition defines a start-form

		StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
		if (startFormData != null
				&& ((startFormData.getFormProperties() != null && startFormData.getFormProperties().size() > 0)
						|| startFormData.getFormKey() != null)) {
			showStartForm(processDefinition, startFormData);
		}
	}

	protected void showProcessDefinitionDetail(String processDefinitionId) {
		changeUrl(processDefinitionId);
	}

	protected void changeUrl(String processDefinitionId) {
		UriFragment processDefinitionFragment = new UriFragment(process_URI_PART, processDefinitionId);
		get().setCurrentUriFragment(processDefinitionFragment);
	}

	public void showStartForm(ProcessDefinition processDefinition, StartFormData startFormData) {
		showProcessDefinitionDetail(processDefinition.getId());
		showProcessStartForm(startFormData);
	}

	public Label getMainTitle() {
		return mainTitle;
	}

	public void setMainTitle(Label mainTitle) {
		this.mainTitle = mainTitle;
	}

}
