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

import static it.vige.greenarea.dto.StatoMissione.COMPLETED;
import static it.vige.greenarea.dto.StatoMissione.STARTED;
import static it.vige.greenarea.dto.StatoMissione.WAITING;
import static it.vige.greenarea.dto.StatoRichiesta.ACCETTATO;
import static it.vige.greenarea.dto.StatoRichiesta.RIFIUTATO;
import static it.vige.greenarea.dto.TipoRichiesta.RITIRO;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.autista.gestioneconsegne.EmptyAggiornaConsegna;
import it.vige.greenarea.bpm.autista.gestioneconsegne.EmptyAggiornaStatoInCarico;
import it.vige.greenarea.bpm.autista.gestioneconsegne.EmptyNotificaAggiornamentoConsegnaAOperatoreLogistico;
import it.vige.greenarea.bpm.autista.gestionemissioni.EmptyAggiornaMissione;
import it.vige.greenarea.bpm.autista.gestionemissioni.EmptyNotificaAggiornamentoMissioneAOperatoreLogistico;
import it.vige.greenarea.bpm.autista.gestionemissioni.EmptyPresaInCaricoMissione;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.operatorelogistico.segnalanuoviritiri.EmptyAggiornamentoMissione;
import it.vige.greenarea.bpm.tempo.autorizzamissioni.EmptyAggiornamentoRanking;
import it.vige.greenarea.bpm.tempo.autorizzamissioni.EmptyElaborazioneRanking;
import it.vige.greenarea.bpm.tempo.autorizzamissioni.EmptyRecuperoDatiMissioniCorrenti;
import it.vige.greenarea.bpm.tempo.autorizzamissioni.EmptyRecuperoDatiPolicy;
import it.vige.greenarea.bpm.tempo.service.autorizzamissioni.ElaborazioneRankingPopolati;
import it.vige.greenarea.bpm.tempo.service.autorizzamissioni.ErroreMissioniMancanti;
import it.vige.greenarea.bpm.tempo.service.autorizzamissioni.NotificaErroreAggiornamentoRanking;
import it.vige.greenarea.bpm.tempo.service.autorizzamissioni.NotificaErroreReperimentoMissioni;
import it.vige.greenarea.bpm.tempo.service.autorizzamissioni.NotificaErroreReperimentoPolicy;
import it.vige.greenarea.bpm.tempo.service.autorizzamissioni.RecuperoDatiMissioniCorrentiPopolate;
import it.vige.greenarea.bpm.tempo.service.autorizzamissioni.SollecitoPolicyMancanti;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;

import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.test.ResourceActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.subethamail.smtp.server.SMTPServer;

