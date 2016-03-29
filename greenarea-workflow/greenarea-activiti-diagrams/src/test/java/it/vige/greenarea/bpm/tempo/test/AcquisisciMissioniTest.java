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
package it.vige.greenarea.bpm.tempo.test;

import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.tempo.acquisiscimissioni.EmptyAggiornamentoDatiMissioni;
import it.vige.greenarea.bpm.tempo.acquisiscimissioni.EmptyRecuperaDatiMissioni;
import it.vige.greenarea.bpm.tempo.acquisiscimissioni.EmptyVerificaDatiMissioni;
import it.vige.greenarea.bpm.tempo.service.acquisiscimissioni.AggiornamentoDatiMissioniConSegnalazioneErrore;
import it.vige.greenarea.bpm.tempo.service.acquisiscimissioni.RecuperaDatiMissioniConSollecitoDatiMissioneNonTrovati;
import it.vige.greenarea.bpm.tempo.service.acquisiscimissioni.VerificaDatiMissioniConSegnalazioneDatiMissioneNonCorretti;

import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.test.ResourceActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.test.Deployment;
import org.subethamail.smtp.server.SMTPServer;

public class AcquisisciMissioniTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "amministratore";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public AcquisisciMissioniTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "bpm/tempo/acquisisci_missioni.bpmn20.xml" })
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

		// INIZIO IL PROCESSO
		ProcessDefinition acquisisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel acquisisciMissioniModel = repositoryService
				.getBpmnModel(acquisisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAcquisisciMissioni = (StartEvent) acquisisciMissioniModel
				.getFlowElement("timerAcquisisciMissioni");
		timerAcquisisciMissioni.getEventDefinitions().clear();
		ServiceTask verificaDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("verificaDatiMissioni");
		verificaDatiMissioni.setImplementation(EmptyVerificaDatiMissioni.class
				.getName());
		ServiceTask recuperaDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("recuperaDatiMissioni");
		recuperaDatiMissioni.setImplementation(EmptyRecuperaDatiMissioni.class
				.getName());
		ServiceTask aggiornamentoDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("aggiornamentoDatiMissioni");
		aggiornamentoDatiMissioni
				.setImplementation(EmptyAggiornamentoDatiMissioni.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", acquisisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("acquisisciMissioni");

		// VERIFICO CHE LA EMAIL DI SOLLECITO MISSIONI MANCANTI NON CORRETTE NON
		// E' STATA
		// MANDATA
		List<HistoricActivityInstance> sollecitoDatiMissioniNonTrovatiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoDatiMissioniNonTrovatiAOperatoreLogistico")
				.list();
		assertEquals(sollecitoDatiMissioniNonTrovatiAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE LA EMAIL DI SEGNALAZIONE DATI MISSIONI NON CORRETTE NON
		// E' STATA
		// MANDATA
		List<HistoricActivityInstance> segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE AGGIORNAMENTO NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreAggiornamentoAAmministratore")
				.list();
		assertEquals(segnalazioneErroreAggiornamentoAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoAOperatoreLogistico")
				.list();
		assertEquals(segnalazioneErroreAggiornamentoAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE I RECUPERI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperaDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperaDatiMissioni").list();
		assertEquals(recuperaDatiMissioniHistory.size(), 2);

		// VERIFICO CHE LE VERIFICHE DELLE MISSIONI SONO STATE EFFETTUATE
		List<HistoricActivityInstance> verificaDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiMissioni").list();
		assertEquals(verificaDatiMissioniHistory.size(), 2);

		// VERIFICO CHE GLI AGGIORNAMENTI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDatiMissioni").list();
		assertEquals(aggiornamentoDatiMissioniHistory.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/acquisisci_missioni.bpmn20.xml" })
	public void testSollecitoDatiMissioneNonTrovati() {
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

		// AGGIUNGO UN ERRORE DI DATI MISSIONE NON TROVATI, ELIMINO I TIMER PER
		// ESEGUIRE I TEST VELOCEMENTE E INIZIO IL PROCESSO
		ProcessDefinition acquisisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel acquisisciMissioniModel = repositoryService
				.getBpmnModel(acquisisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAcquisisciMissioni = (StartEvent) acquisisciMissioniModel
				.getFlowElement("timerAcquisisciMissioni");
		timerAcquisisciMissioni.getEventDefinitions().clear();
		SubProcess subprocessAcquisisciMissioni = acquisisciMissioniModel
				.getProcesses().get(0).findFlowElementsOfType(SubProcess.class)
				.get(0);
		subprocessAcquisisciMissioni
				.removeFlowElement("timerAttesaDatiNonTrovati");
		subprocessAcquisisciMissioni
				.removeFlowElement("flowendSollecitoDatiMissioniNonTrovatiAOperatoreLogistico");
		SequenceFlow flow2 = (SequenceFlow) subprocessAcquisisciMissioni
				.getFlowElement("flow2");
		ServiceTask recuperaDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("recuperaDatiMissioni");
		recuperaDatiMissioni
				.setImplementation(RecuperaDatiMissioniConSollecitoDatiMissioneNonTrovati.class
						.getName());
		ServiceTask verificaDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("verificaDatiMissioni");
		verificaDatiMissioni.setImplementation(EmptyVerificaDatiMissioni.class
				.getName());
		ServiceTask aggiornamentoDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("aggiornamentoDatiMissioni");
		aggiornamentoDatiMissioni
				.setImplementation(EmptyAggiornamentoDatiMissioni.class
						.getName());
		flow2.setTargetRef("recuperaDatiMissioni");
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", acquisisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("acquisisciMissioni");

		// VERIFICO CHE LA EMAIL DI SOLLECITO MISSIONI MANCANTI E' STATA MANDATA
		List<HistoricActivityInstance> sollecitoDatiMissioniNonTrovatiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoDatiMissioniNonTrovatiAOperatoreLogistico")
				.list();
		assertEquals(sollecitoDatiMissioniNonTrovatiAOperatoreLogistico.size(),
				1);

		// VERIFICO CHE LA EMAIL DI SEGNALAZIONE DATI MISSIONI NON CORRETTE NON
		// E' STATA
		// MANDATA
		List<HistoricActivityInstance> segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE AGGIORNAMENTO NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreAggiornamentoAAmministratore")
				.list();
		assertEquals(segnalazioneErroreAggiornamentoAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoAOperatoreLogistico")
				.list();
		assertEquals(segnalazioneErroreAggiornamentoAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE I RECUPERI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperaDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperaDatiMissioni").list();
		assertEquals(recuperaDatiMissioniHistory.size(), 3);

		// VERIFICO CHE LE VERIFICHE DELLE MISSIONI SONO STATE EFFETTUATE
		List<HistoricActivityInstance> verificaDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiMissioni").list();
		assertEquals(verificaDatiMissioniHistory.size(), 2);

		// VERIFICO CHE GLI AGGIORNAMENTI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDatiMissioni").list();
		assertEquals(aggiornamentoDatiMissioniHistory.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/acquisisci_missioni.bpmn20.xml" })
	public void testSegnalazioneDatiMissioneNonCorretti() {
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

		// AGGIUNGO UN ERRORE DI DATI MISSIONE NON CORRETTI, ELIMINO I TIMER PER
		// ESEGUIRE I TEST VELOCEMENTE E INIZIO IL PROCESSO
		ProcessDefinition acquisisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel acquisisciMissioniModel = repositoryService
				.getBpmnModel(acquisisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAcquisisciMissioni = (StartEvent) acquisisciMissioniModel
				.getFlowElement("timerAcquisisciMissioni");
		timerAcquisisciMissioni.getEventDefinitions().clear();
		SubProcess subprocessAcquisisciMissioni = acquisisciMissioniModel
				.getProcesses().get(0).findFlowElementsOfType(SubProcess.class)
				.get(0);
		subprocessAcquisisciMissioni
				.removeFlowElement("timerAttesaDatiNonCorretti");
		subprocessAcquisisciMissioni
				.removeFlowElement("flowendAttesaNuoviDatiMissioni");
		SequenceFlow flowAttesaNuoviDatiMissioni = (SequenceFlow) subprocessAcquisisciMissioni
				.getFlowElement("flowAttesaNuoviDatiMissioni");
		ServiceTask verificaDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("verificaDatiMissioni");
		verificaDatiMissioni
				.setImplementation(VerificaDatiMissioniConSegnalazioneDatiMissioneNonCorretti.class
						.getName());
		ServiceTask recuperaDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("recuperaDatiMissioni");
		recuperaDatiMissioni.setImplementation(EmptyRecuperaDatiMissioni.class
				.getName());
		ServiceTask aggiornamentoDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("aggiornamentoDatiMissioni");
		aggiornamentoDatiMissioni
				.setImplementation(EmptyAggiornamentoDatiMissioni.class
						.getName());
		flowAttesaNuoviDatiMissioni.setTargetRef("verificaDatiMissioni");
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", acquisisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("acquisisciMissioni");

		// VERIFICO CHE LA EMAIL DI SOLLECITO MISSIONI MANCANTI NON E' STATA
		// MANDATA
		List<HistoricActivityInstance> sollecitoDatiMissioniNonTrovatiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoDatiMissioniNonTrovatiAOperatoreLogistico")
				.list();
		assertEquals(sollecitoDatiMissioniNonTrovatiAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE LA EMAIL DI SEGNALAZIONE DATI MISSIONI NON CORRETTE E'
		// STATA MANDATA
		List<HistoricActivityInstance> segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico.size(),
				1);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE AGGIORNAMENTO NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreAggiornamentoAAmministratore")
				.list();
		assertEquals(segnalazioneErroreAggiornamentoAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoAOperatoreLogistico")
				.list();
		assertEquals(segnalazioneErroreAggiornamentoAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE I RECUPERI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperaDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperaDatiMissioni").list();
		assertEquals(recuperaDatiMissioniHistory.size(), 2);

		// VERIFICO CHE LE VERIFICHE DELLE MISSIONI SONO STATE EFFETTUATE
		List<HistoricActivityInstance> verificaDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiMissioni").list();
		assertEquals(verificaDatiMissioniHistory.size(), 3);

		// VERIFICO CHE GLI AGGIORNAMENTI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDatiMissioni").list();
		assertEquals(aggiornamentoDatiMissioniHistory.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/acquisisci_missioni.bpmn20.xml" })
	public void testSegnalazioneErroreAggiornamento() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition acquisisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel acquisisciMissioniModel = repositoryService
				.getBpmnModel(acquisisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAcquisisciMissioni = (StartEvent) acquisisciMissioniModel
				.getFlowElement("timerAcquisisciMissioni");
		timerAcquisisciMissioni.getEventDefinitions().clear();
		ServiceTask aggiornamentoDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("aggiornamentoDatiMissioni");
		aggiornamentoDatiMissioni
				.setImplementation(AggiornamentoDatiMissioniConSegnalazioneErrore.class
						.getName());
		ServiceTask verificaDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("verificaDatiMissioni");
		verificaDatiMissioni.setImplementation(EmptyVerificaDatiMissioni.class
				.getName());
		ServiceTask recuperaDatiMissioni = (ServiceTask) acquisisciMissioniModel
				.getFlowElement("recuperaDatiMissioni");
		recuperaDatiMissioni.setImplementation(EmptyRecuperaDatiMissioni.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", acquisisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("acquisisciMissioni");

		// VERIFICO CHE LA EMAIL DI SOLLECITO MISSIONI MANCANTI NON E' STATA
		// MANDATA
		List<HistoricActivityInstance> sollecitoDatiMissioniNonTrovatiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoDatiMissioniNonTrovatiAOperatoreLogistico")
				.list();
		assertEquals(sollecitoDatiMissioniNonTrovatiAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE LA EMAIL DI SEGNALAZIONE DATI MISSIONI NON CORRETTE NON
		// E' STATA
		// MANDATA
		List<HistoricActivityInstance> segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneDatiMissioneNonCorrettiAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE AGGIORNAMENTO SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreAggiornamentoAAmministratore")
				.list();
		assertEquals(segnalazioneErroreAggiornamentoAAmministratore.size(), 1);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoAOperatoreLogistico")
				.list();
		assertEquals(segnalazioneErroreAggiornamentoAOperatoreLogistico.size(),
				1);

		// VERIFICO CHE I RECUPERI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperaDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperaDatiMissioni").list();
		assertEquals(recuperaDatiMissioniHistory.size(), 2);

		// VERIFICO CHE LE VERIFICHE DELLE MISSIONI SONO STATE EFFETTUATE
		List<HistoricActivityInstance> verificaDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiMissioni").list();
		assertEquals(verificaDatiMissioniHistory.size(), 2);

		// VERIFICO CHE GLI AGGIORNAMENTI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoDatiMissioniHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDatiMissioni").list();
		assertEquals(aggiornamentoDatiMissioniHistory.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

}
