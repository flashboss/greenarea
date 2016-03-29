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
package it.vige.greenarea.bpm.pa.test;

import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.pa.service.richiedireportmissioni.RecuperaMissioniConNotificaErroreReperimentoMissioni;
import it.vige.greenarea.bpm.pa.service.richiedireportmissioni.RecuperaMissioniPopolate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.test.ResourceActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.subethamail.smtp.server.SMTPServer;

public class RichiediReportMissioniPATest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "paguidonia";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public RichiediReportMissioniPATest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "bpm/pa/richiedi_report_missioni_pa.bpmn20.xml" })
	public void testReportSintesiOK() {
		// PARTE IL SERVER DI POSTA
		MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory();
		SMTPServer smtpServer = new SMTPServer(myFactory);
		smtpServer.setPort(25000);
		smtpServer.start();

		// RIEMPIMENTO UTENTI E GRUPPI DI TEST
		greenareaDemoData.initDemoGroups(identityService);
		greenareaDemoData.initDemoUsers(identityService);

		// AUTENTICAZIONE
		// Always reset authenticated user to avoid any mistakes
		identityService.setAuthenticatedUserId(USER_NAME);

		// INSERIMENTO FORM
		Map<String, Object> variables = new HashMap<String, Object>();
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);
		String ga = "ga";
		variables.put("ga", ga);

		// POPOLO LE MISSIONI
		ProcessDefinition reportMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel reportMissioniModel = repositoryService
				.getBpmnModel(reportMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask reportMissioniService = (ServiceTask) reportMissioniModel
				.getFlowElement("recuperaMissioni");
		reportMissioniService.setImplementation(RecuperaMissioniPopolate.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", reportMissioniModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("richiediReportMissioniPA",
				variables);

		// VERIFICO CHE CI SIA SEMPRE UN UNICO PROCESSO DI REPORT ATTIVO
		List<ProcessInstance> reportProcessInstances = runtimeService
				.createProcessInstanceQuery().variableValueEquals("report")
				.list();
		assertEquals(reportProcessInstances.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI REPORT
		List<Task> visualizzaReports = taskService.createTaskQuery()
				.taskName("Visualizza Report Sintesi")
				.includeProcessVariables().list();
		assertEquals(visualizzaReports.size(), 1);
		Task visualizzaReport = visualizzaReports.get(0);
		variables = visualizzaReport.getProcessVariables();
		assertEquals(visualizzaReport.getAssignee(), USER_NAME);

		// CANCELLAZIONE DEL REPORT
		taskService.complete(visualizzaReport.getId());

		// VERIFICO CHE IL RECUPERO DELLE MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperaMissioni = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperaMissioni").list();
		assertEquals(recuperaMissioni.size(), 1);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoMissioniAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoMissioniAOperatoreLogistico.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "bpm/pa/richiedi_report_missioni_pa.bpmn20.xml" })
	public void testNotificaErroreRecuperoMissioni() {
		// PARTE IL SERVER DI POSTA
		MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory();
		SMTPServer smtpServer = new SMTPServer(myFactory);
		smtpServer.setPort(25000);
		smtpServer.start();

		// RIEMPIMENTO UTENTI E GRUPPI DI TEST
		greenareaDemoData.initDemoGroups(identityService);
		greenareaDemoData.initDemoUsers(identityService);

		// AUTENTICAZIONE
		identityService.setAuthenticatedUserId(USER_NAME);

		// INSERIMENTO FORM
		Map<String, Object> variables = new HashMap<String, Object>();
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);
		String ga = "ga";
		variables.put("ga", ga);

		// AGGIUNGO UN ERRORE AL RECUPERO MISSIONI
		ProcessDefinition richiediReportMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel richiediReportMissioniModel = repositoryService
				.getBpmnModel(richiediReportMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperaMissioniService = (ServiceTask) richiediReportMissioniModel
				.getFlowElement("recuperaMissioni");
		recuperaMissioniService
				.setImplementation(RecuperaMissioniConNotificaErroreReperimentoMissioni.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", richiediReportMissioniModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("richiediReportMissioniPA",
				variables);

		// VERIFICO CHE LE EMAIL DI RECUPERO MISSIONI SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoMissioniAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoMissioniAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoMissioniAPA").list();
		assertEquals(notificaErroreReperimentoMissioniAPA.size(), 1);

		// VERIFICO CHE NON CI SIA NESSUN TASK PRESENTE
		List<Task> taskCorrenti = taskService.createTaskQuery().list();
		assertEquals(taskCorrenti.size(), 0);

		// VERIFICO CHE IL RECUPERO DELLE MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperaMissioni = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperaMissioni").list();
		assertEquals(recuperaMissioni.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

}
