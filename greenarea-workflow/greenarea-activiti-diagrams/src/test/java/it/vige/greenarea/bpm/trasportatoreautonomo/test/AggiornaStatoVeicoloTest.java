package it.vige.greenarea.bpm.trasportatoreautonomo.test;

import static it.vige.greenarea.dto.StatoVeicolo.DELIVERING;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.trasportatoreautonomo.aggiornastatoveicolo.EmptyAggiornaStato;
import it.vige.greenarea.bpm.trasportatoreautonomo.service.aggiornastatoveicolo.AggiornaStatoConSegnalazioneErroreAggiornaStato;
import it.vige.greenarea.bpm.trasportatoreautonomo.service.aggiornastatoveicolo.RichiediVeicoloPopolato;
import it.vige.greenarea.dto.Veicolo;

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

public class AggiornaStatoVeicoloTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "trasportatoreautonomo1";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public AggiornaStatoVeicoloTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/trasportatoreautonomo/aggiorna_stato_veicolo.bpmn20.xml" })
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

		// INSERIMENTO FORM
		Map<String, Object> variables = new HashMap<String, Object>();
		String targa = "targa";
		variables.put("targa", targa);

		// POPOLO IL VEICOLO
		ProcessDefinition aggiornaStatoVeicolo = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel aggiornaStatoVeicoloModel = repositoryService
				.getBpmnModel(aggiornaStatoVeicolo.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediVeicolo = (ServiceTask) aggiornaStatoVeicoloModel
				.getFlowElement("richiediIlVeicolo");
		richiediVeicolo.setImplementation(RichiediVeicoloPopolato.class
				.getName());
		ServiceTask aggiornaStato = (ServiceTask) aggiornaStatoVeicoloModel
				.getFlowElement("aggiornaStato");
		aggiornaStato.setImplementation(EmptyAggiornaStato.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", aggiornaStatoVeicoloModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("aggiornaStatoVeicolo",
				variables);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// PARAMETRI DEL VEICOLO
		List<Task> visualizzaParametriVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaParametriVeicolo")
				.includeProcessVariables().list();
		assertEquals(visualizzaParametriVeicoli.size(), 1);
		Task visualizzaParametriVeicolo = visualizzaParametriVeicoli.get(0);
		variables = visualizzaParametriVeicolo.getProcessVariables();
		Veicolo veicoloModel = (Veicolo) variables.get("veicolo");
		assertEquals(veicoloModel.getStato(), DELIVERING.name());
		assertEquals(veicoloModel.getTarga(), "targa1");
		assertEquals(visualizzaParametriVeicolo.getAssignee(), USER_NAME);

		// MODIFICA DEI PARAMETRI DEL VEICOLO
		veicoloModel.setStato(DELIVERING.name());
		veicoloModel.setTarga("targa_modificata_1");
		taskService.complete(visualizzaParametriVeicolo.getId(), variables);
		visualizzaParametriVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaParametriVeicolo").list();
		assertEquals(visualizzaParametriVeicoli.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/trasportatoreautonomo/aggiorna_stato_veicolo.bpmn20.xml" })
	public void testSegnalazioneErroreAggiornaStato() {
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

		// POPOLO I VEICOLI E AGGIUNGO UN ERRORE DI AGGIORNAMENTO DEL VEICOLO
		ProcessDefinition aggiornaStatoVeicolo = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel aggiornaStatoVeicoloModel = repositoryService
				.getBpmnModel(aggiornaStatoVeicolo.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediVeicolo = (ServiceTask) aggiornaStatoVeicoloModel
				.getFlowElement("richiediIlVeicolo");
		richiediVeicolo.setImplementation(RichiediVeicoloPopolato.class
				.getName());
		ServiceTask aggiornaStato = (ServiceTask) aggiornaStatoVeicoloModel
				.getFlowElement("aggiornaStato");
		aggiornaStato
				.setImplementation(AggiornaStatoConSegnalazioneErroreAggiornaStato.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", aggiornaStatoVeicoloModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("aggiornaStatoVeicolo",
				variables);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// PARAMETRI DEL VEICOLO
		List<Task> visualizzaParametriVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaParametriVeicolo")
				.includeProcessVariables().list();
		assertEquals(visualizzaParametriVeicoli.size(), 1);
		Task visualizzaParametriVeicolo = visualizzaParametriVeicoli.get(0);
		variables = visualizzaParametriVeicolo.getProcessVariables();
		Veicolo veicoloModel = (Veicolo) variables.get("veicolo");
		assertEquals(veicoloModel.getStato(), DELIVERING.name());
		assertEquals(veicoloModel.getTarga(), "targa1");
		assertEquals(visualizzaParametriVeicolo.getAssignee(), USER_NAME);

		// MODIFICA DEI PARAMETRI DEL VEICOLO
		veicoloModel.setStato(DELIVERING.name());
		veicoloModel.setTarga("targa_modificata_1");
		taskService.complete(visualizzaParametriVeicolo.getId(), variables);
		visualizzaParametriVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaParametriVeicolo").list();
		assertEquals(visualizzaParametriVeicoli.size(), 1);

		// VERIFICO CHE LE EMAIL PER IL VEICOLO 1 SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreaggiornaStatoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreaggiornaStatoAAmministratore")
				.list();
		assertEquals(segnalazioneErroreaggiornaStatoAAmministratore.size(), 1);
		List<HistoricActivityInstance> segnalazioneErroreaggiornaStatoASocietaDiTrasporto = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreaggiornaStatoATrasportatoreAutonomo")
				.list();
		assertEquals(segnalazioneErroreaggiornaStatoASocietaDiTrasporto.size(),
				1);

		// MODIFICO DI NUOVO I PARAMETRI DEL VEICOLO 1
		veicoloModel.setStato(DELIVERING.name());
		veicoloModel.setTarga("targa_modificata_2");
		taskService.complete(visualizzaParametriVeicoli.get(0).getId(),
				variables);
		visualizzaParametriVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaParametriVeicolo").list();
		assertEquals(visualizzaParametriVeicoli.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

}
