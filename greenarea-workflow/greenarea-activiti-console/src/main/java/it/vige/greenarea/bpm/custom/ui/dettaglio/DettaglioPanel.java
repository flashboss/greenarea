package it.vige.greenarea.bpm.custom.ui.dettaglio;

import static com.vaadin.ui.Label.CONTENT_XHTML;
import static com.vaadin.ui.themes.Reindeer.LAYOUT_WHITE;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.TASK_COMPLETE;
import static org.activiti.explorer.Messages.TASK_COMPLETED;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_DETAIL_BLOCK;
import it.vige.greenarea.bpm.custom.ui.form.GreenareaFormPropertiesForm;

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
import org.activiti.explorer.ui.custom.DetailPanel;
import org.activiti.explorer.ui.form.FormPropertiesEventListener;
import org.activiti.explorer.ui.form.FormPropertiesForm.FormPropertiesEvent;
import org.activiti.explorer.ui.task.TaskRelatedContentComponent;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * The central panel on the task page, showing all the details of a task.
 * 
 * @author Joram Barrez
 */
public class DettaglioPanel extends DetailPanel {

	private static final long serialVersionUID = 1L;

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
	protected GreenareaFormPropertiesForm taskForm;
	protected TaskRelatedContentComponent relatedContent;
	protected Button completeButton;

	public DettaglioPanel(Task task, DettaglioPage taskPage) {
		this.task = task;
		this.taskPage = taskPage;

		this.taskService = getDefaultProcessEngine().getTaskService();
		this.formService = getDefaultProcessEngine().getFormService();
		this.repositoryService = getDefaultProcessEngine()
				.getRepositoryService();
		this.viewManager = get().getViewManager();
		this.i18nManager = get().getI18nManager();
		this.notificationManager = get().getNotificationManager();
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	protected void init() {
		setSizeFull();
		addStyleName(LAYOUT_WHITE);
		initTaskForm();

	}

	protected void initHeader() {
	}

	protected void initCreateTime(HorizontalLayout propertiesLayout) {
	}

	protected void initDescription(HorizontalLayout layout) {
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
			taskForm.setFormProperties(formData.getFormProperties());

			taskForm.addListener(new FormPropertiesEventListener() {

				private static final long serialVersionUID = -3893467157397686736L;

				@Override
				protected void handleFormSubmit(FormPropertiesEvent event) {
					Map<String, String> properties = event.getFormProperties();
					formService.submitTaskFormData(task.getId(), properties);
					notificationManager.showInformationNotification(
							TASK_COMPLETED, task.getName());
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
			addComponent(taskForm);
		} else {
			// Just add a button to complete the task
			// TODO: perhaps move to a better place

			CssLayout buttonLayout = new CssLayout();
			buttonLayout.addStyleName(STYLE_DETAIL_BLOCK);
			buttonLayout.setWidth(100, UNITS_PERCENTAGE);
			addComponent(buttonLayout);

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
