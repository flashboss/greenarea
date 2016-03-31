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
package it.vige.greenarea.bpm.societaditrasporto.test;

import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.societaditrasporto.service.monitoringmissioni.RecuperaMissioniConNotificaErroreReperimentoMissioni;
import it.vige.greenarea.bpm.societaditrasporto.service.monitoringmissioni.RichiediMissionePopolata;

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

public class MonitoringMissioniSocietaDiTrasportoTest extends
		ResourceActivitiTestCase {

	private final static String USER_NAME = "trambus";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public MonitoringMissioniSocietaDiTrasportoTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "bpm/sdt/monitoring_missioni.bpmn20.xml" })
	public void testMonitoringMissioniOK() {
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
		String idmissione = "151";
		variables.put("idmissione", idmissione);
		String ga = "ga";
		variables.put("ga", ga);
		String operatoreLogistico = "tnt";
		variables.put("operatoreLogistico", operatoreLogistico);

		// POPOLO LE MISSIONI
		ProcessDefinition monitoringMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel monitoringMissioniModel = repositoryService
				.getBpmnModel(monitoringMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask monitoringMissioniService = (ServiceTask) monitoringMissioniModel
				.getFlowElement("richiediMissione");
		monitoringMissioniService
				.setImplementation(RichiediMissionePopolata.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", monitoringMissioniModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("monitoringMissioniSt",
				variables);

		// VERIFICO CHE IL RECUPERO DELLE MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> richiediMissione = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("richiediMissione").list();
		assertEquals(richiediMissione.size(), 1);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiestaMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaMissioneAAmministratore")
				.list();
		assertEquals(segnalazioneErroreRichiestaMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreRichiestaMissioneASocietaDiTrasporto = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaMissioneASocietaDiTrasporto")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaMissioneASocietaDiTrasporto.size(),
				0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "bpm/sdt/monitoring_missioni.bpmn20.xml" })
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
		String idmissione = "151";
		variables.put("idmissione", idmissione);
		String ga = "ga";
		variables.put("ga", ga);
		String operatoreLogistico = "tnt";
		variables.put("operatoreLogistico", operatoreLogistico);

		// AGGIUNGO UN ERRORE AL RECUPERO MISSIONI
		ProcessDefinition richiediReportMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel richiediReportMissioniModel = repositoryService
				.getBpmnModel(richiediReportMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediMissioneService = (ServiceTask) richiediReportMissioniModel
				.getFlowElement("richiediMissione");
		richiediMissioneService
				.setImplementation(RecuperaMissioniConNotificaErroreReperimentoMissioni.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", richiediReportMissioniModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("monitoringMissioniSt",
				variables);

		// VERIFICO CHE LE EMAIL DI RECUPERO MISSIONI SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiestaMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaMissioneAAmministratore")
				.list();
		assertEquals(segnalazioneErroreRichiestaMissioneAAmministratore.size(),
				1);
		List<HistoricActivityInstance> segnalazioneErroreRichiestaMissioneASocietaDiTrasporto = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaMissioneASocietaDiTrasporto")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaMissioneASocietaDiTrasporto.size(),
				1);

		// VERIFICO CHE NON CI SIA NESSUN TASK PRESENTE
		List<Task> taskCorrenti = taskService.createTaskQuery().list();
		assertEquals(taskCorrenti.size(), 0);

		// VERIFICO CHE IL RECUPERO DELLE MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> richiediMissione = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("richiediMissione").list();
		assertEquals(richiediMissione.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

}
