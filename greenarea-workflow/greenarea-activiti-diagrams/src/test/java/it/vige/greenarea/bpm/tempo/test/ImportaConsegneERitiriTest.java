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

import static org.activiti.bpmn.model.ImplementationType.IMPLEMENTATION_TYPE_CLASS;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.tempo.importaconsegneeritiri.EmptyAggiornamentoConsegneERitiri;
import it.vige.greenarea.bpm.tempo.importaconsegneeritiri.EmptyVerificaDatiConsegneERitiri;
import it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri.AggiornamentoConsegneERitiriConSegnalazioneErroreGrave;
import it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri.AggiornamentoConsegneERitiriConSegnalazioneErroreLieve;
import it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri.VerificaDatiConsegneERitiriConDatiMancantiConReinoltro;
import it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri.VerificaDatiConsegneERitiriConDatiMancantiSenzaReinoltro;
import it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri.VerificaDatiConsegneERitiriConDatiMancantiSenzaReinoltroSuPrimoOP;
import it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri.VerificaDatiConsegneERitiriConDatiMancantiSenzaReinoltroSuSecondoOP;
import it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri.VerificaDatiConsegneERitiriConDatiNonCorrettiConReinoltro;
import it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri.VerificaDatiConsegneERitiriConDatiNonCorrettiSenzaReinoltro;
import it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri.VerificaDatiConsegneERitiriConDatiNonCorrettiSenzaReinoltroSuPrimoOP;
import it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri.VerificaDatiConsegneERitiriConDatiNonCorrettiSenzaReinoltroSuSecondoOP;
import it.vige.greenarea.bpm.tempo.service.importaconsegneeritiri.VerificaDatiConsegneERitiriConErroreGrave;

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

public class ImportaConsegneERitiriTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "amministratore";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public ImportaConsegneERitiriTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
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
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(EmptyVerificaDatiConsegneERitiri.class
						.getName());
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(EmptyAggiornamentoConsegneERitiri.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 2);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
	public void testVerificaDatiConsegneERitiriConErroreGrave() {
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

		// POPOLO I RITIRI, ELIMINO I TIMER PER ESEGUIRE I TEST VELOCEMENTE E
		// INIZIO IL PROCESSO
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		SubProcess subprocessImportaConsegneERitiri = importaConsegneERitiriModel
				.getProcesses().get(0).findFlowElementsOfType(SubProcess.class)
				.get(0);
		subprocessImportaConsegneERitiri
				.removeFlowElement("timerAttesaDatiConsegneERitiri");
		subprocessImportaConsegneERitiri.removeFlowElement("flow15");
		SequenceFlow flow32 = (SequenceFlow) subprocessImportaConsegneERitiri
				.getFlowElement("flow32");
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(VerificaDatiConsegneERitiriConErroreGrave.class
						.getName());
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(EmptyAggiornamentoConsegneERitiri.class
						.getName());
		flow32.setTargetRef("verificaDatiConsegneERitiri");
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI
		// NON SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI NON SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 0);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
	public void testSegnalazioneErroreAggiornamentoConsegneERitiriConErroreGrave() {
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

		// POPOLO I RITIRI E INIZIO IL PROCESSO
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(AggiornamentoConsegneERitiriConSegnalazioneErroreGrave.class
						.getName());
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(EmptyVerificaDatiConsegneERitiri.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				1);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				1);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 1);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
	public void testSollecitoPerDatiConsegneERitiriNonCorrettiConReinoltro() {
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

		// POPOLO I RITIRI, ELIMINO I TIMER PER ESEGUIRE I TEST VELOCEMENTE E
		// INIZIO IL PROCESSO
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		SubProcess subprocessImportaConsegneERitiri = importaConsegneERitiriModel
				.getProcesses().get(0).findFlowElementsOfType(SubProcess.class)
				.get(0);
		subprocessImportaConsegneERitiri
				.removeFlowElement("timerAttesaDatiConsegneERitiri");
		subprocessImportaConsegneERitiri.removeFlowElement("flow15");
		SequenceFlow flow32 = (SequenceFlow) subprocessImportaConsegneERitiri
				.getFlowElement("flow32");
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(VerificaDatiConsegneERitiriConDatiNonCorrettiConReinoltro.class
						.getName());
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(EmptyAggiornamentoConsegneERitiri.class
						.getName());
		flow32.setTargetRef("verificaDatiConsegneERitiri");
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				1);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 2);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 3);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
	public void testSollecitoPerDatiConsegneERitiriNonCorrettiSenzaReinoltro() {
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

		// POPOLO I RITIRI, ELIMINO I TIMER PER ESEGUIRE I TEST VELOCEMENTE E
		// INIZIO IL PROCESSO
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		SubProcess subprocessImportaConsegneERitiri = importaConsegneERitiriModel
				.getProcesses().get(0).findFlowElementsOfType(SubProcess.class)
				.get(0);
		subprocessImportaConsegneERitiri
				.removeFlowElement("timerAttesaDatiConsegneERitiri");
		subprocessImportaConsegneERitiri.removeFlowElement("flow15");
		SequenceFlow flow32 = (SequenceFlow) subprocessImportaConsegneERitiri
				.getFlowElement("flow32");
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(VerificaDatiConsegneERitiriConDatiNonCorrettiSenzaReinoltro.class
						.getName());
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(EmptyAggiornamentoConsegneERitiri.class
						.getName());
		flow32.setTargetRef("verificaDatiConsegneERitiri");
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				2);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 0);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 4);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
	public void testSollecitoPerDatiConsegneERitiriNonCorrettiSenzaReinoltroSuPrimoOP() {
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

		// POPOLO I RITIRI, ELIMINO I TIMER PER ESEGUIRE I TEST VELOCEMENTE E
		// INIZIO IL PROCESSO
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		SubProcess subprocessImportaConsegneERitiri = importaConsegneERitiriModel
				.getProcesses().get(0).findFlowElementsOfType(SubProcess.class)
				.get(0);
		subprocessImportaConsegneERitiri
				.removeFlowElement("timerAttesaDatiConsegneERitiri");
		subprocessImportaConsegneERitiri.removeFlowElement("flow15");
		SequenceFlow flow32 = (SequenceFlow) subprocessImportaConsegneERitiri
				.getFlowElement("flow32");
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(VerificaDatiConsegneERitiriConDatiNonCorrettiSenzaReinoltroSuPrimoOP.class
						.getName());
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(EmptyAggiornamentoConsegneERitiri.class
						.getName());
		flow32.setTargetRef("verificaDatiConsegneERitiri");
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				1);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI
		// NON SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 1);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 3);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
	public void testSollecitoPerDatiConsegneERitiriNonCorrettiSenzaReinoltroSuSecondoOP() {
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

		// POPOLO I RITIRI, ELIMINO I TIMER PER ESEGUIRE I TEST VELOCEMENTE E
		// INIZIO IL PROCESSO
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		SubProcess subprocessImportaConsegneERitiri = importaConsegneERitiriModel
				.getProcesses().get(0).findFlowElementsOfType(SubProcess.class)
				.get(0);
		subprocessImportaConsegneERitiri
				.removeFlowElement("timerAttesaDatiConsegneERitiri");
		subprocessImportaConsegneERitiri.removeFlowElement("flow15");
		SequenceFlow flow32 = (SequenceFlow) subprocessImportaConsegneERitiri
				.getFlowElement("flow32");
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(VerificaDatiConsegneERitiriConDatiNonCorrettiSenzaReinoltroSuSecondoOP.class
						.getName());
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(EmptyAggiornamentoConsegneERitiri.class
						.getName());
		flow32.setTargetRef("verificaDatiConsegneERitiri");
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				1);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 1);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 3);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
	public void testSollecitoPerDatiConsegneERitiriMancantiConReinoltro() {
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

		// POPOLO I RITIRI, ELIMINO I TIMER PER ESEGUIRE I TEST VELOCEMENTE E
		// INIZIO IL PROCESSO
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		SubProcess subprocessImportaConsegneERitiri = importaConsegneERitiriModel
				.getProcesses().get(0).findFlowElementsOfType(SubProcess.class)
				.get(0);
		subprocessImportaConsegneERitiri
				.removeFlowElement("timerAttesaDatiConsegneERitiri");
		subprocessImportaConsegneERitiri.removeFlowElement("flow15");
		SequenceFlow flow32 = (SequenceFlow) subprocessImportaConsegneERitiri
				.getFlowElement("flow32");
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(VerificaDatiConsegneERitiriConDatiMancantiConReinoltro.class
						.getName());
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(EmptyAggiornamentoConsegneERitiri.class
						.getName());
		flow32.setTargetRef("verificaDatiConsegneERitiri");
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				1);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 2);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 3);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
	public void testSollecitoPerDatiConsegneERitiriMancantiSenzaReinoltro() {
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

		// POPOLO I RITIRI, ELIMINO I TIMER PER ESEGUIRE I TEST VELOCEMENTE E
		// INIZIO IL PROCESSO
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		SubProcess subprocessImportaConsegneERitiri = importaConsegneERitiriModel
				.getProcesses().get(0).findFlowElementsOfType(SubProcess.class)
				.get(0);
		subprocessImportaConsegneERitiri
				.removeFlowElement("timerAttesaDatiConsegneERitiri");
		subprocessImportaConsegneERitiri.removeFlowElement("flow15");
		SequenceFlow flow32 = (SequenceFlow) subprocessImportaConsegneERitiri
				.getFlowElement("flow32");
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(VerificaDatiConsegneERitiriConDatiMancantiSenzaReinoltro.class
						.getName());
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(EmptyAggiornamentoConsegneERitiri.class
						.getName());
		flow32.setTargetRef("verificaDatiConsegneERitiri");
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				2);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 0);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 4);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
	public void testSollecitoPerDatiConsegneERitiriMancantiSenzaReinoltroSuPrimoOP() {
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

		// POPOLO I RITIRI, ELIMINO I TIMER PER ESEGUIRE I TEST VELOCEMENTE E
		// INIZIO IL PROCESSO
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		SubProcess subprocessImportaConsegneERitiri = importaConsegneERitiriModel
				.getProcesses().get(0).findFlowElementsOfType(SubProcess.class)
				.get(0);
		subprocessImportaConsegneERitiri
				.removeFlowElement("timerAttesaDatiConsegneERitiri");
		subprocessImportaConsegneERitiri.removeFlowElement("flow15");
		SequenceFlow flow32 = (SequenceFlow) subprocessImportaConsegneERitiri
				.getFlowElement("flow32");
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(VerificaDatiConsegneERitiriConDatiMancantiSenzaReinoltroSuPrimoOP.class
						.getName());
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(EmptyAggiornamentoConsegneERitiri.class
						.getName());
		flow32.setTargetRef("verificaDatiConsegneERitiri");
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				1);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 1);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 3);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
	public void testSollecitoPerDatiConsegneERitiriMancantiSenzaReinoltroSuSecondoOP() {
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

		// POPOLO I RITIRI, ELIMINO I TIMER PER ESEGUIRE I TEST VELOCEMENTE E
		// INIZIO IL PROCESSO
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		SubProcess subprocessImportaConsegneERitiri = importaConsegneERitiriModel
				.getProcesses().get(0).findFlowElementsOfType(SubProcess.class)
				.get(0);
		subprocessImportaConsegneERitiri
				.removeFlowElement("timerAttesaDatiConsegneERitiri");
		subprocessImportaConsegneERitiri.removeFlowElement("flow15");
		SequenceFlow flow32 = (SequenceFlow) subprocessImportaConsegneERitiri
				.getFlowElement("flow32");
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(VerificaDatiConsegneERitiriConDatiMancantiSenzaReinoltroSuSecondoOP.class
						.getName());
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(EmptyAggiornamentoConsegneERitiri.class
						.getName());
		flow32.setTargetRef("verificaDatiConsegneERitiri");
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				1);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 1);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 3);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/importa_consegne_e_ritiri.bpmn20.xml" })
	public void testSegnalazioneErroreAggiornamentoConsegneERitiriConErroreLieve() {
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

		// POPOLO I RITIRI E INIZIO IL PROCESSO
		ProcessDefinition importaConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaConsegneERitiriModel = repositoryService
				.getBpmnModel(importaConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaConsegneERitiri = (StartEvent) importaConsegneERitiriModel
				.getFlowElement("timerImportaConsegneERitiri");
		timerImportaConsegneERitiri.getEventDefinitions().clear();
		ServiceTask aggiornamentoConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("aggiornamentoConsegneERitiri");
		aggiornamentoConsegneERitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		aggiornamentoConsegneERitiri
				.setImplementation(AggiornamentoConsegneERitiriConSegnalazioneErroreLieve.class
						.getName());
		ServiceTask verificaDatiConsegneERitiri = (ServiceTask) importaConsegneERitiriModel
				.getFlowElement("verificaDatiConsegneERitiri");
		verificaDatiConsegneERitiri
				.setImplementation(EmptyVerificaDatiConsegneERitiri.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaConsegneERitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaConsegneERitiri");

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI NON CORRETTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriNonCorretti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CONSEGNE E RITIRI MANCANTI NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiConsegneERitiriMancanti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAAmministratore
						.size(),
				1);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoConsegneERitiriAOperatoreLogistico
						.size(),
				1);

		// VERIFICO CHE GLI AGGIORNAMENTI CONSEGNE E RITIRI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoConsegneERitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoConsegneERitiri").list();
		assertEquals(aggiornamentoConsegneERitiriHistory.size(), 2);

		// VERIFICO CHE LE VERIFICHE CONSEGNE E RITIRI SONO STATE
		// EFFETTUATE
		List<HistoricActivityInstance> verificaConsegneERitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiConsegneERitiri").list();
		assertEquals(verificaConsegneERitiri.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

}
