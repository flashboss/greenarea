package it.vige.greenarea.bpm.societaditrasporto.test;

import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.societaditrasporto.service.performanceveicoli.RichiediVeicoliConSegnalazioneErrore;
import it.vige.greenarea.bpm.societaditrasporto.service.performanceveicoli.RichiediVeicoliPopolati;

import java.util.Date;
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

public class PerformanceVeicoliSocietaDiTrasportoTest extends
		ResourceActivitiTestCase {

	private final static String USER_NAME = "buscar";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public PerformanceVeicoliSocietaDiTrasportoTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/societaditrasporto/performance_veicoli.bpmn20.xml" })
	public void testPerformanceMissioniOK() {
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
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);

		// POPOLO LE MISSIONI
		ProcessDefinition performanceMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel performanceMissioniModel = repositoryService
				.getBpmnModel(performanceMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediMissioni = (ServiceTask) performanceMissioniModel
				.getFlowElement("richiediVeicoli");
		richiediMissioni.setImplementation(RichiediVeicoliPopolati.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", performanceMissioniModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("performanceVeicoliSt",
				variables);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO VEICOLI
		List<Task> elencoVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("elencoVeicoli").includeProcessVariables()
				.list();
		assertEquals(elencoVeicoli.size(), 1);
		taskService.complete(elencoVeicoli.get(0).getId());

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiediVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreRichiediVeicoliAAmministratore")
				.list();
		assertEquals(segnalazioneErroreRichiediVeicoliAAmministratore.size(),
				0);
		List<HistoricActivityInstance> segnalazioneErroreRichiediVeicoliASocietaDiTrasporto = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiediVeicoliASocietaDiTrasporto")
				.list();
		assertEquals(
				segnalazioneErroreRichiediVeicoliASocietaDiTrasporto.size(), 0);

		// VERIFICO CHE IL RECUPERO MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDatiMissioni = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("richiediVeicoli").list();
		assertEquals(recuperoDatiMissioni.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/societaditrasporto/performance_veicoli.bpmn20.xml" })
	public void testSegnalazioneErroreRichiediVeicoli() {
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
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);

		// AGGIUNGO UN ERRORE AL RECUPERO MISSIONI
		ProcessDefinition performanceMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel performanceMissioniModel = repositoryService
				.getBpmnModel(performanceMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoDatiMissioniService = (ServiceTask) performanceMissioniModel
				.getFlowElement("richiediVeicoli");
		recuperoDatiMissioniService
				.setImplementation(RichiediVeicoliConSegnalazioneErrore.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", performanceMissioniModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("performanceVeicoliSt",
				variables);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO MISSIONI
		List<Task> elencoVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("elencoVeicoli").includeProcessVariables()
				.list();
		assertEquals(elencoVeicoli.size(), 0);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiediVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreRichiediVeicoliAAmministratore")
				.list();
		assertEquals(segnalazioneErroreRichiediVeicoliAAmministratore.size(),
				1);
		List<HistoricActivityInstance> segnalazioneErroreRichiediVeicoliASocietaDiTrasporto = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiediVeicoliASocietaDiTrasporto")
				.list();
		assertEquals(
				segnalazioneErroreRichiediVeicoliASocietaDiTrasporto.size(), 1);

		// VERIFICO CHE IL RECUPERO MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDatiMissioni = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("richiediVeicoli").list();
		assertEquals(recuperoDatiMissioni.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

}
