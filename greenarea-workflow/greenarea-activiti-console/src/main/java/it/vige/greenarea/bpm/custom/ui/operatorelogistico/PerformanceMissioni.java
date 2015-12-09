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
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PERFORMANCE_MISSIONI_BONUS;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PERFORMANCE_MISSIONI_NUMERO_GIALLI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PERFORMANCE_MISSIONI_NUMERO_ROSSI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PERFORMANCE_MISSIONI_NUMERO_TOTALE_MISSIONI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PERFORMANCE_MISSIONI_NUMERO_VERDI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PERFORMANCE_MISSIONI_POPUP;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PERFORMANCE_MISSIONI_TOTALE_CREDITI_CONSUMATI;
import static it.vige.greenarea.dto.Color.GIALLO;
import static it.vige.greenarea.dto.Color.ROSSO;
import static it.vige.greenarea.dto.Color.VERDE;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.navigation.ProcessNavigator.process_URI_PART;
import static org.activiti.explorer.ui.reports.ChartGenerator.CHART_TYPE_BAR_CHART;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import org.activiti.explorer.ui.custom.PopupWindow;
import org.activiti.explorer.ui.event.SubmitEvent;
import org.activiti.explorer.ui.event.SubmitEventListener;
import org.activiti.explorer.ui.form.FormPropertiesEventListener;
import org.activiti.explorer.ui.form.FormPropertiesForm.FormPropertiesEvent;
import org.activiti.explorer.ui.reports.ChartComponent;
import org.activiti.explorer.ui.reports.ChartGenerator;
import org.activiti.explorer.ui.reports.ReportDetailPanel;
import org.slf4j.Logger;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import it.vige.greenarea.bpm.custom.ui.form.GreenareaFormPropertiesForm;
import it.vige.greenarea.dto.Color;
import it.vige.greenarea.dto.Missione;

public class PerformanceMissioni extends ReportDetailPanel {

	private static final long serialVersionUID = -5903548585312958722L;
	private Logger logger = getLogger(getClass());

	protected transient RuntimeService runtimeService;
	protected transient TaskService taskService;
	protected NotificationManager notificationManager;
	protected GreenareaFormPropertiesForm processDefinitionStartForm;

	private Label mainTitle;

	private FormService formService;

	private HistoricVariableInstance historicVariableInstance;

	private HistoricVariableInstance historicVariableInstancePerMissioni;

	public PerformanceMissioni(String processDefinitionId, OperatoreLogisticoDetailPanel detailPanel) {
		super(processDefinitionId, detailPanel.getParentPage());
		this.mainTitle = (Label) detailPanel.getMainPanel().getComponentIterator().next();
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

		StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
		if (startFormData != null
				&& ((startFormData.getFormProperties() != null && startFormData.getFormProperties().size() > 0)
						|| startFormData.getFormKey() != null)) {
			showStartForm(processDefinition, startFormData);
		}
	}

	@Override
	protected void initActions() {
	}

