package it.vige.greenarea.bpm.societaditrasporto.test;

import static it.vige.greenarea.dto.StatoVeicolo.DELIVERING;
import static java.util.Arrays.asList;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.societaditrasporto.aggiornastatoveicoli.EmptyAggiornaStato;
import it.vige.greenarea.bpm.societaditrasporto.service.aggiornastatoveicoli.AggiornaStatoConSegnalazioneErroreAggiornaStato;
import it.vige.greenarea.bpm.societaditrasporto.service.aggiornastatoveicoli.RichiediVeicoliPopolati;
import it.vige.greenarea.dto.Veicolo;

import java.util.ArrayList;
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

public class AggiornaStatoVeicoliSocietaDiTrasportoTest extends
		ResourceActivitiTestCase {

	private final static String USER_NAME = "buscar";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public AggiornaStatoVeicoliSocietaDiTrasportoTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/societaditrasporto/aggiorna_stato_veicoli_societa_di_trasporto.bpmn20.xml" })
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

		// POPOLO I VEICOLI
		ProcessDefinition aggiornaStatoVeicoli = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel aggiornaStatoVeicoliModel = repositoryService
				.getBpmnModel(aggiornaStatoVeicoli.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediVeicoli = (ServiceTask) aggiornaStatoVeicoliModel
				.getFlowElement("richiediIVeicoli");
		richiediVeicoli.setImplementation(RichiediVeicoliPopolati.class
				.getName());
		ServiceTask aggiornaStato = (ServiceTask) aggiornaStatoVeicoliModel
				.getFlowElement("aggiornaStato");
		aggiornaStato.setImplementation(EmptyAggiornaStato.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", aggiornaStatoVeicoliModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey(
				"aggiornaStatoVeicoliSocietaDiTrasporto", variables);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO VEICOLI
		List<Task> elencoVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("elencoVeicoli").includeProcessVariables()
				.list();
		assertEquals(elencoVeicoli.size(), 1);

		// SELEZIONO 1 FILTRO
		@SuppressWarnings("unchecked")
		List<Veicolo> veicoli = (List<Veicolo>) taskService.getVariable(
				elencoVeicoli.get(0).getId(), "veicoli");
		List<Veicolo> veicoliDaSelezionare = new ArrayList<Veicolo>(
				asList(new Veicolo[] { veicoli.get(0) }));
		assertEquals(veicoli.size(), 2);
		Map<String, Object> selezioneVeicoli = new HashMap<String, Object>();
		selezioneVeicoli.put("veicoliselezionati", veicoliDaSelezionare);
		taskService.complete(elencoVeicoli.get(0).getId(), selezioneVeicoli);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// PARAMETRI DEL VEICOLO
		List<Task> visualizzaParametriVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaParametriVeicolo")
				.includeProcessVariables().list();
		assertEquals(visualizzaParametriVeicoli.size(), 1);
		Task visualizzaParametriVeicolo = visualizzaParametriVeicoli.get(0);
		variables = visualizzaParametriVeicolo.getProcessVariables();
		@SuppressWarnings("unchecked")
		Veicolo veicoloModel = ((List<Veicolo>) variables.get("veicoli"))
				.get(0);
		assertEquals(veicoloModel.getStato(), DELIVERING.name());
		assertEquals(veicoloModel.getTarga(), "targa1");
		assertEquals(visualizzaParametriVeicolo.getAssignee(), USER_NAME);

		// MODIFICA DEI PARAMETRI DEL VEICOLO 1
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

	@Deployment(resources = { "it/vige/greenarea/bpm/societaditrasporto/aggiorna_stato_veicoli_societa_di_trasporto.bpmn20.xml" })
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
		ProcessDefinition aggiornaStatoVeicoli = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel aggiornaStatoVeicoliModel = repositoryService
				.getBpmnModel(aggiornaStatoVeicoli.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediVeicoli = (ServiceTask) aggiornaStatoVeicoliModel
				.getFlowElement("richiediIVeicoli");
		richiediVeicoli.setImplementation(RichiediVeicoliPopolati.class
				.getName());
		ServiceTask aggiornaStato = (ServiceTask) aggiornaStatoVeicoliModel
				.getFlowElement("aggiornaStato");
		aggiornaStato
				.setImplementation(AggiornaStatoConSegnalazioneErroreAggiornaStato.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", aggiornaStatoVeicoliModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey(
				"aggiornaStatoVeicoliSocietaDiTrasporto", variables);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO VEICOLI
		List<Task> elencoVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("elencoVeicoli").includeProcessVariables()
				.list();
		assertEquals(elencoVeicoli.size(), 1);

		// SELEZIONO 1 FILTRO
		@SuppressWarnings("unchecked")
		List<Veicolo> veicoli = (List<Veicolo>) taskService.getVariable(
				elencoVeicoli.get(0).getId(), "veicoli");
		List<Veicolo> veicoliDaSelezionare = new ArrayList<Veicolo>(
				asList(new Veicolo[] { veicoli.get(0) }));
		assertEquals(veicoli.size(), 2);
		Map<String, Object> selezioneVeicoli = new HashMap<String, Object>();
		selezioneVeicoli.put("veicoliselezionati", veicoliDaSelezionare);
		taskService.complete(elencoVeicoli.get(0).getId(), selezioneVeicoli);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// PARAMETRI DEL VEICOLO
		List<Task> visualizzaParametriVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaParametriVeicolo")
				.includeProcessVariables().list();
		assertEquals(visualizzaParametriVeicoli.size(), 1);
		Task visualizzaParametriVeicolo = visualizzaParametriVeicoli.get(0);
		variables = visualizzaParametriVeicolo.getProcessVariables();
		@SuppressWarnings("unchecked")
		Veicolo veicoloModel = ((List<Veicolo>) variables.get("veicoli"))
				.get(0);
		assertEquals(veicoloModel.getStato(), DELIVERING.name());
		assertEquals(veicoloModel.getTarga(), "targa1");
		assertEquals(visualizzaParametriVeicolo.getAssignee(), USER_NAME);

		// MODIFICA DEI PARAMETRI DEL VEICOLO 1
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
						"segnalazioneErroreaggiornaStatoASocietaDiTrasporto")
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
