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
package it.vige.greenarea.bpm.custom.ui.dettaglio.societaditrasporto.aggiornastatoveicoli;

import static com.vaadin.ui.Label.CONTENT_XHTML;
import static com.vaadin.ui.themes.Reindeer.LAYOUT_WHITE;
import static com.vaadin.ui.themes.Reindeer.PANEL_LIGHT;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.TASK_COMPLETE;
import static org.activiti.explorer.Messages.TASK_COMPLETED;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_DETAIL_BLOCK;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_DETAIL_PANEL;
import it.vige.greenarea.bpm.custom.ui.GreenareaFormLayout;
import it.vige.greenarea.bpm.custom.ui.dettaglio.DettaglioPage;
import it.vige.greenarea.bpm.custom.ui.form.GreenareaFormPropertiesForm;

import java.util.List;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.NotificationManager;
import org.activiti.explorer.ViewManager;
import org.activiti.explorer.ui.form.FormPropertiesComponent;
import org.activiti.explorer.ui.form.FormPropertiesEventListener;
import org.activiti.explorer.ui.form.FormPropertiesForm.FormPropertiesEvent;
import org.activiti.explorer.ui.task.TaskRelatedContentComponent;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class AggiornaStatoVeicoliStPanel extends VerticalLayout {

	private static final long serialVersionUID = 144744131312540177L;

	protected Panel mainPanel;

	protected Task task;

	// Services
	protected transient TaskService taskService;
	protected transient FormService formService;
	protected transient RepositoryService repositoryService;
	protected ViewManager viewManager;
	protected I18nManager i18nManager;
	protected NotificationManager notificationManager;

	// UI
	protected DettaglioPage taskPage;
	protected VerticalLayout centralLayout;
	protected GreenareaFormPropertiesForm taskForm;
	protected TaskRelatedContentComponent relatedContent;
	protected Button completeButton;

	public AggiornaStatoVeicoliStPanel(Task task, DettaglioPage taskPage) {
		this.task = task;
		this.taskPage = taskPage;

		this.taskService = getDefaultProcessEngine().getTaskService();
		this.formService = getDefaultProcessEngine().getFormService();
		this.repositoryService = getDefaultProcessEngine()
				.getRepositoryService();
		this.viewManager = get().getViewManager();
		this.i18nManager = get().getI18nManager();
		this.notificationManager = get().getNotificationManager();
		setSizeFull();
		addStyleName(STYLE_DETAIL_PANEL);

		mainPanel = new Panel();
		mainPanel.addStyleName(PANEL_LIGHT);
		mainPanel.setSizeFull();
		super.addComponent(mainPanel);
	}

	/**
	 * Set the actual content of the panel.
	 */
	public void setDetailContainer(ComponentContainer component) {
		mainPanel.setContent(component);
	}

	/**
	 * Set the component that is rendered in a fixed position below the content.
	 * When content is scrolled, this component stays visible all the time.
	 */
	public void setFixedButtons(Component component) {
		if (getComponentCount() == 2) {
			removeComponent(getComponent(1));
		}
		addComponent(component);
	}

	@Override
	public void addComponent(Component c) {
		mainPanel.addComponent(c);
	}

	/**
	 * Add component to detail-container.
	 */
	public void addDetailComponent(Component c) {
		mainPanel.addComponent(c);
	}

	@Override
	public void addComponent(Component c, int index) {
		throw new UnsupportedOperationException(
				"Cannot add components directly. Use addDetailComponent or setDetailContainer");
	}

	/**
	 * Add component to detail-container.
	 */
	public void addDetailComponent(Component c, int index) {
		if (mainPanel.getContent() instanceof AbstractOrderedLayout) {
			((AbstractOrderedLayout) mainPanel.getContent()).addComponent(c,
					index);
		} else {
			throw new UnsupportedOperationException(
					"Cannot add components indexed component, detail content is not AbstractOrderedLayout");
		}
	}

	/**
	 * Set expand-ratio of detail-component
	 */
	public void setDetailExpandRatio(Component component, float ratio) {
		if (mainPanel.getContent() instanceof AbstractOrderedLayout) {
			((AbstractOrderedLayout) mainPanel.getContent()).setExpandRatio(
					component, ratio);
		} else {
			throw new UnsupportedOperationException(
					"Cannot set ExpandRatio, detail content is not AbstractOrderedLayout");
		}
	}

	@Override
	public void addComponentAsFirst(Component c) {
		addComponent(c, 0);
	}

	public Panel getMainPanel() {
		return mainPanel;
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	protected void init() {
		setSizeFull();
		addStyleName(LAYOUT_WHITE);

		// Central panel: all task data
		centralLayout = new VerticalLayout();
		setDetailContainer(centralLayout);

		initTaskForm();

	}

	protected String getProcessDisplayName(ProcessDefinition processDefinition) {
		if (processDefinition.getName() != null) {
			return processDefinition.getName();
		} else {
			return processDefinition.getKey();
		}
	}

	protected void initTaskForm() {
		// Check if task requires a form
		TaskFormData formData = formService.getTaskFormData(task.getId());
		if (formData != null && formData.getFormProperties() != null
				&& formData.getFormProperties().size() > 0) {
			taskForm = new GreenareaFormPropertiesForm();
			taskForm.setMainTitle(taskPage.getMainTitle());
			if (task.getTaskDefinitionKey().equals("elencoVeicoli"))
				((Form) ((FormPropertiesComponent) taskForm.getComponent(1))
						.getComponent(0)).setLayout(new GreenareaFormLayout());
			taskForm.setFormProperties(formData.getFormProperties());

			final AggiornaStatoVeicoliStPanel aggiornaStatoVeicoliStPanel = this;
			taskForm.addListener(new FormPropertiesEventListener() {

				private static final long serialVersionUID = -3893467157397686736L;

				@Override
				protected void handleFormSubmit(FormPropertiesEvent event) {
					Map<String, String> properties = event.getFormProperties();
					formService.submitTaskFormData(task.getId(), properties);
					notificationManager.showInformationNotification(
							TASK_COMPLETED, task.getName());
					List<Task> tasks = taskService.createTaskQuery()
							.processInstanceId(task.getProcessInstanceId())
							.active().list();
					if (tasks.size() == 1) {
						task = tasks.get(0);
						aggiornaStatoVeicoliStPanel.setTask(task);
						aggiornaStatoVeicoliStPanel.attach();
					} else
						taskPage.refreshSelectNext();
				}

				@Override
				protected void handleFormCancel(FormPropertiesEvent event) {
					// Clear the form values
					taskForm.clear();
				}
			});
			// Only if current user is task's assignee
			taskForm.setEnabled(isCurrentUserAssignee());

			// Add component to page
			centralLayout.addComponent(taskForm);
		} else {
			// Just add a button to complete the task
			// TODO: perhaps move to a better place

			CssLayout buttonLayout = new CssLayout();
			buttonLayout.addStyleName(STYLE_DETAIL_BLOCK);
			buttonLayout.setWidth(100, UNITS_PERCENTAGE);
			centralLayout.addComponent(buttonLayout);

			completeButton = new Button(i18nManager.getMessage(TASK_COMPLETE));

			completeButton.addListener(new ClickListener() {

				private static final long serialVersionUID = 1L;

				public void buttonClick(ClickEvent event) {
					// If no owner, make assignee owner (will go into archived
					// then)
					if (task.getOwner() == null) {
						task.setOwner(task.getAssignee());
						taskService.setOwner(task.getId(), task.getAssignee());
					}

					taskService.complete(task.getId());
					notificationManager.showInformationNotification(
							TASK_COMPLETED, task.getName());
					taskPage.refreshSelectNext();
				}
			});

			completeButton.setEnabled(isCurrentUserAssignee()
					|| isCurrentUserOwner());
			buttonLayout.addComponent(completeButton);
		}
	}

	protected boolean isCurrentUserAssignee() {
		String currentUser = get().getLoggedInUser().getId();
		return currentUser.equals(task.getAssignee());
	}

	protected boolean isCurrentUserOwner() {
		String currentUser = get().getLoggedInUser().getId();
		return currentUser.equals(task.getOwner());
	}

	protected boolean canUserClaimTask() {
		return taskService.createTaskQuery()
				.taskCandidateUser(get().getLoggedInUser().getId())
				.taskId(task.getId()).count() == 1;
	}

	protected void addEmptySpace(ComponentContainer container) {
		Label emptySpace = new Label("&nbsp;", CONTENT_XHTML);
		emptySpace.setSizeUndefined();
		container.addComponent(emptySpace);
	}

	public void notifyRelatedContentChanged() {
		relatedContent.refreshTaskAttachments();
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
}