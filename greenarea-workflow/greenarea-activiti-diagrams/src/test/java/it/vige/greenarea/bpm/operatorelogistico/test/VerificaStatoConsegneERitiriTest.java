package it.vige.greenarea.bpm.operatorelogistico.test;

import static it.vige.greenarea.Constants.OPERAZIONE;
import static it.vige.greenarea.dto.Operazione.CHIUDI;
import static it.vige.greenarea.dto.Operazione.DETTAGLIO;
import static it.vige.greenarea.dto.Operazione.ELENCO;
import static it.vige.greenarea.dto.Operazione.SIMULAZIONE;
import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static java.util.Arrays.asList;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.operatorelogistico.service.verificastatoconsegneeritiri.RichiediStatiConSegnalazioneErroreRichiestaStati;
import it.vige.greenarea.bpm.operatorelogistico.service.verificastatoconsegneeritiri.RichiediStatiPopolati;
import it.vige.greenarea.bpm.operatorelogistico.service.verificastatoconsegneeritiri.SimulazioneConErrore;
import it.vige.greenarea.bpm.operatorelogistico.service.verificastatoconsegneeritiri.SimulazionePopolata;
import it.vige.greenarea.dto.Missione;

import java.util.ArrayList;
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

public class VerificaStatoConsegneERitiriTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "tnt";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public VerificaStatoConsegneERitiriTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/operatorelogistico/verifica_stato_consegne_e_ritiri.bpmn20.xml" })
	public void testDettaglioOK() {
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
		String ga = "ga";
		variables.put("ga", ga);
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);
		String tipoRichiesta = CONSEGNA.name();
		variables.put("tipoRichiesta", tipoRichiesta);

		// POPOLO GLI STATI
		ProcessDefinition verificaStatoConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel verificaStatoConsegneERitiriModel = repositoryService
				.getBpmnModel(verificaStatoConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediStati = (ServiceTask) verificaStatoConsegneERitiriModel
				.getFlowElement("richiediGliStati");
		richiediStati.setImplementation(RichiediStatiPopolati.class.getName());
		ServiceTask simulazione = (ServiceTask) verificaStatoConsegneERitiriModel
				.getFlowElement("simulazione");
		simulazione.setImplementation(SimulazionePopolata.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn",
						verificaStatoConsegneERitiriModel).deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey(
				"verificaStatoConsegneERitiri", variables);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI ELENCO
		// STATI
		List<Task> visualizzaElenco = taskService.createTaskQuery()
				.taskDefinitionKey("elencoStati").includeProcessVariables()
				.list();
		assertEquals(visualizzaElenco.size(), 1);

		// ESEGUO LA SIMULAZIONE
		variables = new HashMap<String, Object>();
		variables.put(OPERAZIONE, SIMULAZIONE.name());
		taskService.complete(visualizzaElenco.get(0).getId(), variables);
		List<Task> elencoMissioni = taskService.createTaskQuery()
				.includeProcessVariables().taskDefinitionKey("elencoMissioni")
				.list();
		assertEquals(elencoMissioni.size(), 1);

		// SELEZIONO UNA MISSIONE
		@SuppressWarnings("unchecked")
		List<Missione> missioni = (List<Missione>) taskService.getVariable(
				elencoMissioni.get(0).getId(), "missioni");
		List<Missione> missioniDaSelezionare = new ArrayList<Missione>(
				asList(new Missione[] { missioni.get(0) }));
		assertEquals(missioni.size(), 2);
		Map<String, Object> selezioneMissioni = new HashMap<String, Object>();
		selezioneMissioni.put("missioniselezionate", missioniDaSelezionare);
		selezioneMissioni.put(OPERAZIONE, DETTAGLIO.name());
		taskService.complete(elencoMissioni.get(0).getId(), selezioneMissioni);
		List<Task> visualizzaMissioniRichieste = taskService.createTaskQuery()
				.includeProcessVariables()
				.taskDefinitionKey("missioneRichiesta").list();
		assertEquals(visualizzaMissioniRichieste.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VERIFICA
		// STATO
		Task visualizzaMissioneRichiesta = visualizzaMissioniRichieste.get(0);
		variables = visualizzaMissioneRichiesta.getProcessVariables();
		@SuppressWarnings("unchecked")
		Missione missioneDaDetaglio = ((List<Missione>) variables
				.get("missioni")).get(0);
		assertEquals(missioneDaDetaglio.getNome(), "missione1");
		assertEquals(visualizzaMissioneRichiesta.getAssignee(), USER_NAME);

		// RITORNO ALL'ELENCO
		variables = new HashMap<String, Object>();
		variables.put(OPERAZIONE, ELENCO.name());
		taskService.complete(visualizzaMissioneRichiesta.getId(), variables);
		visualizzaMissioniRichieste = taskService.createTaskQuery()
				.taskDefinitionKey("elencoStati").list();
		assertEquals(visualizzaMissioniRichieste.size(), 0);

		// CHIUDO IL TASK
		visualizzaMissioniRichieste = taskService.createTaskQuery()
				.includeProcessVariables().taskDefinitionKey("elencoMissioni")
				.list();
		assertEquals(visualizzaMissioniRichieste.size(), 1);
		variables = new HashMap<String, Object>();
		variables.put(OPERAZIONE, CHIUDI.name());
		taskService.complete(visualizzaMissioniRichieste.get(0).getId(),
				variables);

		// VERIFICO CHE LE EMAIL PER LO STATO NON SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiestaStatiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreRichiestaStatiAAmministratore")
				.list();
		assertEquals(segnalazioneErroreRichiestaStatiAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreRichiestaStatiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaStatiAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaStatiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE IL RECUPERO DEGLI STATI E' STATO ESEGUITO
		List<HistoricActivityInstance> richiediGliStati = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("richiediGliStati").list();
		assertEquals(richiediGliStati.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/operatorelogistico/verifica_stato_consegne_e_ritiri.bpmn20.xml" })
	public void testNoDettaglioOK() {
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
		String ga = "ga";
		variables.put("ga", ga);
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);
		String tiporichiesta = "tiporichiesta";
		variables.put("tiporichiesta", tiporichiesta);

		// POPOLO GLI STATI
		ProcessDefinition verificaStatoConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel verificaStatoConsegneERitiriModel = repositoryService
				.getBpmnModel(verificaStatoConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediStati = (ServiceTask) verificaStatoConsegneERitiriModel
				.getFlowElement("richiediGliStati");
		richiediStati.setImplementation(RichiediStatiPopolati.class.getName());
		ServiceTask simulazione = (ServiceTask) verificaStatoConsegneERitiriModel
				.getFlowElement("simulazione");
		simulazione.setImplementation(SimulazionePopolata.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn",
						verificaStatoConsegneERitiriModel).deploy();

		// INIZIO PROCESSO
		variables = new HashMap<String, Object>();
		variables.put(OPERAZIONE, CHIUDI.name());
		runtimeService.startProcessInstanceByKey(
				"verificaStatoConsegneERitiri", variables);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// STATO RICHIESTO
		List<Task> visualizzaStatiRichiesti = taskService.createTaskQuery()
				.taskDefinitionKey("elencoStati").includeProcessVariables()
				.list();
		assertEquals(visualizzaStatiRichiesti.size(), 1);

		// CHIUDO L'ELENCO
		taskService
				.complete(visualizzaStatiRichiesti.get(0).getId(), variables);

		// VERIFICO CHE LE EMAIL PER LO STATO NON SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiestaStatiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreRichiestaStatiAAmministratore")
				.list();
		assertEquals(segnalazioneErroreRichiestaStatiAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreRichiestaStatiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaStatiAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaStatiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE IL RECUPERO DEGLI STATI E' STATO ESEGUITO
		List<HistoricActivityInstance> richiediGliStati = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("richiediGliStati").list();
		assertEquals(richiediGliStati.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/operatorelogistico/verifica_stato_consegne_e_ritiri.bpmn20.xml" })
	public void testSegnalazioneRichiestaStati() {
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
		String ga = "ga";
		variables.put("ga", ga);
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);
		String tiporichiesta = "tiporichiesta";
		variables.put("tiporichiesta", tiporichiesta);

		// AGGIUNGO UN ERRORE DI RICHIESTA DEGLI STATI
		ProcessDefinition verificaStatoConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel verificaStatoConsegneERitiriModel = repositoryService
				.getBpmnModel(verificaStatoConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediStati = (ServiceTask) verificaStatoConsegneERitiriModel
				.getFlowElement("richiediGliStati");
		richiediStati
				.setImplementation(RichiediStatiConSegnalazioneErroreRichiestaStati.class
						.getName());
		ServiceTask simulazione = (ServiceTask) verificaStatoConsegneERitiriModel
				.getFlowElement("simulazione");
		simulazione.setImplementation(SimulazionePopolata.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn",
						verificaStatoConsegneERitiriModel).deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey(
				"verificaStatoConsegneERitiri", variables);

		// VERIFICO CHE LE EMAIL PER LO STATO SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiestaStatiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreRichiestaStatiAAmministratore")
				.list();
		assertEquals(segnalazioneErroreRichiestaStatiAAmministratore.size(), 1);
		List<HistoricActivityInstance> segnalazioneErroreRichiestaStatiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaStatiAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaStatiAOperatoreLogistico.size(), 1);

		// VERIFICO CHE IL RECUPERO DEGLI STATI E' STATO ESEGUITO
		List<HistoricActivityInstance> richiediGliStati = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("richiediGliStati").list();
		assertEquals(richiediGliStati.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/operatorelogistico/verifica_stato_consegne_e_ritiri.bpmn20.xml" })
	public void testErroreSimulazione() {
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
		String ga = "ga";
		variables.put("ga", ga);
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);
		String tipoRichiesta = CONSEGNA.name();
		variables.put("tipoRichiesta", tipoRichiesta);

		// POPOLO GLI STATI
		ProcessDefinition verificaStatoConsegneERitiri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel verificaStatoConsegneERitiriModel = repositoryService
				.getBpmnModel(verificaStatoConsegneERitiri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask richiediStati = (ServiceTask) verificaStatoConsegneERitiriModel
				.getFlowElement("richiediGliStati");
		richiediStati.setImplementation(RichiediStatiPopolati.class.getName());
		ServiceTask simulazione = (ServiceTask) verificaStatoConsegneERitiriModel
				.getFlowElement("simulazione");
		simulazione.setImplementation(SimulazioneConErrore.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService
				.createDeployment()
				.addBpmnModel("dynamic-model.bpmn",
						verificaStatoConsegneERitiriModel).deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey(
				"verificaStatoConsegneERitiri", variables);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI ELENCO
		// STATI
		List<Task> visualizzaElenco = taskService.createTaskQuery()
				.taskDefinitionKey("elencoStati").includeProcessVariables()
				.list();
		assertEquals(visualizzaElenco.size(), 1);

		// ESEGUO LA SIMULAZIONE
		variables = new HashMap<String, Object>();
		variables.put(OPERAZIONE, SIMULAZIONE.name());
		taskService.complete(visualizzaElenco.get(0).getId(), variables);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI ELENCO
		// STATI
		List<Task> visualizzaSecondoElenco = taskService.createTaskQuery()
				.taskDefinitionKey("elencoStati").includeProcessVariables()
				.list();
		assertEquals(visualizzaSecondoElenco.size(), 1);

		// RIESEGUO LA SIMULAZIONE
		variables = new HashMap<String, Object>();
		variables.put(OPERAZIONE, SIMULAZIONE.name());
		taskService.complete(visualizzaSecondoElenco.get(0).getId(), variables);

		List<Task> elencoMissioni = taskService.createTaskQuery()
				.includeProcessVariables().taskDefinitionKey("elencoMissioni")
				.list();
		assertEquals(elencoMissioni.size(), 1);

		// SELEZIONO UNA MISSIONE
		@SuppressWarnings("unchecked")
		List<Missione> missioni = (List<Missione>) taskService.getVariable(
				elencoMissioni.get(0).getId(), "missioni");
		List<Missione> missioniDaSelezionare = new ArrayList<Missione>(
				asList(new Missione[] { missioni.get(0) }));
		assertEquals(missioni.size(), 2);
		Map<String, Object> selezioneMissioni = new HashMap<String, Object>();
		selezioneMissioni.put("missioniselezionate", missioniDaSelezionare);
		selezioneMissioni.put(OPERAZIONE, DETTAGLIO.name());
		taskService.complete(elencoMissioni.get(0).getId(), selezioneMissioni);
		List<Task> visualizzaMissioniRichieste = taskService.createTaskQuery()
				.includeProcessVariables()
				.taskDefinitionKey("missioneRichiesta").list();
		assertEquals(visualizzaMissioniRichieste.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VERIFICA
		// STATO
		Task visualizzaMissioneRichiesta = visualizzaMissioniRichieste.get(0);
		variables = visualizzaMissioneRichiesta.getProcessVariables();
		@SuppressWarnings("unchecked")
		Missione missioneDaDetaglio = ((List<Missione>) variables
				.get("missioni")).get(0);
		assertEquals(missioneDaDetaglio.getNome(), "missione1");
		assertEquals(visualizzaMissioneRichiesta.getAssignee(), USER_NAME);

		// RITORNO ALL'ELENCO
		variables = new HashMap<String, Object>();
		variables.put(OPERAZIONE, ELENCO.name());
		taskService.complete(visualizzaMissioneRichiesta.getId(), variables);
		visualizzaMissioniRichieste = taskService.createTaskQuery()
				.taskDefinitionKey("elencoStati").list();
		assertEquals(visualizzaMissioniRichieste.size(), 0);

		// CHIUDO IL TASK
		visualizzaMissioniRichieste = taskService.createTaskQuery()
				.includeProcessVariables().taskDefinitionKey("elencoMissioni")
				.list();
		assertEquals(visualizzaMissioniRichieste.size(), 1);
		variables = new HashMap<String, Object>();
		variables.put(OPERAZIONE, CHIUDI.name());
		taskService.complete(visualizzaMissioniRichieste.get(0).getId(),
				variables);

		// VERIFICO CHE LE EMAIL PER LO STATO NON SONO STATE MANDATE
		List<HistoricActivityInstance> segnalazioneErroreRichiestaStatiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("segnalazioneErroreRichiestaStatiAAmministratore")
				.list();
		assertEquals(segnalazioneErroreRichiestaStatiAAmministratore.size(), 0);
		List<HistoricActivityInstance> segnalazioneErroreRichiestaStatiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"segnalazioneErroreRichiestaStatiAOperatoreLogistico")
				.list();
		assertEquals(
				segnalazioneErroreRichiestaStatiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE IL RECUPERO DEGLI STATI E' STATO ESEGUITO
		List<HistoricActivityInstance> richiediGliStati = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("richiediGliStati").list();
		assertEquals(richiediGliStati.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

}
