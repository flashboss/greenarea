package it.vige.greenarea.bpm.custom.ui.pa;

import static com.vaadin.ui.themes.Reindeer.LAYOUT_WHITE;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.navigation.ProcessNavigator.process_URI_PART;
import it.vige.greenarea.bpm.custom.ui.dettaglio.pa.richiedireportmissioni.RichiediReportMissioniPAPage;
import it.vige.greenarea.bpm.custom.ui.dettaglio.pa.richiedireportmissioni.RichiediReportMissioniPAPanel;
import it.vige.greenarea.bpm.custom.ui.form.GreenareaFormPropertiesForm;

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

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class RichiediReportMissioni extends
		AbstractProcessDefinitionDetailPanel {

	private static final long serialVersionUID = -5903548585312958722L;

	protected transient RuntimeService runtimeService = getDefaultProcessEngine()
			.getRuntimeService();
	protected transient TaskService taskService = getDefaultProcessEngine()
			.getTaskService();
	protected NotificationManager notificationManager = get()
			.getNotificationManager();
	protected GreenareaFormPropertiesForm processDefinitionStartForm;

	private Label mainTitle;

	public RichiediReportMissioni(String processDefinitionId,
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

	public void showProcessStartForm(StartFormData startFormData) {
		if (processDefinitionStartForm == null) {
			processDefinitionStartForm = new GreenareaFormPropertiesForm();
			processDefinitionStartForm.setMainTitle(mainTitle);

			// When form is submitted/cancelled, show the info again
			processDefinitionStartForm
					.addListener(new FormPropertiesEventListener() {
						private static final long serialVersionUID = 1L;

						protected void handleFormSubmit(
								FormPropertiesEvent event) {
							formService.submitStartFormData(
									processDefinition.getId(),
									event.getFormProperties());
							goToDettaglio();
						}

						protected void handleFormCancel(
								FormPropertiesEvent event) {
							processDefinitionStartForm.clear();
						}
					});
		}
		processDefinitionStartForm.setFormProperties(startFormData
				.getFormProperties());
		addComponent(processDefinitionStartForm);
	}

	public void goToDettaglio() {
		// Just start the process-instance since it has no form.
		// TODO: Error handling
		String userId = get().getLoggedInUser().getId();
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery().active().involvedUser(userId)
				.processDefinitionId(processDefinition.getId()).singleResult();

		Task task = taskService.createTaskQuery()
				.processInstanceId(processInstance.getId()).singleResult();
		Component detailComponent = new RichiediReportMissioniPAPanel(task,
				new RichiediReportMissioniPAPage(processInstance.getId(),
						mainTitle));
		mainPanel.getContent().removeAllComponents();
		addComponent(processDefinitionStartForm);
		addComponent(detailComponent);
	}

	public void executeProcess() {
		// Check if process-definition defines a start-form

		StartFormData startFormData = formService
				.getStartFormData(processDefinition.getId());
		if (startFormData != null
				&& ((startFormData.getFormProperties() != null && startFormData
						.getFormProperties().size() > 0) || startFormData
						.getFormKey() != null)) {
			showStartForm(processDefinition, startFormData);
		}
	}

	protected void showProcessDefinitionDetail(String processDefinitionId) {
		changeUrl(processDefinitionId);
	}

	protected void changeUrl(String processDefinitionId) {
		UriFragment processDefinitionFragment = new UriFragment(
				process_URI_PART, processDefinitionId);
		get().setCurrentUriFragment(processDefinitionFragment);
	}

	public void showStartForm(ProcessDefinition processDefinition,
			StartFormData startFormData) {
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
