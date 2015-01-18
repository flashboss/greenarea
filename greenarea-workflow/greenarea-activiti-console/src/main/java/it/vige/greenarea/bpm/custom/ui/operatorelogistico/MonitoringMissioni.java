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
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MONITORING_MISSIONI_OP_DATA;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MONITORING_MISSIONI_OP_SPEDIZIONI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MONITORING_MISSIONI_OP_STATO;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.navigation.ProcessNavigator.process_URI_PART;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.custom.ui.form.GreenareaFormPropertiesForm;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.NotificationManager;
import org.activiti.explorer.navigation.UriFragment;
import org.activiti.explorer.reporting.Dataset;
import org.activiti.explorer.reporting.ReportData;
import org.activiti.explorer.ui.form.FormPropertiesEventListener;
import org.activiti.explorer.ui.form.FormPropertiesForm.FormPropertiesEvent;
import org.activiti.explorer.ui.reports.ChartComponent;
import org.activiti.explorer.ui.reports.ChartGenerator;
import org.activiti.explorer.ui.reports.ReportDetailPanel;
import org.slf4j.Logger;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MonitoringMissioni extends ReportDetailPanel {

	private static final long serialVersionUID = -5903548585312958722L;

	protected transient RuntimeService runtimeService;
	protected transient TaskService taskService;
	protected NotificationManager notificationManager;
	protected GreenareaFormPropertiesForm processDefinitionStartForm;

	private Logger logger = getLogger(getClass());

	private Label mainTitle;

	private FormService formService;

	public MonitoringMissioni(String processDefinitionId,
			OperatoreLogisticoDetailPanel detailPanel) {
		super(processDefinitionId, detailPanel.getParentPage());
		this.mainTitle = (Label) detailPanel.getMainPanel()
				.getComponentIterator().next();
	}

	@Override
	protected void initUi() {
		runtimeService = getDefaultProcessEngine().getRuntimeService();
		taskService = getDefaultProcessEngine().getTaskService();
		notificationManager = get().getNotificationManager();
		formService = getDefaultProcessEngine().getFormService();
		setSizeFull();
		addStyleName(LAYOUT_WHITE);

		detailPanelLayout = new VerticalLayout();
		detailPanelLayout.setWidth(100, UNITS_PERCENTAGE);
		setDetailContainer(detailPanelLayout);
		executeProcess();
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

	@Override
	protected void initActions() {
	}

	@Override
	public void initForm() {
		if (processDefinitionStartForm == null) {
			StartFormData startFormData = formService
					.getStartFormData(processDefinition.getId());
			processDefinitionStartForm = new GreenareaFormPropertiesForm();
			processDefinitionStartForm.setMainTitle(mainTitle);
			processDefinitionStartForm.setFormProperties(startFormData
					.getFormProperties());
			processDefinitionStartForm.hideCancelButton();

			// When form is submitted/cancelled, show the info again
			processDefinitionStartForm
					.addListener(new FormPropertiesEventListener() {
						private static final long serialVersionUID = 1L;

						protected void handleFormSubmit(
								FormPropertiesEvent event) {
							// Report is generated by running a process and
							// storing the dataset in the history tablkes
							savedFormProperties = event.getFormProperties();
							ProcessInstance processInstance = startProcessInstanceWithFormProperties(
									processDefinition.getId(),
									event.getFormProperties());
							generateReport(processInstance);
						}

						protected void handleFormCancel(
								FormPropertiesEvent event) {
						}
					});
		}
		addComponent(processDefinitionStartForm);
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
		initForm();
	}

	public Label getMainTitle() {
		return mainTitle;
	}

	public void setMainTitle(Label mainTitle) {
		this.mainTitle = mainTitle;
	}

	@Override
	protected void generateReport(ProcessInstance processInstance) {
		I18nManager i18nManager = get().getI18nManager();
		HistoricVariableInstance elencoStatiInstance = ProcessEngines
				.getDefaultProcessEngine().getHistoryService()
				.createHistoricVariableInstanceQuery()
				.processInstanceId(processInstance.getId())
				.variableName("elencoStati").singleResult();
		HistoricVariableInstance dataMissioneInstance = ProcessEngines
				.getDefaultProcessEngine().getHistoryService()
				.createHistoricVariableInstanceQuery()
				.processInstanceId(processInstance.getId())
				.variableName("dataMissione").singleResult();
		GreenareaFormPropertiesForm form = (GreenareaFormPropertiesForm) mainPanel
				.getComponentIterator().next();
		mainPanel.removeAllComponents();
		addComponent(form);
		if (dataMissioneInstance != null) {
			ReportData report = new ReportData();
			report.setTitle(i18nManager.getMessage(MONITORING_MISSIONI_OP_DATA)
					+ " " + dataMissioneInstance.getValue() + "");
			List<Dataset> datasets = new ArrayList<Dataset>();
			report.setDatasets(datasets);
			Dataset dataset = new Dataset();
			dataset.setDescription(i18nManager
					.getMessage(MONITORING_MISSIONI_OP_SPEDIZIONI));
			dataset.setType("pieChart");
			@SuppressWarnings("unchecked")
			Map<String, Number> elencoStati = (Map<String, Number>) elencoStatiInstance
					.getValue();
			Map<String, Number> i18nElencoStati = new HashMap<String, Number>();
			for (String key : elencoStati.keySet()) {
				i18nElencoStati.put(
						i18nManager.getMessage(MONITORING_MISSIONI_OP_STATO
								+ key), elencoStati.get(key));
			}
			dataset.setData(i18nElencoStati);
			datasets.add(dataset);

			byte[] reportData = null;
			try {
				reportData = report.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("errore conversione json", e);
			}
			ChartComponent chart = ChartGenerator.generateChart(reportData);
			chart.setWidth(100, UNITS_PERCENTAGE);
			chart.setHeight(100, UNITS_PERCENTAGE);

			// Put chart on screen
			if (processDefinitionStartForm != null) {
				removeComponent(processDefinitionStartForm);
				processDefinitionStartForm = null;
			}
			addComponent(chart);
		}
		// The historic process instance can now be removed from the system
		// Only when save is clicked, the report will be regenerated
		ProcessEngines.getDefaultProcessEngine().getHistoryService()
				.deleteHistoricProcessInstance(processInstance.getId());
	}

}
