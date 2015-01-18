package it.vige.greenarea.bpm.operatorelogistico.test;

import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.operatorelogistico.service.monitoringmissioni.RecuperaMissioniConNotificaErroreReperimentoMissioni;
import it.vige.greenarea.bpm.operatorelogistico.service.monitoringmissioni.RichiediMissionePopolata;

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

public class MonitoringMissioniOperatoreLogisticoTest extends
		ResourceActivitiTestCase {

	private final static String USER_NAME = "tnt";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public MonitoringMissioniOperatoreLogisticoTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/operatorelogistico/monitoring_missioni.bpmn20.xml" })
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
		runtimeService.startProcessInstanceByKey("monitoringMissioniOP",
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
		List<HistoricActivityInstance> segnalazioneErroreRichiestaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaMissioneAOperatoreLogistico")
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

	@Deployment(resources = { "it/vige/greenarea/bpm/operatorelogistico/monitoring_missioni.bpmn20.xml" })
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
		runtimeService.startProcessInstanceByKey("monitoringMissioniOP",
				variables);

		// VERIFICO CHE LE EMAIL DI RECUPERO MISSIONI SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiestaMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaMissioneAAmministratore")
				.list();
		assertEquals(segnalazioneErroreRichiestaMissioneAAmministratore.size(),
				1);
		List<HistoricActivityInstance> segnalazioneErroreRichiestaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaMissioneAOperatoreLogistico.size(),
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