public class AutorizzaMissioniTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "amministratore";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public AutorizzaMissioniTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/tempo/autorizza_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/operatorelogistico/segnala_nuovi_ritiri.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_consegne.bpmn20.xml" })
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

		// POPOLO LE MISSIONI, IL RANKING E INIZIO IL PROCESSO
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition autorizzaMissioni = processiAttivi.get(0);
		ProcessDefinition gestioneConsegne = processiAttivi.get(1);
		ProcessDefinition gestioneMissioni = processiAttivi.get(2);
		ProcessDefinition segnalaNuoviRitiri = processiAttivi.get(3);
		BpmnModel autorizzaMissioniModel = repositoryService
				.getBpmnModel(autorizzaMissioni.getId());
		BpmnModel segnalaNuoviRitiriModel = repositoryService
				.getBpmnModel(segnalaNuoviRitiri.getId());
		BpmnModel gestioneMissioniModel = repositoryService
				.getBpmnModel(gestioneMissioni.getId());
		BpmnModel gestioneConsegneModel = repositoryService
				.getBpmnModel(gestioneConsegne.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAutorizzaMissioni = (StartEvent) autorizzaMissioniModel
				.getFlowElement("timerAutorizzaMissioni");
		timerAutorizzaMissioni.getEventDefinitions().clear();
		ServiceTask recuperoDatiMissioniCorrenti = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiMissioniCorrenti");
		recuperoDatiMissioniCorrenti
				.setImplementation(RecuperoDatiMissioniCorrentiPopolate.class
						.getName());
		ServiceTask elaborazioneRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("elaborazioneRanking");
		elaborazioneRanking.setImplementation(ElaborazioneRankingPopolati.class
				.getName());
		ServiceTask recuperoDatiPolicy = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiPolicy");
		recuperoDatiPolicy.setImplementation(EmptyRecuperoDatiPolicy.class
				.getName());
		ServiceTask aggiornamentoRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("aggiornamentoRanking");
		aggiornamentoRanking.setImplementation(EmptyAggiornamentoRanking.class
				.getName());
		ServiceTask aggiornamentoMissione = (ServiceTask) segnalaNuoviRitiriModel
				.getFlowElement("aggiornamentoMissione");
		aggiornamentoMissione
				.setImplementation(EmptyAggiornamentoMissione.class.getName());
		UserTask aggiornaMissioneUserTask = (UserTask) gestioneMissioniModel
				.getFlowElement("aggiornaMissione");
		ActivitiListener aggiornaMissioneService = aggiornaMissioneUserTask
				.getTaskListeners().get(0);
		aggiornaMissioneService.setImplementation(EmptyAggiornaMissione.class
				.getName());
		ActivitiListener notificaAggiornamentoMissioneAOperatoreLogisticoService = aggiornaMissioneUserTask
				.getTaskListeners().get(1);
		notificaAggiornamentoMissioneAOperatoreLogisticoService
				.setImplementation(EmptyNotificaAggiornamentoMissioneAOperatoreLogistico.class
						.getName());
		ActivitiListener presaInCaricoMissioneService = aggiornaMissioneUserTask
				.getTaskListeners().get(2);
		presaInCaricoMissioneService
				.setImplementation(EmptyPresaInCaricoMissione.class.getName());
		UserTask aggiornaConsegnaUserTask = (UserTask) gestioneConsegneModel
				.getFlowElement("aggiornaConsegna");
		ActivitiListener aggiornaStatoInCaricoService = aggiornaConsegnaUserTask
				.getTaskListeners().get(0);
		aggiornaStatoInCaricoService
				.setImplementation(EmptyAggiornaStatoInCarico.class.getName());
		ActivitiListener aggiornaConsegnaService = aggiornaConsegnaUserTask
				.getTaskListeners().get(1);
		aggiornaConsegnaService.setImplementation(EmptyAggiornaConsegna.class
				.getName());
		ActivitiListener notificaAggiornamentoConsegnaAOperatoreLogisticoService = aggiornaConsegnaUserTask
				.getTaskListeners().get(2);
		notificaAggiornamentoConsegnaAOperatoreLogisticoService
				.setImplementation(EmptyNotificaAggiornamentoConsegnaAOperatoreLogistico.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", autorizzaMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", segnalaNuoviRitiriModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneConsegneModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("autorizzaMissioni");

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO MISSIONI NON
		// SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoMissioniAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoMissioniAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreReperimentoMissioniAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO RANKING NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAggiornamentoRankingAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoRankingAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoRankingAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreAggiornamentoRankingAOperatoriLogistici.size(), 0);

		// VERIFICO CHE IL RECUPERO DATI POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiPolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiPolicy").list();
		assertEquals(recuperoDatiPolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI MISSIONI E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrenti").list();
		assertEquals(recuperoDatiMissioniCorrentiHistory.size(), 1);

		// VERIFICO CHE L'ELABORAZIONE RANKING E' STATA EFFETTUATA
		List<HistoricActivityInstance> elaborazioneRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("elaborazioneRanking").list();
		assertEquals(elaborazioneRankingHistory.size(), 1);

		// VERIFICO CHE L'AGGIORNAMENTO RANKING E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoRanking").list();
		assertEquals(aggiornamentoRankingHistory.size(), 1);

		// PARTE IL TEST DI SEGNALA NUOVI RITIRI
		segnalaNuoviRitiriTestOK();

		// PARTE IL TEST DI GESTIONE MISSIONI
		gestioneMissioniTestOK();

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/tempo/autorizza_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/operatorelogistico/segnala_nuovi_ritiri.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_consegne.bpmn20.xml" })
	public void testNotificaErroreReperimentoPolicy() {
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

		// POPOLO LE MISSIONI, IL RANKING E INIZIO IL PROCESSO
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition autorizzaMissioni = processiAttivi.get(0);
		ProcessDefinition segnalaNuoviRitiri = processiAttivi.get(1);
		ProcessDefinition gestioneMissioni = processiAttivi.get(2);
		ProcessDefinition gestioneConsegne = processiAttivi.get(3);
		BpmnModel autorizzaMissioniModel = repositoryService
				.getBpmnModel(autorizzaMissioni.getId());
		BpmnModel segnalaNuoviRitiriModel = repositoryService
				.getBpmnModel(segnalaNuoviRitiri.getId());
		BpmnModel gestioneMissioniModel = repositoryService
				.getBpmnModel(gestioneMissioni.getId());
		BpmnModel gestioneConsegneModel = repositoryService
				.getBpmnModel(gestioneConsegne.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAutorizzaMissioni = (StartEvent) autorizzaMissioniModel
				.getFlowElement("timerAutorizzaMissioni");
		timerAutorizzaMissioni.getEventDefinitions().clear();
		ServiceTask recuperoDatiPolicy = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiPolicy");
		recuperoDatiPolicy
				.setImplementation(NotificaErroreReperimentoPolicy.class
						.getName());
		ServiceTask recuperoDatiMissioniCorrenti = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiMissioniCorrenti");
		recuperoDatiMissioniCorrenti
				.setImplementation(EmptyRecuperoDatiMissioniCorrenti.class
						.getName());
		ServiceTask elaborazioneRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("elaborazioneRanking");
		elaborazioneRanking.setImplementation(EmptyElaborazioneRanking.class
				.getName());
		ServiceTask aggiornamentoRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("aggiornamentoRanking");
		aggiornamentoRanking.setImplementation(EmptyAggiornamentoRanking.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", autorizzaMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", segnalaNuoviRitiriModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneConsegneModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("autorizzaMissioni");

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				2);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO MISSIONI NON
		// SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoMissioniAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoMissioniAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreReperimentoMissioniAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO RANKING NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAggiornamentoRankingAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoRankingAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoRankingAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreAggiornamentoRankingAOperatoriLogistici.size(), 0);

		// VERIFICO CHE IL RECUPERO DATI POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiPolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiPolicy").list();
		assertEquals(recuperoDatiPolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI MISSIONI NON E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrenti").list();
		assertEquals(recuperoDatiMissioniCorrentiHistory.size(), 0);

		// VERIFICO CHE L'ELABORAZIONE RANKING NON E' STATA EFFETTUATA
		List<HistoricActivityInstance> elaborazioneRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("elaborazioneRanking").list();
		assertEquals(elaborazioneRankingHistory.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO RANKING NON E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoRanking").list();
		assertEquals(aggiornamentoRankingHistory.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/tempo/autorizza_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/operatorelogistico/segnala_nuovi_ritiri.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_consegne.bpmn20.xml" })
	public void testSollecitoPolicyMancanti() {
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

		// POPOLO LE MISSIONI, IL RANKING E INIZIO IL PROCESSO
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition autorizzaMissioni = processiAttivi.get(0);
		ProcessDefinition segnalaNuoviRitiri = processiAttivi.get(1);
		ProcessDefinition gestioneMissioni = processiAttivi.get(2);
		ProcessDefinition gestioneConsegne = processiAttivi.get(3);
		BpmnModel autorizzaMissioniModel = repositoryService
				.getBpmnModel(autorizzaMissioni.getId());
		BpmnModel segnalaNuoviRitiriModel = repositoryService
				.getBpmnModel(segnalaNuoviRitiri.getId());
		BpmnModel gestioneMissioniModel = repositoryService
				.getBpmnModel(gestioneMissioni.getId());
		BpmnModel gestioneConsegneModel = repositoryService
				.getBpmnModel(gestioneConsegne.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAutorizzaMissioni = (StartEvent) autorizzaMissioniModel
				.getFlowElement("timerAutorizzaMissioni");
		timerAutorizzaMissioni.getEventDefinitions().clear();
		ServiceTask recuperoDatiPolicy = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiPolicy");
		recuperoDatiPolicy.setImplementation(SollecitoPolicyMancanti.class
				.getName());
		ServiceTask recuperoDatiMissioniCorrenti = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiMissioniCorrenti");
		recuperoDatiMissioniCorrenti
				.setImplementation(EmptyRecuperoDatiMissioniCorrenti.class
						.getName());
		ServiceTask elaborazioneRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("elaborazioneRanking");
		elaborazioneRanking.setImplementation(EmptyElaborazioneRanking.class
				.getName());
		ServiceTask aggiornamentoRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("aggiornamentoRanking");
		aggiornamentoRanking.setImplementation(EmptyAggiornamentoRanking.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", autorizzaMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", segnalaNuoviRitiriModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneConsegneModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("autorizzaMissioni");

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI SONO STATE MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 2);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 1);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO MISSIONI NON
		// SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoMissioniAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoMissioniAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreReperimentoMissioniAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO RANKING NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAggiornamentoRankingAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoRankingAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoRankingAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreAggiornamentoRankingAOperatoriLogistici.size(), 0);

		// VERIFICO CHE IL RECUPERO DATI POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiPolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiPolicy").list();
		assertEquals(recuperoDatiPolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI MISSIONI NON E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrenti").list();
		assertEquals(recuperoDatiMissioniCorrentiHistory.size(), 0);

		// VERIFICO CHE L'ELABORAZIONE RANKING NON E' STATA EFFETTUATA
		List<HistoricActivityInstance> elaborazioneRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("elaborazioneRanking").list();
		assertEquals(elaborazioneRankingHistory.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO RANKING NON E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoRanking").list();
		assertEquals(aggiornamentoRankingHistory.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/tempo/autorizza_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/operatorelogistico/segnala_nuovi_ritiri.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_consegne.bpmn20.xml" })
	public void testErroreMissioniMancanti() {
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

		// POPOLO LE MISSIONI, IL RANKING E INIZIO IL PROCESSO
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition autorizzaMissioni = processiAttivi.get(0);
		ProcessDefinition segnalaNuoviRitiri = processiAttivi.get(1);
		ProcessDefinition gestioneMissioni = processiAttivi.get(2);
		ProcessDefinition gestioneConsegne = processiAttivi.get(3);
		BpmnModel autorizzaMissioniModel = repositoryService
				.getBpmnModel(autorizzaMissioni.getId());
		BpmnModel segnalaNuoviRitiriModel = repositoryService
				.getBpmnModel(segnalaNuoviRitiri.getId());
		BpmnModel gestioneMissioniModel = repositoryService
				.getBpmnModel(gestioneMissioni.getId());
		BpmnModel gestioneConsegneModel = repositoryService
				.getBpmnModel(gestioneConsegne.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAutorizzaMissioni = (StartEvent) autorizzaMissioniModel
				.getFlowElement("timerAutorizzaMissioni");
		timerAutorizzaMissioni.getEventDefinitions().clear();
		ServiceTask recuperoDatiMissioniCorrenti = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiMissioniCorrenti");
		recuperoDatiMissioniCorrenti
				.setImplementation(ErroreMissioniMancanti.class.getName());
		ServiceTask recuperoDatiPolicy = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiPolicy");
		recuperoDatiPolicy.setImplementation(EmptyRecuperoDatiPolicy.class
				.getName());
		ServiceTask elaborazioneRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("elaborazioneRanking");
		elaborazioneRanking.setImplementation(EmptyElaborazioneRanking.class
				.getName());
		ServiceTask aggiornamentoRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("aggiornamentoRanking");
		aggiornamentoRanking.setImplementation(EmptyAggiornamentoRanking.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", autorizzaMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", segnalaNuoviRitiriModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneConsegneModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("autorizzaMissioni");

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO MISSIONI NON
		// SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoMissioniAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoMissioniAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreReperimentoMissioniAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO RANKING NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAggiornamentoRankingAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoRankingAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoRankingAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreAggiornamentoRankingAOperatoriLogistici.size(), 0);

		// VERIFICO CHE IL RECUPERO DATI POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiPolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiPolicy").list();
		assertEquals(recuperoDatiPolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI MISSIONI E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrenti").list();
		assertEquals(recuperoDatiMissioniCorrentiHistory.size(), 1);

		// VERIFICO CHE L'ELABORAZIONE RANKING E' STATA EFFETTUATA
		List<HistoricActivityInstance> elaborazioneRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("elaborazioneRanking").list();
		assertEquals(elaborazioneRankingHistory.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO RANKING E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoRanking").list();
		assertEquals(aggiornamentoRankingHistory.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/tempo/autorizza_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/operatorelogistico/segnala_nuovi_ritiri.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_consegne.bpmn20.xml" })
	public void testNotificaErroreReperimentoMissioni() {
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

		// POPOLO LE MISSIONI, IL RANKING E INIZIO IL PROCESSO
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition autorizzaMissioni = processiAttivi.get(0);
		ProcessDefinition segnalaNuoviRitiri = processiAttivi.get(1);
		ProcessDefinition gestioneMissioni = processiAttivi.get(2);
		ProcessDefinition gestioneConsegne = processiAttivi.get(3);
		BpmnModel autorizzaMissioniModel = repositoryService
				.getBpmnModel(autorizzaMissioni.getId());
		BpmnModel segnalaNuoviRitiriModel = repositoryService
				.getBpmnModel(segnalaNuoviRitiri.getId());
		BpmnModel gestioneMissioniModel = repositoryService
				.getBpmnModel(gestioneMissioni.getId());
		BpmnModel gestioneConsegneModel = repositoryService
				.getBpmnModel(gestioneConsegne.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAutorizzaMissioni = (StartEvent) autorizzaMissioniModel
				.getFlowElement("timerAutorizzaMissioni");
		timerAutorizzaMissioni.getEventDefinitions().clear();
		ServiceTask recuperoDatiMissioniCorrenti = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiMissioniCorrenti");
		recuperoDatiMissioniCorrenti
				.setImplementation(NotificaErroreReperimentoMissioni.class
						.getName());
		ServiceTask recuperoDatiPolicy = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiPolicy");
		recuperoDatiPolicy.setImplementation(EmptyRecuperoDatiPolicy.class
				.getName());
		ServiceTask elaborazioneRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("elaborazioneRanking");
		elaborazioneRanking.setImplementation(EmptyElaborazioneRanking.class
				.getName());
		ServiceTask aggiornamentoRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("aggiornamentoRanking");
		aggiornamentoRanking.setImplementation(EmptyAggiornamentoRanking.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", autorizzaMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", segnalaNuoviRitiriModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneConsegneModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("autorizzaMissioni");

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO MISSIONI SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoMissioniAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoMissioniAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoMissioniAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreReperimentoMissioniAOperatoriLogistici.size(), 2);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO RANKING NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAggiornamentoRankingAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoRankingAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoRankingAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreAggiornamentoRankingAOperatoriLogistici.size(), 0);

		// VERIFICO CHE IL RECUPERO DATI POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiPolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiPolicy").list();
		assertEquals(recuperoDatiPolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI MISSIONI E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrenti").list();
		assertEquals(recuperoDatiMissioniCorrentiHistory.size(), 1);

		// VERIFICO CHE L'ELABORAZIONE RANKING E' STATA EFFETTUATA
		List<HistoricActivityInstance> elaborazioneRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("elaborazioneRanking").list();
		assertEquals(elaborazioneRankingHistory.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO RANKING E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoRanking").list();
		assertEquals(aggiornamentoRankingHistory.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/tempo/autorizza_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/operatorelogistico/segnala_nuovi_ritiri.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_missioni.bpmn20.xml",
			"it/vige/greenarea/bpm/autista/gestione_consegne.bpmn20.xml" })
	public void testNotificaErroreAggiornamentoRanking() {
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

		// POPOLO LE MISSIONI, IL RANKING E INIZIO IL PROCESSO
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition autorizzaMissioni = processiAttivi.get(0);
		ProcessDefinition segnalaNuoviRitiri = processiAttivi.get(1);
		ProcessDefinition gestioneMissioni = processiAttivi.get(2);
		ProcessDefinition gestioneConsegne = processiAttivi.get(3);
		BpmnModel autorizzaMissioniModel = repositoryService
				.getBpmnModel(autorizzaMissioni.getId());
		BpmnModel segnalaNuoviRitiriModel = repositoryService
				.getBpmnModel(segnalaNuoviRitiri.getId());
		BpmnModel gestioneMissioniModel = repositoryService
				.getBpmnModel(gestioneMissioni.getId());
		BpmnModel gestioneConsegneModel = repositoryService
				.getBpmnModel(gestioneConsegne.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerAutorizzaMissioni = (StartEvent) autorizzaMissioniModel
				.getFlowElement("timerAutorizzaMissioni");
		timerAutorizzaMissioni.getEventDefinitions().clear();
		ServiceTask recuperoDatiMissioniCorrenti = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiMissioniCorrenti");
		recuperoDatiMissioniCorrenti
				.setImplementation(RecuperoDatiMissioniCorrentiPopolate.class
						.getName());
		ServiceTask aggiornamentoRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("aggiornamentoRanking");
		aggiornamentoRanking
				.setImplementation(NotificaErroreAggiornamentoRanking.class
						.getName());
		ServiceTask recuperoDatiPolicy = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("recuperoDatiPolicy");
		recuperoDatiPolicy.setImplementation(EmptyRecuperoDatiPolicy.class
				.getName());
		ServiceTask elaborazioneRanking = (ServiceTask) autorizzaMissioniModel
				.getFlowElement("elaborazioneRanking");
		elaborazioneRanking.setImplementation(EmptyElaborazioneRanking.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", autorizzaMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", segnalaNuoviRitiriModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneConsegneModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("autorizzaMissioni");

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO MISSIONI NON
		// SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoMissioniAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoMissioniAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoMissioniAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreReperimentoMissioniAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO RANKING SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAggiornamentoRankingAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoRankingAAmministratore.size(),
				1);
		List<HistoricActivityInstance> notificaErroreAggiornamentoRankingAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoRankingAOperatoriLogistici")
				.list();
		assertEquals(
				notificaErroreAggiornamentoRankingAOperatoriLogistici.size(), 2);

		// VERIFICO CHE IL RECUPERO DATI POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiPolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiPolicy").list();
		assertEquals(recuperoDatiPolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI MISSIONI E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiMissioniCorrentiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioniCorrenti").list();
		assertEquals(recuperoDatiMissioniCorrentiHistory.size(), 1);

		// VERIFICO CHE L'ELABORAZIONE RANKING E' STATA EFFETTUATA
		List<HistoricActivityInstance> elaborazioneRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("elaborazioneRanking").list();
		assertEquals(elaborazioneRankingHistory.size(), 1);

		// VERIFICO CHE L'AGGIORNAMENTO RANKING E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoRankingHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoRanking").list();
		assertEquals(aggiornamentoRankingHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	public void gestioneMissioniTestOK() {
		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// MISSIONI
		List<Task> aggiornaMissioni = taskService.createTaskQuery()
				.taskName("Aggiorna Missione").includeProcessVariables().list();
		assertEquals(aggiornaMissioni.size(), 2);
		Task aggiornaMissione = aggiornaMissioni.get(0);
		Map<String, Object> variables = aggiornaMissione.getProcessVariables();
		@SuppressWarnings("unchecked")
		Missione missione = ((List<Missione>) variables.get("missioni")).get(0);
		assertEquals(missione.getStato(), STARTED);
		assertEquals(aggiornaMissione.getAssignee(), "autista1");
		Task aggiornaMissione2 = aggiornaMissioni.get(1);
		variables = aggiornaMissione2.getProcessVariables();
		@SuppressWarnings("unchecked")
		Missione missione2 = ((List<Missione>) variables.get("missioni"))
				.get(1);
		assertEquals(missione2.getStato(), WAITING);
		assertEquals(aggiornaMissione2.getAssignee(), "autista1");

		// MODIFICA DEI PARAMETRI DELLO STATO 1
		missione.setStato(COMPLETED);
		taskService.complete(aggiornaMissione.getId(), variables);
		aggiornaMissioni = taskService.createTaskQuery()
				.taskName("Aggiorna Missione").list();
		assertEquals(aggiornaMissioni.size(), 1);

		// MODIFICA DEI PARAMETRI DELLO STATO 2
		missione2.setStato(STARTED);
		taskService.complete(aggiornaMissione2.getId(), variables);
		aggiornaMissioni = taskService.createTaskQuery()
				.taskName("Aggiorna Missione").list();
		assertEquals(aggiornaMissioni.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DELLE MISSIONI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreMissioneAAutista = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreMissioneAAutista").list();
		assertEquals(notificaErroreMissioneAAutista.size(), 0);
		List<HistoricActivityInstance> notificaErroreMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreMissioneAOperatoreLogistico").list();
		assertEquals(notificaErroreMissioneAOperatoreLogistico.size(), 0);
		List<HistoricActivityInstance> notificaErroreMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreMissioneAAmministratore").list();
		assertEquals(notificaErroreMissioneAAmministratore.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO DELLE MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> aggiornaStato = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornaMissione").list();
		assertEquals(aggiornaStato.size(), 2);

		// PARTE IL TEST DI GESTIONE CONSEGNE
		gestioneConsegneTestOK();
	}

	public void gestioneConsegneTestOK() {
		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// CONSEGNE
		List<Task> aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").includeProcessVariables().list();
		assertEquals(aggiornaConsegne.size(), 4);

		Task aggiornaConsegna = aggiornaConsegne.get(0);
		Map<String, Object> variables = aggiornaConsegna.getProcessVariables();
		Missione missione = (Missione) variables.get("missione");
		Richiesta richiesta = missione.getRichieste().get(0);
		assertEquals(richiesta.getStato(), ACCETTATO.name());
		assertEquals(richiesta.getMotivazione(), "bello");

		Task aggiornaConsegna2 = aggiornaConsegne.get(1);
		variables = aggiornaConsegna2.getProcessVariables();
		missione = (Missione) variables.get("missione");
		Richiesta richiesta2 = missione.getRichieste().get(1);
		assertEquals(richiesta2.getStato(), ACCETTATO.name());
		assertEquals(richiesta2.getMotivazione(), "mi piace");

		Task aggiornaConsegna3 = aggiornaConsegne.get(2);
		variables = aggiornaConsegna3.getProcessVariables();
		missione = (Missione) variables.get("missione");
		Richiesta richiesta3 = missione.getRichieste().get(0);
		assertEquals(richiesta3.getStato(), ACCETTATO.name());
		assertEquals(richiesta3.getMotivazione(), "bello");

		Task aggiornaConsegna4 = aggiornaConsegne.get(3);
		variables = aggiornaConsegna4.getProcessVariables();
		missione = (Missione) variables.get("missione");
		Richiesta richiesta4 = missione.getRichieste().get(1);
		assertEquals(richiesta4.getStato(), ACCETTATO.name());
		assertEquals(richiesta4.getMotivazione(), "mi piace");

		// MODIFICA DEI PARAMETRI DELLO STATO 1
		richiesta.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna.getId(), variables);
		aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").list();
		assertEquals(aggiornaConsegne.size(), 3);

		// MODIFICA DEI PARAMETRI DELLO STATO 2
		richiesta2.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna2.getId(), variables);
		aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").list();
		assertEquals(aggiornaConsegne.size(), 2);

		// MODIFICA DEI PARAMETRI DELLO STATO 3
		richiesta3.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna3.getId(), variables);
		aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").list();
		assertEquals(aggiornaConsegne.size(), 1);

		// MODIFICA DEI PARAMETRI DELLO STATO 4
		richiesta4.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna4.getId(), variables);
		aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").list();
		assertEquals(aggiornaConsegne.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DELLE CONSEGNE NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAAutista = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAAutista").list();
		assertEquals(notificaErroreAAutista.size(), 0);
		List<HistoricActivityInstance> notificaErroreAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAOperatoreLogistico").list();
		assertEquals(notificaErroreAOperatoreLogistico.size(), 0);
		List<HistoricActivityInstance> notificaErroreAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAAmministratore").list();
		assertEquals(notificaErroreAAmministratore.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO DELLE CONSEGNE E' STATO ESEGUITO
		List<HistoricActivityInstance> aggiornaStato = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornaConsegna").list();
		assertEquals(aggiornaStato.size(), 4);

	}

	public void segnalaNuoviRitiriTestOK() {
		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// STATO RICHIESTO
		List<Task> visualizzaStatiRichiesti = taskService.createTaskQuery()
				.taskName("Stato Richiesto").includeProcessVariables().list();
		assertEquals(visualizzaStatiRichiesti.size(), 4);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// STATO RICHIESTO 1
		Task visualizzaStatoRichiesto = visualizzaStatiRichiesti.get(0);
		visualizzaStatoRichiesto.setAssignee(USER_NAME);
		Map<String, Object> variables = visualizzaStatoRichiesto
				.getProcessVariables();
		@SuppressWarnings("unchecked")
		Richiesta richiesta = ((List<OperatoreLogistico>) variables
				.get("operatorilogistici")).get(0).getRitiri().get(0);
		assertEquals(richiesta.getTipo(), RITIRO.name());
		assertEquals(visualizzaStatoRichiesto.getAssignee(), USER_NAME);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// STATO RICHIESTO 2
		Task visualizzaStatoRichiesto2 = visualizzaStatiRichiesti.get(1);
		visualizzaStatoRichiesto2.setAssignee(USER_NAME);
		variables = visualizzaStatoRichiesto2.getProcessVariables();
		@SuppressWarnings("unchecked")
		Richiesta richiesta2 = ((List<OperatoreLogistico>) variables
				.get("operatorilogistici")).get(0).getRitiri().get(1);
		assertEquals(richiesta2.getTipo(), RITIRO.name());
		assertEquals(visualizzaStatoRichiesto2.getAssignee(), USER_NAME);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// STATO RICHIESTO 3
		Task visualizzaStatoRichiesto3 = visualizzaStatiRichiesti.get(2);
		visualizzaStatoRichiesto3.setAssignee(USER_NAME);
		variables = visualizzaStatoRichiesto3.getProcessVariables();
		@SuppressWarnings("unchecked")
		Richiesta richiesta3 = ((List<OperatoreLogistico>) variables
				.get("operatorilogistici")).get(1).getRitiri().get(0);
		assertEquals(richiesta3.getTipo(), RITIRO.name());
		assertEquals(visualizzaStatoRichiesto3.getAssignee(), USER_NAME);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// STATO RICHIESTO 4
		Task visualizzaStatoRichiesto4 = visualizzaStatiRichiesti.get(3);
		visualizzaStatoRichiesto4.setAssignee(USER_NAME);
		variables = visualizzaStatoRichiesto4.getProcessVariables();
		@SuppressWarnings("unchecked")
		Richiesta richiesta4 = ((List<OperatoreLogistico>) variables
				.get("operatorilogistici")).get(1).getRitiri().get(1);
		assertEquals(richiesta4.getTipo(), RITIRO.name());
		assertEquals(visualizzaStatoRichiesto4.getAssignee(), USER_NAME);

		// MODIFICA DEI PARAMETRI DELLO STATO 1
		variables.put("autista", "autista1");
		taskService.complete(visualizzaStatoRichiesto.getId(), variables);
		visualizzaStatiRichiesti = taskService.createTaskQuery()
				.taskName("Stato Richiesto").list();
		assertEquals(visualizzaStatiRichiesti.size(), 3);

		// MODIFICA DEI PARAMETRI DELLO STATO 2
		variables.put("autista", "autista2");
		taskService.complete(visualizzaStatoRichiesto2.getId(), variables);
		visualizzaStatiRichiesti = taskService.createTaskQuery()
				.taskName("Stato Richiesto").list();
		assertEquals(visualizzaStatiRichiesti.size(), 2);

		// MODIFICA DEI PARAMETRI DELLO STATO 3
		variables.put("autista", "autista1");
		taskService.complete(visualizzaStatoRichiesto3.getId(), variables);
		visualizzaStatiRichiesti = taskService.createTaskQuery()
				.taskName("Stato Richiesto").list();
		assertEquals(visualizzaStatiRichiesti.size(), 1);

		// MODIFICA DEI PARAMETRI DELLO STATO 4
		variables.put("autista", "autista2");
		taskService.complete(visualizzaStatoRichiesto4.getId(), variables);
		visualizzaStatiRichiesti = taskService.createTaskQuery()
				.taskName("Stato Richiesto").list();
		assertEquals(visualizzaStatiRichiesti.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO DI STATO E' STATO ESEGUITO
		List<HistoricActivityInstance> aggiornaStato = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("statoRichiesto").list();
		assertEquals(aggiornaStato.size(), 4);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI ACCETTAZIONE
		// RICHIESTA
		List<Task> accettazioneRichieste = taskService.createTaskQuery()
				.taskName("Accettazione Richiesta").includeProcessVariables()
				.list();
		assertEquals(accettazioneRichieste.size(), 4);
		Task accettazioneRichiesta = accettazioneRichieste.get(0);
		assertEquals(accettazioneRichiesta.getAssignee(), "autista1");
		Task accettazioneRichiesta2 = accettazioneRichieste.get(1);
		assertEquals(accettazioneRichiesta2.getAssignee(), "autista2");
		Task accettazioneRichiesta3 = accettazioneRichieste.get(2);
		assertEquals(accettazioneRichiesta3.getAssignee(), "autista1");
		Task accettazioneRichiesta4 = accettazioneRichieste.get(3);
		assertEquals(accettazioneRichiesta4.getAssignee(), "autista2");

		variables = accettazioneRichiesta.getProcessVariables();

		// MODIFICA DEI PARAMETRI DELLO STATO 1
		variables.put("stato", ACCETTATO);
		taskService.complete(accettazioneRichiesta.getId(), variables);
		accettazioneRichieste = taskService.createTaskQuery()
				.taskName("Accettazione Richiesta").list();
		assertEquals(accettazioneRichieste.size(), 3);

		// MODIFICA DEI PARAMETRI DELLO STATO 2
		variables.put("stato", ACCETTATO);
		taskService.complete(accettazioneRichiesta2.getId(), variables);
		accettazioneRichieste = taskService.createTaskQuery()
				.taskName("Accettazione Richiesta").list();
		assertEquals(accettazioneRichieste.size(), 2);

		// MODIFICA DEI PARAMETRI DELLO STATO 3
		variables.put("stato", ACCETTATO);
		taskService.complete(accettazioneRichiesta3.getId(), variables);
		accettazioneRichieste = taskService.createTaskQuery()
				.taskName("Accettazione Richiesta").list();
		assertEquals(accettazioneRichieste.size(), 1);

		// MODIFICA DEI PARAMETRI DELLO STATO 4
		variables.put("stato", ACCETTATO);
		taskService.complete(accettazioneRichiesta4.getId(), variables);
		accettazioneRichieste = taskService.createTaskQuery()
				.taskName("Accettazione Richiesta").list();
		assertEquals(accettazioneRichieste.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE E' STATO ESEGUITO
		List<HistoricActivityInstance> aggiornamentoMissione = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoMissione").list();
		assertEquals(aggiornamentoMissione.size(), 4);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreLieveAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreLieveAAmministratore").list();
		assertEquals(segnalazioneErroreLieveAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreGraveAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreGraveAAmministratore").list();
		assertEquals(segnalazioneErroreGraveAAmministratore.size(), 0);
	}
}
