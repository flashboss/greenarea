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

import static com.vaadin.ui.themes.Reindeer.LAYOUT_WHITE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.INSERIMENTO_ESEGUITO;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.navigation.ProcessNavigator.process_URI_PART;

import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.NotificationManager;
import org.activiti.explorer.navigation.UriFragment;
import org.activiti.explorer.ui.AbstractPage;
import org.activiti.explorer.ui.form.FormPropertiesEventListener;
import org.activiti.explorer.ui.form.FormPropertiesForm.FormPropertiesEvent;
import org.activiti.explorer.ui.process.AbstractProcessDefinitionDetailPanel;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import it.vige.greenarea.bpm.custom.ui.form.GreenareaFormPropertiesForm;

public class DefinizioneFiltri extends AbstractProcessDefinitionDetailPanel {

	private static final long serialVersionUID = -5903548585312958722L;

	private I18nManager i18nManager = get().getI18nManager();
	protected transient RuntimeService runtimeService = getDefaultProcessEngine().getRuntimeService();
	protected transient TaskService taskService = getDefaultProcessEngine().getTaskService();
	protected NotificationManager notificationManager = get().getNotificationManager();
	protected GreenareaFormPropertiesForm processDefinitionStartForm;
	private transient FormService formService = getDefaultProcessEngine().getFormService();

	private Label mainTitle;

	public DefinizioneFiltri(String processDefinitionId, AdminDetailPanel detailPanel) {
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

					// Show notification
					get().getMainWindow().showNotification(i18nManager.getMessage(INSERIMENTO_ESEGUITO));
				}

				protected void handleFormCancel(FormPropertiesEvent event) {
					processDefinitionStartForm.clear();
				}
			});
		}
		processDefinitionStartForm.setFormProperties(startFormData.getFormProperties());
		addComponent(processDefinitionStartForm);
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