	@Override
	public void initForm() {
		if (processDefinitionStartForm == null) {
			StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
			processDefinitionStartForm = new GreenareaFormPropertiesForm();
			processDefinitionStartForm.setMainTitle(mainTitle);
			processDefinitionStartForm.setFormProperties(startFormData.getFormProperties());
			processDefinitionStartForm.hideCancelButton();

			// When form is submitted/cancelled, show the info again
			processDefinitionStartForm.addListener(new FormPropertiesEventListener() {
				private static final long serialVersionUID = 1L;

				protected void handleFormSubmit(FormPropertiesEvent event) {
					// Report is generated by running a process and
					// storing the dataset in the history tablkes
					savedFormProperties = event.getFormProperties();
					ProcessInstance processInstance = startProcessInstanceWithFormProperties(processDefinition.getId(),
							event.getFormProperties());
					generateReport(processInstance);
				}

				protected void handleFormCancel(FormPropertiesEvent event) {
				}
			});
		}
		addComponent(processDefinitionStartForm);
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
		// Report dataset is stored as historical variable as json
		historicVariableInstance = ProcessEngines.getDefaultProcessEngine().getHistoryService()
				.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId())
				.variableName("reportData").singleResult();
		historicVariableInstancePerMissioni = ProcessEngines.getDefaultProcessEngine().getHistoryService()
				.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId())
				.variableName("elencoMissioni").singleResult();

		if (historicVariableInstance != null) {
			// Generate chart
			ChartComponent chart = createChartComponent(CHART_TYPE_BAR_CHART);
			// Put chart on screen
			if (processDefinitionStartForm != null) {
				removeComponent(processDefinitionStartForm);
				processDefinitionStartForm = null;
			}
			GreenareaFormPropertiesForm form = (GreenareaFormPropertiesForm) mainPanel.getComponentIterator().next();
			mainPanel.removeAllComponents();
			addComponent(form);
			addComponent(chart);
		}

		// The historic process instance can now be removed from the system
		// Only when save is clicked, the report will be regenerated
		ProcessEngines.getDefaultProcessEngine().getHistoryService()
				.deleteHistoricProcessInstance(processInstance.getId());
	}

	private ChartComponent createChartComponent(String type) {
		ReportData report = new ReportData();
		// report.setTitle("Crediti di mobilità residui");
		List<Dataset> datasets = new ArrayList<Dataset>();
		report.setDatasets(datasets);
		Dataset dataset = new Dataset();
		dataset.setDescription("Crediti di mobilità residui");
		dataset.setType(type);

		@SuppressWarnings("unchecked")
		Map<String, Number> elencoAccessi = (Map<String, Number>) historicVariableInstance.getValue();
		@SuppressWarnings("unchecked")
		final Map<String, List<Missione>> elencoMissioni = (Map<String, List<Missione>>) historicVariableInstancePerMissioni
				.getValue();
		filtraPerOperatoreLogistico(elencoAccessi, elencoMissioni);

		dataset.setData(elencoAccessi);
		datasets.add(dataset);

		byte[] reportData = null;
		try {
			reportData = report.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("errore conversione json", e);
		}
		final Map<String, Number> finalElencoAccessi = elencoAccessi;
		ChartComponent chart = ChartGenerator.generateChart(reportData);
		chart.setWidth(100, UNITS_PERCENTAGE);
		chart.setHeight(100, UNITS_PERCENTAGE);
		chart.addListener(new LayoutClickListener() {

			private static final long serialVersionUID = 5416440288386886066L;

			@Override
			public void layoutClick(LayoutClickEvent event) {
				String data = individuaData(event, finalElencoAccessi);
				List<Missione> missioni = elencoMissioni.get(data);
				I18nManager i18nManager = get().getI18nManager();
				PopupWindow window = new PopupWindow(i18nManager.getMessage(PERFORMANCE_MISSIONI_POPUP) + " " + data);
				VerticalLayout verticalLayout = new VerticalLayout();
				window.setContent(verticalLayout);
				Panel numeroTotaleMissioni = new Panel();
				numeroTotaleMissioni.addComponent(new Label(
						i18nManager.getMessage(PERFORMANCE_MISSIONI_NUMERO_TOTALE_MISSIONI) + " " + missioni.size()));
				verticalLayout.addComponent(numeroTotaleMissioni);
				Map<Color, Integer> totaleRanking = calcoloTotaleRanking(elencoMissioni, data);
				Panel numeroRossi = new Panel();
				int totaleRossi = totaleRanking.get(ROSSO);
				numeroRossi.addComponent(new Label(i18nManager.getMessage(PERFORMANCE_MISSIONI_NUMERO_ROSSI) + " "
						+ totaleRossi + " - " + (double) totaleRossi / missioni.size() * 100 + "%"));
				verticalLayout.addComponent(numeroRossi);
				Panel numeroVerdi = new Panel();
				int totaleVerdi = totaleRanking.get(VERDE);
				numeroVerdi.addComponent(new Label(i18nManager.getMessage(PERFORMANCE_MISSIONI_NUMERO_VERDI) + " "
						+ totaleVerdi + " - " + (double) totaleVerdi / missioni.size() * 100 + "%"));
				verticalLayout.addComponent(numeroVerdi);
				Panel numeroGialli = new Panel();
				int totaleGialli = totaleRanking.get(GIALLO);
				numeroGialli.addComponent(new Label(i18nManager.getMessage(PERFORMANCE_MISSIONI_NUMERO_GIALLI) + " "
						+ totaleGialli + " - " + (double) totaleGialli / missioni.size() * 100 + "%"));
				verticalLayout.addComponent(numeroGialli);
				Panel totaleCreditiConsumati = new Panel();
				totaleCreditiConsumati
						.addComponent(new Label(i18nManager.getMessage(PERFORMANCE_MISSIONI_TOTALE_CREDITI_CONSUMATI)
								+ " " + calcoloTotaleCreditiConsumati(finalElencoAccessi, data)));
				verticalLayout.addComponent(totaleCreditiConsumati);
				Panel bonus = new Panel();
				bonus.addComponent(new Label(i18nManager.getMessage(PERFORMANCE_MISSIONI_BONUS) + " " + 0));
				verticalLayout.addComponent(bonus);
				verticalLayout.addStyleName("main-table");
				window.addListener(new SubmitEventListener() {
					private static final long serialVersionUID = 1L;

					@Override
					protected void submitted(SubmitEvent event) {

					}

					@Override
					protected void cancelled(SubmitEvent event) {
					}
				});
				window.setWidth(450, Sizeable.UNITS_PIXELS);
				window.setHeight(450, Sizeable.UNITS_PIXELS);
				window.setModal(true);
				window.center();
				get().getViewManager().showPopupWindow(window);
			}
		});
		return chart;
	}

	private String individuaData(LayoutClickEvent event, Map<String, Number> finalElencoAccessi) {
		int x = event.getRelativeX();
		double length = event.getChildComponent().getWidth();
		int size = (int) (x / (length / finalElencoAccessi.size()));
		return finalElencoAccessi.keySet().toArray(new String[0])[size];
	}

	private String calcoloTotaleCreditiConsumati(Map<String, Number> elencoAccessi, String data) {
		double totale = 0.0;
		Set<String> keys = new TreeSet<String>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date date1 = new Date();
				Date date2 = new Date();
				try {
					date1 = dateFormat.parse(o1);
					date2 = dateFormat.parse(o2);
				} catch (ParseException e) {
					logger.error("parse della data", e);
				}
				return date1.compareTo(date2);
			}
		});
		keys.addAll(elencoAccessi.keySet());
		for (String key : keys) {
			totale += elencoAccessi.get(key).doubleValue();
			if (key.equals(data))
				return totale + "";
		}
		return totale + "";
	}

	private Map<Color, Integer> calcoloTotaleRanking(Map<String, List<Missione>> elencoMissioni, String data) {
		int totaleRossi = 0;
		int totaleVerdi = 0;
		int totaleGialli = 0;
		Map<Color, Integer> mappaRanking = new HashMap<Color, Integer>();
		mappaRanking.put(ROSSO, totaleRossi);
		mappaRanking.put(GIALLO, totaleGialli);
		mappaRanking.put(VERDE, totaleVerdi);
		Set<String> keys = new TreeSet<String>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				DateFormat dateFormat = DateFormat.getInstance();
				Date date1 = new Date();
				Date date2 = new Date();
				try {
					date1 = dateFormat.parse(o1);
					date2 = dateFormat.parse(o2);
				} catch (ParseException e) {
					logger.error("parse della data", e);
				}
				return date1.compareTo(date2);
			}
		});
		keys.addAll(elencoMissioni.keySet());
		for (String key : keys) {
			List<Missione> missioni = elencoMissioni.get(key);
			for (Missione missione : missioni) {
				if (missione.getRanking().equals(ROSSO)) {
					totaleRossi++;
					mappaRanking.put(ROSSO, totaleRossi);
				} else if (missione.getRanking().equals(GIALLO)) {
					totaleGialli++;
					mappaRanking.put(GIALLO, totaleGialli);
				} else if (missione.getRanking().equals(VERDE)) {
					totaleVerdi++;
					mappaRanking.put(VERDE, totaleVerdi);
				}
			}
			if (key.equals(data))
				return mappaRanking;
		}
		return mappaRanking;
	}

	private void filtraPerOperatoreLogistico(Map<String, Number> elencoAccessi,
			Map<String, List<Missione>> elencoMissioni) {
		String operatoreLogistico = get().getLoggedInUser().getId();
		for (String key : elencoMissioni.keySet()) {
			List<Missione> missioni = elencoMissioni.get(key);
			List<Missione> missioniDaRimuovere = new ArrayList<Missione>();
			if (elencoAccessi.containsKey(key)) {
				double value = elencoAccessi.get(key).doubleValue();
				for (Missione missione : missioni)
					if (!missione.getCompagnia().equals(operatoreLogistico)) {
						missioni.remove(missione);
						elencoAccessi.put(key, value - missione.getCreditoMobilita());
						missioniDaRimuovere.add(missione);
					}
			}
			missioni.removeAll(missioniDaRimuovere);
		}
	}

}
