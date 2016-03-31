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
import it.vige.greenarea.bpm.pa.impattoambientale.EmptyRecuperoDati;
import it.vige.greenarea.bpm.pa.service.impattoambientale.RecuperoDatiConNotificaErroreReperimentoDati;

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

public class ImpattoAmbientaleTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "paguidonia";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public ImpattoAmbientaleTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "bpm/pa/impatto_ambientale.bpmn20.xml" })
	public void testOK() {
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
		String ga = "ga";
		variables.put("ga", ga);
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);

		// IL TEST NON PREVEDE L'INJECTION. RESETTO I SERVIZI
		ProcessDefinition impattoAmbientale = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel impattoAmbientaleModel = repositoryService
				.getBpmnModel(impattoAmbientale.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask inserimentoFasciaOrariaService = (ServiceTask) impattoAmbientaleModel
				.getFlowElement("recuperoDati");
		inserimentoFasciaOrariaService
				.setImplementation(EmptyRecuperoDati.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", impattoAmbientaleModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService
				.startProcessInstanceByKey("impattoAmbientale", variables);

		// VERIFICO CHE CI SIA SEMPRE UN UNICO PROCESSO DI REPORT ATTIVO
		List<ProcessInstance> reportProcessInstances = runtimeService
				.createProcessInstanceQuery().variableValueEquals("report")
				.list();
		assertEquals(reportProcessInstances.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI REPORT
		List<Task> visualizzaReports = taskService.createTaskQuery()
				.taskName("Visualizza Report").includeProcessVariables().list();
		assertEquals(visualizzaReports.size(), 1);
		Task visualizzaReport = visualizzaReports.get(0);
		variables = visualizzaReport.getProcessVariables();
		assertEquals(visualizzaReport.getAssignee(), USER_NAME);

		// CANCELLAZIONE DEL REPORT
		taskService.complete(visualizzaReport.getId());

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreInserimentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiAAmministratore")
				.list();
		assertEquals(notificaErroreInserimentoAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreInserimentoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiAPA").list();
		assertEquals(notificaErroreInserimentoAPA.size(), 0);

		// VERIFICO CHE IL RECUPERO DATI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDati = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDati").list();
		assertEquals(recuperoDati.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "bpm/pa/impatto_ambientale.bpmn20.xml" })
	public void testNotificaErroreReperimentoDati() {
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
		String ga = "ga";
		variables.put("ga", ga);
		String impattoAmbientaleStr = "ImpattoAmbientale";
		variables.put("ImpattoAmbientale", impattoAmbientaleStr);

		// AGGIUNGO UN ERRORE AL RECUPERO DATI
		ProcessDefinition impattoAmbientale = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel impattoAmbientaleModel = repositoryService
				.getBpmnModel(impattoAmbientale.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask impattoAmbientaleService = (ServiceTask) impattoAmbientaleModel
				.getFlowElement("recuperoDati");
		impattoAmbientaleService
				.setImplementation(RecuperoDatiConNotificaErroreReperimentoDati.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", impattoAmbientaleModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService
				.startProcessInstanceByKey("impattoAmbientale", variables);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreInserimentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiAAmministratore")
				.list();
		assertEquals(notificaErroreInserimentoAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreInserimentoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiAPA").list();
		assertEquals(notificaErroreInserimentoAPA.size(), 1);

		// VERIFICO LA CANCELLAZIONE DEL REPORT
		List<Task> visualizzazioneReports = taskService.createTaskQuery()
				.taskName("Visualizza Report").list();
		assertEquals(visualizzazioneReports.size(), 0);

		// VERIFICO CHE IL RECUPERO DATI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDati = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDati").list();
		assertEquals(recuperoDati.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

}
