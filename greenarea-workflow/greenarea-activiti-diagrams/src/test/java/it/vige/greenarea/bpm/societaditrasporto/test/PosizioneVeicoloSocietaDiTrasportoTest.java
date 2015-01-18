package it.vige.greenarea.bpm.societaditrasporto.test;

import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.societaditrasporto.service.posizioneveicolo.RecuperaPosizioneVeicoloConNotificaErroreReperimentoPosizione;
import it.vige.greenarea.bpm.societaditrasporto.service.posizioneveicolo.RichiediPosizioneVeicoloPopolata;

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

public class PosizioneVeicoloSocietaDiTrasportoTest extends
		ResourceActivitiTestCase {

	private final static String USER_NAME = "tnt";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public PosizioneVeicoloSocietaDiTrasportoTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/societaditrasporto/posizione_veicolo.bpmn20.xml" })
	public void testPosizioneVeicoloOK() {
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
		String targa = "targa";
		variables.put("targa", targa);
		String idMissione = "idmissione";
		variables.put("idmissione", idMissione);

		// POPOLO LE MISSIONI
		ProcessDefinition monitoringPosizioneVeicolo = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel monitoringPosizioneVeicoloModel = repositoryService
				.getBpmnModel(monitoringPosizioneVeicolo.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask monitoringPosizioneVeicoloService = (ServiceTask) monitoringPosizioneVeicoloModel
				.getFlowElement("richiediPosizioneVeicolo");
		monitoringPosizioneVeicoloService
				.setImplementation(RichiediPosizioneVeicoloPopolata.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn",
						monitoringPosizioneVeicoloModel).deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("posizioneVeicoloSt",
				variables);

		// VERIFICO CHE IL RECUPERO DELLE MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> richiediAccessi = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("richiediPosizioneVeicolo").list();
		assertEquals(richiediAccessi.size(), 1);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiestaPosizioneVeicoloAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaPosizioneVeicoloAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaPosizioneVeicoloAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreRichiestaPosizioneVeicoloAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaPosizioneVeicoloAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaPosizioneVeicoloAOperatoreLogistico
						.size(),
				0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/societaditrasporto/posizione_veicolo.bpmn20.xml" })
	public void testNotificaErroreRecuperoPosizione() {
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
		String targa = "targa";
		variables.put("targa", targa);
		String idMissione = "idmissione";
		variables.put("idmissione", idMissione);

		// AGGIUNGO UN ERRORE AL RECUPERO MISSIONI
		ProcessDefinition richiediReportPosizioneVeicolo = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel richiediReportPosizioneVeicoloModel = repositoryService
				.getBpmnModel(richiediReportPosizioneVeicolo.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediAccessiGaService = (ServiceTask) richiediReportPosizioneVeicoloModel
				.getFlowElement("richiediPosizioneVeicolo");
		richiediAccessiGaService
				.setImplementation(RecuperaPosizioneVeicoloConNotificaErroreReperimentoPosizione.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn",
						richiediReportPosizioneVeicoloModel).deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("posizioneVeicoloSt",
				variables);

		// VERIFICO CHE LE EMAIL DI RECUPERO MISSIONI SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiestaPosizioneVeicoloAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaPosizioneVeicoloAAmministratore")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaPosizioneVeicoloAAmministratore
						.size(),
				1);
		List<HistoricActivityInstance> segnalazioneErroreRichiestaPosizioneVeicoloAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaPosizioneVeicoloAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaPosizioneVeicoloAOperatoreLogistico
						.size(),
				1);

		// VERIFICO CHE NON CI SIA NESSUN TASK PRESENTE
		List<Task> taskCorrenti = taskService.createTaskQuery().list();
		assertEquals(taskCorrenti.size(), 0);

		// VERIFICO CHE IL RECUPERO DELLE MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> richiediPosizioneVeicolo = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("richiediPosizioneVeicolo").list();
		assertEquals(richiediPosizioneVeicolo.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

}
