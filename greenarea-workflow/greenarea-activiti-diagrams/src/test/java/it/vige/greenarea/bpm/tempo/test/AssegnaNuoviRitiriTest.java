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
import it.vige.greenarea.bpm.tempo.assegnanuoviritiri.EmptyAggiornamentoMissioneCorrente;
import it.vige.greenarea.bpm.tempo.assegnanuoviritiri.EmptyAssegnazioneRitiroAMissioneCorrente;
import it.vige.greenarea.bpm.tempo.assegnanuoviritiri.EmptyRecuperoDatiMissioniCorrentiPerOperatoreLogistico;
import it.vige.greenarea.bpm.tempo.service.assegnanuoviritiri.AggiornamentoMissioneCorrenteConErroreGrave;
import it.vige.greenarea.bpm.tempo.service.assegnanuoviritiri.AggiornamentoMissioneCorrenteConErroreLieve;
import it.vige.greenarea.bpm.tempo.service.assegnanuoviritiri.RecuperoDatiMissioniCorrentiConErroreGrave;
import it.vige.greenarea.bpm.tempo.service.assegnanuoviritiri.RecuperoDatiMissioniCorrentiConErroreLieve;
import it.vige.greenarea.bpm.tempo.service.assegnanuoviritiri.RecuperoNuoviRitiriConErroreGrave;
import it.vige.greenarea.bpm.tempo.service.assegnanuoviritiri.RecuperoNuoviRitiriConErroreLieve;
import it.vige.greenarea.bpm.tempo.service.assegnanuoviritiri.RecuperoNuoviRitiriPopolati;

import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.test.ResourceActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.test.Deployment;
import org.subethamail.smtp.server.SMTPServer;

public class AssegnaNuoviRitiriTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "amministratore";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public AssegnaNuoviRitiriTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "bpm/tempo/assegna_nuovi_ritiri.bpmn20.xml" })
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

		// POPOLO I RITIRI E INIZIO IL PROCESSO
		ProcessDefinition assegnaNuoviRitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel assegnaNuoviRitiriModel = repositoryService
				.getBpmnModel(assegnaNuoviRitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAssegnaNuoviRitiri = (StartEvent) assegnaNuoviRitiriModel
				.getFlowElement("timerAssegnaNuoviRitiri");
		timerAssegnaNuoviRitiri.getEventDefinitions().clear();
		ServiceTask recuperoNuoviRitiriService = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoNuoviRitiri");
		recuperoNuoviRitiriService
				.setImplementation(RecuperoNuoviRitiriPopolati.class.getName());
		ServiceTask recuperoDatiMissioniCorrentiPerOperatoreLogistico = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoDatiMissioniCorrentiPerOperatoreLogistico");
		recuperoDatiMissioniCorrentiPerOperatoreLogistico
				.setImplementation(EmptyRecuperoDatiMissioniCorrentiPerOperatoreLogistico.class
						.getName());
		ServiceTask assegnazioneRitiroAMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("assegnazioneRitiroAMissioneCorrente");
		assegnazioneRitiroAMissioneCorrente
				.setImplementation(EmptyAssegnazioneRitiroAMissioneCorrente.class
						.getName());
		ServiceTask aggiornamentoMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("aggiornamentoMissioneCorrente");
		aggiornamentoMissioneCorrente
				.setImplementation(EmptyAggiornamentoMissioneCorrente.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", assegnaNuoviRitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("assegnaNuoviRitiri");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE RECUPERO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoNuoviRitiriAAmministratore")
				.list();
		assertEquals(notificaErroreRecuperoNuoviRitiriAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoNuoviRitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreRecuperoNuoviRitiriAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE MISSIONI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAOperatoreLogistico")
				.list();
		assertEquals(segnalazioneErroreMissioniAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE AGGIORNAMENTO NON SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE I RECUPERI DEI NUOVI RITIRI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoNuoviRitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoNuoviRitiri").list();
		assertEquals(recuperoNuoviRitiri.size(), 2);

		// VERIFICO CHE I RECUPERI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrentiPerOperatoreLogistico")
				.list();
		assertEquals(
				recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory.size(),
				2);

		// VERIFICO CHE LE ASSEGNAZIONI DEI NUOVI RITIRI ALLE MISSIONI SONO
		// STATE EFFETTUATE
		List<HistoricActivityInstance> assegnazioneRitiroAMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("assegnazioneRitiroAMissioneCorrente").list();
		assertEquals(assegnazioneRitiroAMissioneCorrenteHistory.size(), 4);

		// VERIFICO CHE GLI AGGIORNAMENTI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoMissioneCorrente").list();
		assertEquals(aggiornamentoMissioneCorrenteHistory.size(), 4);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE MISSIONI AGGIORNATE SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneMissioniAggiornate = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneMissioniAggiornate").list();
		assertEquals(segnalazioneMissioniAggiornate.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/assegna_nuovi_ritiri.bpmn20.xml" })
	public void testNotificaErroreLieveRecuperoNuoviRitiri() {
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

		// AGGIUNGO UN ERRORE DI RECUPERO RITIRI E INIZIO IL PROCESSO
		ProcessDefinition assegnaNuoviRitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel assegnaNuoviRitiriModel = repositoryService
				.getBpmnModel(assegnaNuoviRitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAssegnaNuoviRitiri = (StartEvent) assegnaNuoviRitiriModel
				.getFlowElement("timerAssegnaNuoviRitiri");
		timerAssegnaNuoviRitiri.getEventDefinitions().clear();
		ServiceTask recuperoNuoviRitiriService = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoNuoviRitiri");
		recuperoNuoviRitiriService
				.setImplementation(RecuperoNuoviRitiriConErroreLieve.class
						.getName());
		ServiceTask recuperoDatiMissioniCorrentiPerOperatoreLogistico = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoDatiMissioniCorrentiPerOperatoreLogistico");
		recuperoDatiMissioniCorrentiPerOperatoreLogistico
				.setImplementation(EmptyRecuperoDatiMissioniCorrentiPerOperatoreLogistico.class
						.getName());
		ServiceTask assegnazioneRitiroAMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("assegnazioneRitiroAMissioneCorrente");
		assegnazioneRitiroAMissioneCorrente
				.setImplementation(EmptyAssegnazioneRitiroAMissioneCorrente.class
						.getName());
		ServiceTask aggiornamentoMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("aggiornamentoMissioneCorrente");
		aggiornamentoMissioneCorrente
				.setImplementation(EmptyAggiornamentoMissioneCorrente.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", assegnaNuoviRitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("assegnaNuoviRitiri");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE RECUPERO SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoNuoviRitiriAAmministratore")
				.list();
		assertEquals(notificaErroreRecuperoNuoviRitiriAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoNuoviRitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreRecuperoNuoviRitiriAOperatoreLogistico.size(), 1);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE MISSIONI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE AGGIORNAMENTO NON SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE I RECUPERI DEI NUOVI RITIRI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoNuoviRitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoNuoviRitiri").list();
		assertEquals(recuperoNuoviRitiri.size(), 2);

		// VERIFICO CHE I RECUPERI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrentiPerOperatoreLogistico")
				.list();
		assertEquals(
				recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory.size(),
				1);

		// VERIFICO CHE LE ASSEGNAZIONI DEI NUOVI RITIRI ALLE MISSIONI SONO
		// STATE EFFETTUATE
		List<HistoricActivityInstance> assegnazioneRitiroAMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("assegnazioneRitiroAMissioneCorrente").list();
		assertEquals(assegnazioneRitiroAMissioneCorrenteHistory.size(), 2);

		// VERIFICO CHE GLI AGGIORNAMENTI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoMissioneCorrente").list();
		assertEquals(aggiornamentoMissioneCorrenteHistory.size(), 2);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE MISSIONI AGGIORNATE SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneMissioniAggiornate = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneMissioniAggiornate").list();
		assertEquals(segnalazioneMissioniAggiornate.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/assegna_nuovi_ritiri.bpmn20.xml" })
	public void testNotificaErroreGraveRecuperoNuoviRitiri() {
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

		// AGGIUNGO UN ERRORE DI RECUPERO RITIRI E INIZIO IL PROCESSO
		ProcessDefinition assegnaNuoviRitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel assegnaNuoviRitiriModel = repositoryService
				.getBpmnModel(assegnaNuoviRitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAssegnaNuoviRitiri = (StartEvent) assegnaNuoviRitiriModel
				.getFlowElement("timerAssegnaNuoviRitiri");
		timerAssegnaNuoviRitiri.getEventDefinitions().clear();
		ServiceTask recuperoNuoviRitiriService = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoNuoviRitiri");
		recuperoNuoviRitiriService
				.setImplementation(RecuperoNuoviRitiriConErroreGrave.class
						.getName());
		ServiceTask recuperoDatiMissioniCorrentiPerOperatoreLogistico = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoDatiMissioniCorrentiPerOperatoreLogistico");
		recuperoDatiMissioniCorrentiPerOperatoreLogistico
				.setImplementation(EmptyRecuperoDatiMissioniCorrentiPerOperatoreLogistico.class
						.getName());
		ServiceTask assegnazioneRitiroAMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("assegnazioneRitiroAMissioneCorrente");
		assegnazioneRitiroAMissioneCorrente
				.setImplementation(EmptyAssegnazioneRitiroAMissioneCorrente.class
						.getName());
		ServiceTask aggiornamentoMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("aggiornamentoMissioneCorrente");
		aggiornamentoMissioneCorrente
				.setImplementation(EmptyAggiornamentoMissioneCorrente.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", assegnaNuoviRitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("assegnaNuoviRitiri");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE RECUPERO SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoNuoviRitiriAAmministratore")
				.list();
		assertEquals(notificaErroreRecuperoNuoviRitiriAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoNuoviRitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreRecuperoNuoviRitiriAOperatoreLogistico.size(), 1);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE MISSIONI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE AGGIORNAMENTO NON SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE I RECUPERI DEI NUOVI RITIRI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoNuoviRitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoNuoviRitiri").list();
		assertEquals(recuperoNuoviRitiri.size(), 1);

		// VERIFICO CHE I RECUPERI DELLE MISSIONI NON SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrentiPerOperatoreLogistico")
				.list();
		assertEquals(
				recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory.size(),
				0);

		// VERIFICO CHE LE ASSEGNAZIONI DEI NUOVI RITIRI ALLE MISSIONI NON SONO
		// STATE EFFETTUATE
		List<HistoricActivityInstance> assegnazioneRitiroAMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("assegnazioneRitiroAMissioneCorrente").list();
		assertEquals(assegnazioneRitiroAMissioneCorrenteHistory.size(), 0);

		// VERIFICO CHE GLI AGGIORNAMENTI DELLE MISSIONI NON SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoMissioneCorrente").list();
		assertEquals(aggiornamentoMissioneCorrenteHistory.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE MISSIONI AGGIORNATE NON SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneMissioniAggiornate = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneMissioniAggiornate").list();
		assertEquals(segnalazioneMissioniAggiornate.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/assegna_nuovi_ritiri.bpmn20.xml" })
	public void testSegnalazioneErroreLieveMissioni() {
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

		// POPOLO I RITIRI, AGGIUNGO UN ERRORE LIEVE ALLE MISSIONI E INIZIO IL
		// PROCESSO
		ProcessDefinition assegnaNuoviRitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel assegnaNuoviRitiriModel = repositoryService
				.getBpmnModel(assegnaNuoviRitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAssegnaNuoviRitiri = (StartEvent) assegnaNuoviRitiriModel
				.getFlowElement("timerAssegnaNuoviRitiri");
		timerAssegnaNuoviRitiri.getEventDefinitions().clear();
		ServiceTask recuperoNuoviRitiriService = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoNuoviRitiri");
		recuperoNuoviRitiriService
				.setImplementation(RecuperoNuoviRitiriPopolati.class.getName());
		ServiceTask recuperoDatiMissioniCorrentiPerOperatoreLogistico = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoDatiMissioniCorrentiPerOperatoreLogistico");
		recuperoDatiMissioniCorrentiPerOperatoreLogistico
				.setImplementation(EmptyRecuperoDatiMissioniCorrentiPerOperatoreLogistico.class
						.getName());
		ServiceTask assegnazioneRitiroAMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("assegnazioneRitiroAMissioneCorrente");
		assegnazioneRitiroAMissioneCorrente
				.setImplementation(EmptyAssegnazioneRitiroAMissioneCorrente.class
						.getName());
		ServiceTask aggiornamentoMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("aggiornamentoMissioneCorrente");
		aggiornamentoMissioneCorrente
				.setImplementation(EmptyAggiornamentoMissioneCorrente.class
						.getName());
		ServiceTask recuperoMissioniService = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoDatiMissioniCorrentiPerOperatoreLogistico");
		recuperoMissioniService
				.setImplementation(RecuperoDatiMissioniCorrentiConErroreLieve.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", assegnaNuoviRitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("assegnaNuoviRitiri");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE RECUPERO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoNuoviRitiriAAmministratore")
				.list();
		assertEquals(notificaErroreRecuperoNuoviRitiriAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoNuoviRitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreRecuperoNuoviRitiriAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE MISSIONI SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAAmministratore.size(), 1);
		List<HistoricActivityInstance> segnalazioneErroreMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAOperatoreLogistico.size(), 1);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE AGGIORNAMENTO NON SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE I RECUPERI DEI NUOVI RITIRI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoNuoviRitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoNuoviRitiri").list();
		assertEquals(recuperoNuoviRitiri.size(), 2);

		// VERIFICO CHE I RECUPERI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrentiPerOperatoreLogistico")
				.list();
		assertEquals(
				recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory.size(),
				2);

		// VERIFICO CHE LE ASSEGNAZIONI DEI NUOVI RITIRI ALLE MISSIONI SONO
		// STATE EFFETTUATE
		List<HistoricActivityInstance> assegnazioneRitiroAMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("assegnazioneRitiroAMissioneCorrente").list();
		assertEquals(assegnazioneRitiroAMissioneCorrenteHistory.size(), 2);

		// VERIFICO CHE GLI AGGIORNAMENTI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoMissioneCorrente").list();
		assertEquals(aggiornamentoMissioneCorrenteHistory.size(), 2);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE MISSIONI AGGIORNATE SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneMissioniAggiornate = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneMissioniAggiornate").list();
		assertEquals(segnalazioneMissioniAggiornate.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/assegna_nuovi_ritiri.bpmn20.xml" })
	public void testSegnalazioneErroreGraveMissioni() {
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

		// POPOLO I RITIRI, AGGIUNGO UN ERRORE GRAVE ALLE MISSIONI E INIZIO IL
		// PROCESSO
		ProcessDefinition assegnaNuoviRitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel assegnaNuoviRitiriModel = repositoryService
				.getBpmnModel(assegnaNuoviRitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAssegnaNuoviRitiri = (StartEvent) assegnaNuoviRitiriModel
				.getFlowElement("timerAssegnaNuoviRitiri");
		timerAssegnaNuoviRitiri.getEventDefinitions().clear();
		ServiceTask recuperoNuoviRitiriService = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoNuoviRitiri");
		recuperoNuoviRitiriService
				.setImplementation(RecuperoNuoviRitiriPopolati.class.getName());
		ServiceTask recuperoMissioniService = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoDatiMissioniCorrentiPerOperatoreLogistico");
		recuperoMissioniService
				.setImplementation(RecuperoDatiMissioniCorrentiConErroreGrave.class
						.getName());
		ServiceTask assegnazioneRitiroAMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("assegnazioneRitiroAMissioneCorrente");
		assegnazioneRitiroAMissioneCorrente
				.setImplementation(EmptyAssegnazioneRitiroAMissioneCorrente.class
						.getName());
		ServiceTask aggiornamentoMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("aggiornamentoMissioneCorrente");
		aggiornamentoMissioneCorrente
				.setImplementation(EmptyAggiornamentoMissioneCorrente.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", assegnaNuoviRitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("assegnaNuoviRitiri");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE RECUPERO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoNuoviRitiriAAmministratore")
				.list();
		assertEquals(notificaErroreRecuperoNuoviRitiriAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoNuoviRitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreRecuperoNuoviRitiriAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE MISSIONI SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAAmministratore.size(), 1);
		List<HistoricActivityInstance> segnalazioneErroreMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAOperatoreLogistico.size(), 1);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE AGGIORNAMENTO NON SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE I RECUPERI DEI NUOVI RITIRI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoNuoviRitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoNuoviRitiri").list();
		assertEquals(recuperoNuoviRitiri.size(), 1);

		// VERIFICO CHE I RECUPERI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiPerOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrentiPerOperatoreLogistico")
				.list();
		assertEquals(recuperoDatiMissioniCorrentiPerOperatoreLogistico.size(),
				1);

		// VERIFICO CHE LE ASSEGNAZIONI DEI NUOVI RITIRI ALLE MISSIONI NON SONO
		// STATE EFFETTUATE
		List<HistoricActivityInstance> assegnazioneRitiroAMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("assegnazioneRitiroAMissioneCorrente").list();
		assertEquals(assegnazioneRitiroAMissioneCorrenteHistory.size(), 0);

		// VERIFICO CHE GLI AGGIORNAMENTI DELLE MISSIONI NON SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoMissioneCorrente").list();
		assertEquals(aggiornamentoMissioneCorrenteHistory.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE MISSIONI AGGIORNATE NON SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneMissioniAggiornate = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneMissioniAggiornate").list();
		assertEquals(segnalazioneMissioniAggiornate.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/assegna_nuovi_ritiri.bpmn20.xml" })
	public void testSegnalazioneErroreLieveAggiornamentoCorrente() {
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
		ProcessDefinition assegnaNuoviRitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel assegnaNuoviRitiriModel = repositoryService
				.getBpmnModel(assegnaNuoviRitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAssegnaNuoviRitiri = (StartEvent) assegnaNuoviRitiriModel
				.getFlowElement("timerAssegnaNuoviRitiri");
		timerAssegnaNuoviRitiri.getEventDefinitions().clear();
		ServiceTask recuperoNuoviRitiriService = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoNuoviRitiri");
		recuperoNuoviRitiriService
				.setImplementation(RecuperoNuoviRitiriPopolati.class.getName());
		ServiceTask aggiornamentoMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("aggiornamentoMissioneCorrente");
		aggiornamentoMissioneCorrente
				.setImplementation(AggiornamentoMissioneCorrenteConErroreLieve.class
						.getName());
		ServiceTask recuperoDatiMissioniCorrentiPerOperatoreLogistico = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoDatiMissioniCorrentiPerOperatoreLogistico");
		recuperoDatiMissioniCorrentiPerOperatoreLogistico
				.setImplementation(EmptyRecuperoDatiMissioniCorrentiPerOperatoreLogistico.class
						.getName());
		ServiceTask assegnazioneRitiroAMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("assegnazioneRitiroAMissioneCorrente");
		assegnazioneRitiroAMissioneCorrente
				.setImplementation(EmptyAssegnazioneRitiroAMissioneCorrente.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", assegnaNuoviRitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("assegnaNuoviRitiri");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE RECUPERO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoNuoviRitiriAAmministratore")
				.list();
		assertEquals(notificaErroreRecuperoNuoviRitiriAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoNuoviRitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreRecuperoNuoviRitiriAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE MISSIONI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE AGGIORNAMENTO SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore
						.size(),
				1);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico
						.size(),
				1);

		// VERIFICO CHE I RECUPERI DEI NUOVI RITIRI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoNuoviRitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoNuoviRitiri").list();
		assertEquals(recuperoNuoviRitiri.size(), 2);

		// VERIFICO CHE I RECUPERI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrentiPerOperatoreLogistico")
				.list();
		assertEquals(
				recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory.size(),
				2);

		// VERIFICO CHE LE ASSEGNAZIONI DEI NUOVI RITIRI ALLE MISSIONI SONO
		// STATE EFFETTUATE
		List<HistoricActivityInstance> assegnazioneRitiroAMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("assegnazioneRitiroAMissioneCorrente").list();
		assertEquals(assegnazioneRitiroAMissioneCorrenteHistory.size(), 4);

		// VERIFICO CHE GLI AGGIORNAMENTI DELLE MISSIONI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoMissioneCorrente").list();
		assertEquals(aggiornamentoMissioneCorrenteHistory.size(), 4);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE MISSIONI AGGIORNATE NON SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneMissioniAggiornate = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneMissioniAggiornate").list();
		assertEquals(segnalazioneMissioniAggiornate.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "bpm/tempo/assegna_nuovi_ritiri.bpmn20.xml" })
	public void testSegnalazioneErroreGraveAggiornamentoCorrente() {
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
		ProcessDefinition assegnaNuoviRitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel assegnaNuoviRitiriModel = repositoryService
				.getBpmnModel(assegnaNuoviRitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAssegnaNuoviRitiri = (StartEvent) assegnaNuoviRitiriModel
				.getFlowElement("timerAssegnaNuoviRitiri");
		timerAssegnaNuoviRitiri.getEventDefinitions().clear();
		ServiceTask recuperoNuoviRitiriService = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoNuoviRitiri");
		recuperoNuoviRitiriService
				.setImplementation(RecuperoNuoviRitiriPopolati.class.getName());
		ServiceTask aggiornamentoMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("aggiornamentoMissioneCorrente");
		aggiornamentoMissioneCorrente
				.setImplementation(AggiornamentoMissioneCorrenteConErroreGrave.class
						.getName());
		ServiceTask recuperoDatiMissioniCorrentiPerOperatoreLogistico = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("recuperoDatiMissioniCorrentiPerOperatoreLogistico");
		recuperoDatiMissioniCorrentiPerOperatoreLogistico
				.setImplementation(EmptyRecuperoDatiMissioniCorrentiPerOperatoreLogistico.class
						.getName());
		ServiceTask assegnazioneRitiroAMissioneCorrente = (ServiceTask) assegnaNuoviRitiriModel
				.getFlowElement("assegnazioneRitiroAMissioneCorrente");
		assegnazioneRitiroAMissioneCorrente
				.setImplementation(EmptyAssegnazioneRitiroAMissioneCorrente.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", assegnaNuoviRitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("assegnaNuoviRitiri");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE RECUPERO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoNuoviRitiriAAmministratore")
				.list();
		assertEquals(notificaErroreRecuperoNuoviRitiriAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreRecuperoNuoviRitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoNuoviRitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreRecuperoNuoviRitiriAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE MISSIONI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreMissioniAAmministratore").list();
		assertEquals(segnalazioneErroreMissioniAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE ERRORE AGGIORNAMENTO SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAAmministratore
						.size(),
				1);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoMissioneCorrenteAOperatoreLogistico
						.size(),
				1);

		// VERIFICO CHE I RECUPERI DEI NUOVI RITIRI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoNuoviRitiri = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoNuoviRitiri").list();
		assertEquals(recuperoNuoviRitiri.size(), 1);

		// VERIFICO CHE I RECUPERI DELLE MISSIONI SONO STATI EFFETTUATI
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrentiPerOperatoreLogistico")
				.list();
		assertEquals(
				recuperoDatiMissioniCorrentiPerOperatoreLogisticoHistory.size(),
				1);

		// VERIFICO CHE LE ASSEGNAZIONI DEI NUOVI RITIRI ALLE MISSIONI SONO
		// STATE EFFETTUATE
		List<HistoricActivityInstance> assegnazioneRitiroAMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("assegnazioneRitiroAMissioneCorrente").list();
		assertEquals(assegnazioneRitiroAMissioneCorrenteHistory.size(), 1);

		// VERIFICO CHE GLI AGGIORNAMENTI DELLE MISSIONI SONO STATI
		// EFFETTUATI
		List<HistoricActivityInstance> aggiornamentoMissioneCorrenteHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoMissioneCorrente").list();
		assertEquals(aggiornamentoMissioneCorrenteHistory.size(), 1);

		// VERIFICO CHE LE EMAIL DI SEGNALAZIONE MISSIONI AGGIORNATE SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneMissioniAggiornate = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneMissioniAggiornate").list();
		assertEquals(segnalazioneMissioniAggiornate.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

}
