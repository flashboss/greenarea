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
import it.vige.greenarea.bpm.operatorelogistico.service.accessiga.RecuperaAccessiGaConNotificaErroreReperimentoAccessi;
import it.vige.greenarea.bpm.operatorelogistico.service.accessiga.RichiediAccessiGaPopolata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.test.ResourceActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.subethamail.smtp.server.SMTPServer;

public class AccessoInGATest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "tnt";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public AccessoInGATest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/accesso_in_ga.bpmn20.xml" })
	public void testReportDettaglioOK() {
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/accesso_in_ga.bpmn20.xml" })
	public void testMonitoringAccessiGaOK() {
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
		String dal = "dal";
		variables.put("dal", dal);
		String al = "al";
		variables.put("al", al);
		String op = "op";
		variables.put("operatorelogistico", op);

		// POPOLO LE MISSIONI
		ProcessDefinition monitoringAccessiGa = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel monitoringMissioniModel = repositoryService
				.getBpmnModel(monitoringAccessiGa.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask monitoringMissioniService = (ServiceTask) monitoringMissioniModel
				.getFlowElement("richiediAccessiGa");
		monitoringMissioniService
				.setImplementation(RichiediAccessiGaPopolata.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", monitoringMissioniModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("accessoInGA", variables);

		// VERIFICO CHE IL RECUPERO DELLE MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> richiediAccessi = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("richiediAccessiGa").list();
		assertEquals(richiediAccessi.size(), 1);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiestaMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaAccessiGaAAmministratore")
				.list();
		assertEquals(segnalazioneErroreRichiestaMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreRichiestaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaAccessiGaAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaMissioneAOperatoreLogistico.size(),
				0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/accesso_in_ga.bpmn20.xml" })
	public void testNotificaErroreRecuperoAccessiGa() {
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
		String dal = "dal";
		variables.put("dal", dal);
		String al = "al";
		variables.put("al", al);
		String op = "op";
		variables.put("operatorelogistico", op);

		// AGGIUNGO UN ERRORE AL RECUPERO MISSIONI
		ProcessDefinition richiediReportAccessiGa = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel richiediReportAccessiGaModel = repositoryService
				.getBpmnModel(richiediReportAccessiGa.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediAccessiGaService = (ServiceTask) richiediReportAccessiGaModel
				.getFlowElement("richiediAccessiGa");
		richiediAccessiGaService
				.setImplementation(RecuperaAccessiGaConNotificaErroreReperimentoAccessi.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn",
						richiediReportAccessiGaModel).deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("accessoInGA", variables);

		// VERIFICO CHE LE EMAIL DI RECUPERO MISSIONI SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiestaMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaAccessiGaAAmministratore")
				.list();
		assertEquals(segnalazioneErroreRichiestaMissioneAAmministratore.size(),
				1);
		List<HistoricActivityInstance> segnalazioneErroreRichiestaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaAccessiGaAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaMissioneAOperatoreLogistico.size(),
				1);

		// VERIFICO CHE NON CI SIA NESSUN TASK PRESENTE
		List<Task> taskCorrenti = taskService.createTaskQuery().list();
		assertEquals(taskCorrenti.size(), 0);

		// VERIFICO CHE IL RECUPERO DELLE MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> richiediMissione = historyService
				.createHistoricActivityInstanceQuery().activityId("richiediAccessiGa")
				.list();
		assertEquals(richiediMissione.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

}
