package it.vige.greenarea.bpm.tempo.test;

import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.tempo.importanuoviritiri.EmptyAggiornamentoNuoviRitiri;
import it.vige.greenarea.bpm.tempo.importanuoviritiri.EmptyVerificaDatiNuoviRitiri;
import it.vige.greenarea.bpm.tempo.service.importanuoviritiri.AggiornamentoNuoviRitiriConSegnalazioneErrore;
import it.vige.greenarea.bpm.tempo.service.importanuoviritiri.RecuperoDatiNuoviRitiriPopolati;
import it.vige.greenarea.bpm.tempo.service.importanuoviritiri.VerificaDatiNuoviRitiriConSollecitoNuoviRitiriNonCorretti;

import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.test.ResourceActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.test.Deployment;
import org.subethamail.smtp.server.SMTPServer;

public class ImportaNuoviRitiriTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "amministratore";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public ImportaNuoviRitiriTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/importa_nuovi_ritiri.bpmn20.xml" })
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
		ProcessDefinition importaNuoviRitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaNuoviRitiriModel = repositoryService
				.getBpmnModel(importaNuoviRitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaNuoviRitiri = (StartEvent) importaNuoviRitiriModel
				.getFlowElement("timerImportaNuoviRitiri");
		timerImportaNuoviRitiri.getEventDefinitions().clear();
		ServiceTask recuperoDatiNuoviRitiri = (ServiceTask) importaNuoviRitiriModel
				.getFlowElement("recuperoDatiNuoviRitiri");
		recuperoDatiNuoviRitiri
				.setImplementation(RecuperoDatiNuoviRitiriPopolati.class
						.getName());
		ServiceTask verificaDatiNuoviRitiri = (ServiceTask) importaNuoviRitiriModel
				.getFlowElement("verificaDatiNuoviRitiri");
		verificaDatiNuoviRitiri
				.setImplementation(EmptyVerificaDatiNuoviRitiri.class.getName());
		ServiceTask aggiornamentoNuoviRitiri = (ServiceTask) importaNuoviRitiriModel
				.getFlowElement("aggiornamentoNuoviRitiri");
		aggiornamentoNuoviRitiri
				.setImplementation(EmptyAggiornamentoNuoviRitiri.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaNuoviRitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaNuoviRitiri");

		// VERIFICO L'ESECUZIONE DEL RECUPERO DATI NUOVI RITIRI
		List<HistoricActivityInstance> recuperoDatiNuoviRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiNuoviRitiri").list();
		assertEquals(recuperoDatiNuoviRitiriHistory.size(), 2);

		// VERIFICO L'ESECUZIONE DELLA VERIFICA DATI NUOVI RITIRI
		List<HistoricActivityInstance> verificaDatiNuoviRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiNuoviRitiri").list();
		assertEquals(verificaDatiNuoviRitiriHistory.size(), 2);

		// VERIFICO L'ESECUZIONE DELL'AGGIORNAMENTO NUOVI RITIRI
		List<HistoricActivityInstance> aggiornamentoNuoviRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoNuoviRitiri").list();
		assertEquals(aggiornamentoNuoviRitiriHistory.size(), 2);

		// VERIFICO CHE LE EMAIL DI ERRORE DI NUOVI RITIRI NON CORRETTI NON SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiNuoviRitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiNuoviRitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiNuoviRitiriNonCorretti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NUOVI RITIRI NON
		// SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoNuoviRitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoNuoviRitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoNuoviRitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoNuoviRitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoNuoviRitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoNuoviRitiriAOperatoreLogistico
						.size(),
				0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/importa_nuovi_ritiri.bpmn20.xml" })
	public void testSollecitoPerDatiNuoviRitiriNonCorretti() {
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
		ProcessDefinition importaNuoviRitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaNuoviRitiriModel = repositoryService
				.getBpmnModel(importaNuoviRitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaNuoviRitiri = (StartEvent) importaNuoviRitiriModel
				.getFlowElement("timerImportaNuoviRitiri");
		timerImportaNuoviRitiri.getEventDefinitions().clear();
		ServiceTask verificaDatiNuoviRitiri = (ServiceTask) importaNuoviRitiriModel
				.getFlowElement("verificaDatiNuoviRitiri");
		verificaDatiNuoviRitiri
				.setImplementation(VerificaDatiNuoviRitiriConSollecitoNuoviRitiriNonCorretti.class
						.getName());
		ServiceTask recuperoDatiNuoviRitiri = (ServiceTask) importaNuoviRitiriModel
				.getFlowElement("recuperoDatiNuoviRitiri");
		recuperoDatiNuoviRitiri
				.setImplementation(RecuperoDatiNuoviRitiriPopolati.class
						.getName());
		ServiceTask aggiornamentoNuoviRitiri = (ServiceTask) importaNuoviRitiriModel
				.getFlowElement("aggiornamentoNuoviRitiri");
		aggiornamentoNuoviRitiri
				.setImplementation(EmptyAggiornamentoNuoviRitiri.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaNuoviRitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaNuoviRitiri");

		// VERIFICO L'ESECUZIONE DEL RECUPERO DATI NUOVI RITIRI
		List<HistoricActivityInstance> recuperoDatiNuoviRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiNuoviRitiri").list();
		assertEquals(recuperoDatiNuoviRitiriHistory.size(), 1);

		// VERIFICO L'ESECUZIONE DELLA VERIFICA DATI NUOVI RITIRI
		List<HistoricActivityInstance> verificaDatiNuoviRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiNuoviRitiri").list();
		assertEquals(verificaDatiNuoviRitiriHistory.size(), 1);

		// VERIFICO L'ESECUZIONE DELL'AGGIORNAMENTO NUOVI RITIRI
		List<HistoricActivityInstance> aggiornamentoNuoviRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoNuoviRitiri").list();
		assertEquals(aggiornamentoNuoviRitiriHistory.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI NUOVI RITIRI NON CORRETTI SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiNuoviRitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiNuoviRitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiNuoviRitiriNonCorretti
						.size(),
				1);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NUOVI RITIRI NON
		// SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoNuoviRitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoNuoviRitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoNuoviRitiriAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoNuoviRitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoNuoviRitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoNuoviRitiriAOperatoreLogistico
						.size(),
				0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/importa_nuovi_ritiri.bpmn20.xml" })
	public void testSegnalazioneErroreAggiornamentoNuoviRitiri() {
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
		ProcessDefinition importaNuoviRitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel importaNuoviRitiriModel = repositoryService
				.getBpmnModel(importaNuoviRitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerImportaNuoviRitiri = (StartEvent) importaNuoviRitiriModel
				.getFlowElement("timerImportaNuoviRitiri");
		timerImportaNuoviRitiri.getEventDefinitions().clear();
		ServiceTask aggiornamentoNuoviRitiri = (ServiceTask) importaNuoviRitiriModel
				.getFlowElement("aggiornamentoNuoviRitiri");
		aggiornamentoNuoviRitiri
				.setImplementation(AggiornamentoNuoviRitiriConSegnalazioneErrore.class
						.getName());
		ServiceTask recuperoDatiNuoviRitiri = (ServiceTask) importaNuoviRitiriModel
				.getFlowElement("recuperoDatiNuoviRitiri");
		recuperoDatiNuoviRitiri
				.setImplementation(RecuperoDatiNuoviRitiriPopolati.class
						.getName());
		ServiceTask verificaDatiNuoviRitiri = (ServiceTask) importaNuoviRitiriModel
				.getFlowElement("verificaDatiNuoviRitiri");
		verificaDatiNuoviRitiri
				.setImplementation(EmptyVerificaDatiNuoviRitiri.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", importaNuoviRitiriModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("importaNuoviRitiri");

		// VERIFICO L'ESECUZIONE DEL RECUPERO DATI NUOVI RITIRI
		List<HistoricActivityInstance> recuperoDatiNuoviRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiNuoviRitiri").list();
		assertEquals(recuperoDatiNuoviRitiriHistory.size(), 1);

		// VERIFICO L'ESECUZIONE DELLA VERIFICA DATI NUOVI RITIRI
		List<HistoricActivityInstance> verificaDatiNuoviRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("verificaDatiNuoviRitiri").list();
		assertEquals(verificaDatiNuoviRitiriHistory.size(), 1);

		// VERIFICO L'ESECUZIONE DELL'AGGIORNAMENTO NUOVI RITIRI
		List<HistoricActivityInstance> aggiornamentoNuoviRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoNuoviRitiri").list();
		assertEquals(aggiornamentoNuoviRitiriHistory.size(), 1);

		// VERIFICO CHE LE EMAIL DI ERRORE DI NUOVI RITIRI NON CORRETTI NON SONO
		// STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoAOperatoreLogisticoPerDatiNuoviRitiriNonCorretti = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"sollecitoAOperatoreLogisticoPerDatiNuoviRitiriNonCorretti")
				.list();
		assertEquals(
				sollecitoAOperatoreLogisticoPerDatiNuoviRitiriNonCorretti
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI AGGIORNAMENTO NUOVI RITIRI
		// SONO STATE
		// MANDATE
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoNuoviRitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoNuoviRitiriAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoNuoviRitiriAAmministratore
						.size(),
				1);
		List<HistoricActivityInstance> segnalazioneErroreAggiornamentoNuoviRitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreAggiornamentoNuoviRitiriAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreAggiornamentoNuoviRitiriAOperatoreLogistico
						.size(),
				1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

}
