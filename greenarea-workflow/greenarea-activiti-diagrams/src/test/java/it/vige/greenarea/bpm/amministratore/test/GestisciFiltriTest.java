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
package it.vige.greenarea.bpm.amministratore.test;

import static java.util.Arrays.asList;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.amministratore.gestiscifiltri.EmptyCancellaFiltro;
import it.vige.greenarea.bpm.amministratore.service.gestiscifiltri.CancellaFiltroConNotificaErrore;
import it.vige.greenarea.bpm.amministratore.service.gestiscifiltri.InserisciFiltroConNotificaErrore;
import it.vige.greenarea.bpm.amministratore.service.gestiscifiltri.RecuperaFiltriConNotificaErroreGrave;
import it.vige.greenarea.bpm.amministratore.service.gestiscifiltri.RecuperaFiltriConNotificaErroreLieve;
import it.vige.greenarea.bpm.amministratore.service.gestiscifiltri.RecuperaFiltriPopolati;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.pa.gestisciparametri.EmptyInserisciParametro;
import it.vige.greenarea.dto.Filtro;

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

public class GestisciFiltriTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "amministratore";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public GestisciFiltriTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/amministratore/impostazione_filtri.bpmn20.xml" })
	public void testImpostazioneOK() {
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
		String nome = "nome";
		variables.put("nome", nome);

		// IL TEST NON PREVEDE L'INJECTION. RESETTO I SERVIZI
		ProcessDefinition gestisciParametri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel gestisciParametriModel = repositoryService
				.getBpmnModel(gestisciParametri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask inserisciFiltroService = (ServiceTask) gestisciParametriModel
				.getFlowElement("inserisciFiltroService");
		inserisciFiltroService.setImplementation(EmptyInserisciParametro.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestisciParametriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("impostazioneFiltri",
				variables);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreInserimentoFiltroAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreInserimentoFiltroAAmministratore")
				.list();
		assertEquals(notificaErroreInserimentoFiltroAAmministratore.size(), 0);

		// VERIFICO CHE L'INSERIMENTO E' STATO ESEGUITO
		List<HistoricActivityInstance> inserisciFiltroServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("inserisciFiltroService").list();
		assertEquals(inserisciFiltroServiceHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/amministratore/variazione_filtri.bpmn20.xml" })
	public void testVariazioneOK() {
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

		// POPOLO I FILTRI
		ProcessDefinition variazioneFiltri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel variazioneFiltriModel = repositoryService
				.getBpmnModel(variazioneFiltri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoFiltriService = (ServiceTask) variazioneFiltriModel
				.getFlowElement("recuperoFiltriService");
		recuperoFiltriService.setImplementation(RecuperaFiltriPopolati.class
				.getName());
		ServiceTask cancellaFiltroService = (ServiceTask) variazioneFiltriModel
				.getFlowElement("cancellaFiltroService");
		cancellaFiltroService.setImplementation(EmptyCancellaFiltro.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", variazioneFiltriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("variazioneFiltri");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FILTRI
		List<Task> elencoFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFiltri").includeProcessVariables()
				.list();
		assertEquals(elencoFiltri.size(), 1);

		// SELEZIONO 1 FILTRO
		@SuppressWarnings("unchecked")
		List<Filtro> filtri = (List<Filtro>) taskService.getVariable(
				elencoFiltri.get(0).getId(), "filtri");
		List<Filtro> filtriDaSelezionare = new ArrayList<Filtro>(
				asList(new Filtro[] { filtri.get(0) }));
		assertEquals(filtri.size(), 3);
		Map<String, Object> selezioneFiltri = new HashMap<String, Object>();
		selezioneFiltri.put("filtriselezionati", filtriDaSelezionare);
		taskService.complete(elencoFiltri.get(0).getId(), selezioneFiltri);

		// VERIFICO LA CREAZIONE DEI TASK DI VISUALIZZAZIONE
		List<Task> visualizzaFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaFiltro")
				.includeProcessVariables().list();
		assertEquals(visualizzaFiltri.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI GESTIONE
		// DEL FILTRO 1
		Task visualizzaFiltro = visualizzaFiltri.get(0);
		Map<String, Object> variables = visualizzaFiltro.getProcessVariables();
		@SuppressWarnings("unchecked")
		List<Filtro> filtriOttenuti = (List<Filtro>) variables
				.get("filtriselezionati");
		Filtro filtro1 = filtriOttenuti.get(0);
		assertEquals(filtro1.getRoundCode(), "01");
		assertEquals(filtro1.getOperatoreLogistico(), "tnt");
		assertEquals(visualizzaFiltro.getAssignee(), USER_NAME);

		// VERIFICO CHE LE EMAIL DI ERRORE RECUPERO NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoFiltriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoFiltriAAmministratore")
				.list();
		assertEquals(notificaErroreRecuperoFiltriAAmministratore.size(), 0);

		// CANCELLAZIONE DEL FILTRO 1
		visualizzaFiltro = visualizzaFiltri.get(0);
		taskService.complete(visualizzaFiltro.getId());

		// VERIFICO CHE LA GESTIONE SCOMPARE DOPO LA CANCELLAZIONE
		visualizzaFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaFiltro").list();
		assertEquals(visualizzaFiltri.size(), 0);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreCancellazioneFiltroAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneFiltroAAmministratore")
				.list();
		assertEquals(notificaErroreCancellazioneFiltroAAmministratore.size(), 0);

		// VERIFICO CHE IL RECUPERO DEI FILTRI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoFiltriServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoFiltriService").list();
		assertEquals(recuperoFiltriServiceHistory.size(), 1);

		// VERIFICO CHE LE CANCELLAZIONI DEI FILTRI SONO STATE ESEGUITE
		List<HistoricActivityInstance> cancellaFiltroServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("cancellaFiltroService").list();
		assertEquals(cancellaFiltroServiceHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/amministratore/variazione_filtri.bpmn20.xml" })
	public void testNotificaErroreLetturaGrave() {
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

		// AGGIUNGO UN ERRORE AL RECUPERO DATI
		ProcessDefinition variazioneFiltri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel variazioneFiltriModel = repositoryService
				.getBpmnModel(variazioneFiltri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoFiltriService = (ServiceTask) variazioneFiltriModel
				.getFlowElement("recuperoFiltriService");
		recuperoFiltriService
				.setImplementation(RecuperaFiltriConNotificaErroreGrave.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", variazioneFiltriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("variazioneFiltri");

		// VERIFICO CHE NON SIA CREATO IL TASK DI ELENCO FILTRI
		List<Task> elencoFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFiltri").includeProcessVariables()
				.list();
		assertEquals(elencoFiltri.size(), 0);

		// VERIFICO CHE NON SIANO CREATI I TASK DI VISUALIZZAZIONE
		List<Task> cancellaFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaFiltro")
				.includeProcessVariables().list();
		assertEquals(cancellaFiltri.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoFiltriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoFiltriAAmministratore")
				.list();
		assertEquals(notificaErroreRecuperoFiltriAAmministratore.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/amministratore/variazione_filtri.bpmn20.xml" })
	public void testNotificaErroreLetturaLieve() {
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

		// AGGIUNGO UN ERRORE AL RECUPERO DATI
		ProcessDefinition variazioneFiltri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel variazioneFiltriModel = repositoryService
				.getBpmnModel(variazioneFiltri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoFiltriService = (ServiceTask) variazioneFiltriModel
				.getFlowElement("recuperoFiltriService");
		recuperoFiltriService
				.setImplementation(RecuperaFiltriConNotificaErroreLieve.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", variazioneFiltriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("variazioneFiltri");

		// VERIFICO CHE NON SIA CREATO IL TASK DI ELENCO FILTRI
		List<Task> elencoFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFiltri").includeProcessVariables()
				.list();
		assertEquals(elencoFiltri.size(), 0);

		// VERIFICO CHE NON SIANO CREATI I TASK DI VISUALIZZAZIONE
		List<Task> cancellaFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaFiltro")
				.includeProcessVariables().list();
		assertEquals(cancellaFiltri.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoFiltriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoFiltriAAmministratore")
				.list();
		assertEquals(notificaErroreRecuperoFiltriAAmministratore.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/amministratore/impostazione_filtri.bpmn20.xml" })
	public void testNotificaErroreInserimento() {
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
		String filtro = "01";
		variables.put("filtro", filtro);

		// AGGIUNGO UN ERRORE ALL'INSERIMENTO
		ProcessDefinition impostazioneFiltri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel impostazioneFiltriModel = repositoryService
				.getBpmnModel(impostazioneFiltri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask inserisciFiltroService = (ServiceTask) impostazioneFiltriModel
				.getFlowElement("inserisciFiltroService");
		inserisciFiltroService
				.setImplementation(InserisciFiltroConNotificaErrore.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", impostazioneFiltriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("impostazioneFiltri",
				variables);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreInserimentoFiltroAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreInserimentoFiltroAAmministratore")
				.list();
		assertEquals(notificaErroreInserimentoFiltroAAmministratore.size(), 1);

		// VERIFICO CHE IL SERVIZIO DI INSERIMENTO SIA STATO ESEGUITO 1 VOLTA
		List<HistoricActivityInstance> inserisciFiltroServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("inserisciFiltroService").list();
		assertEquals(inserisciFiltroServiceHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/amministratore/variazione_filtri.bpmn20.xml" })
	public void testNotificaErroreCancellazione() {
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

		// POPOLO LE FILTRI E AGGIUNGO UN ERRORE ALLA CANCELLAZIONE
		ProcessDefinition variazioneFiltriDefinition = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel variazioneFiltriModel = repositoryService
				.getBpmnModel(variazioneFiltriDefinition.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoFiltriService = (ServiceTask) variazioneFiltriModel
				.getFlowElement("recuperoFiltriService");
		recuperoFiltriService.setImplementation(RecuperaFiltriPopolati.class
				.getName());
		ServiceTask cancellaFiltroService = (ServiceTask) variazioneFiltriModel
				.getFlowElement("cancellaFiltroService");
		cancellaFiltroService
				.setImplementation(CancellaFiltroConNotificaErrore.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", variazioneFiltriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("variazioneFiltri");

		// VERIFICO CHE IL RECUPERO DEI FILTRI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoFiltriServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoFiltriService").list();
		assertEquals(recuperoFiltriServiceHistory.size(), 1);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FILTRI
		List<Task> elencoFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFiltri").includeProcessVariables()
				.list();
		assertEquals(elencoFiltri.size(), 1);

		// SELEZIONO 1 FILTRO
		@SuppressWarnings("unchecked")
		List<Filtro> filtri = (List<Filtro>) taskService.getVariable(
				elencoFiltri.get(0).getId(), "filtri");
		List<Filtro> filtriDaSelezionare = new ArrayList<Filtro>(
				asList(new Filtro[] { filtri.get(0) }));
		assertEquals(filtri.size(), 3);
		Map<String, Object> selezioneFiltri = new HashMap<String, Object>();
		selezioneFiltri.put("filtriselezionati", filtriDaSelezionare);
		taskService.complete(elencoFiltri.get(0).getId(), selezioneFiltri);

		// VERIFICO LA CREAZIONE DEI TASK DI VISUALIZZAZIONE
		List<Task> visualizzaFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaFiltro")
				.includeProcessVariables().list();
		assertEquals(visualizzaFiltri.size(), 1);

		// VERIFICO CHE SIANO PRESENTI I TASK DI CANCELLAZIONE
		// DEL FILTRO
		List<Task> cancellaFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaFiltro")
				.includeProcessVariables().list();
		assertEquals(cancellaFiltri.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// DEL FILTRO 1
		Task cancellaFiltro = cancellaFiltri.get(0);
		Map<String, Object> variables = cancellaFiltro.getProcessVariables();
		@SuppressWarnings("unchecked")
		List<Filtro> filtriOttenuti = (List<Filtro>) variables.get("filtri");
		Filtro filtro1 = filtriOttenuti.get(0);
		assertEquals(filtro1.getRoundCode(), "01");
		assertEquals(filtro1.getOperatoreLogistico(), "tnt");
		assertEquals(cancellaFiltro.getAssignee(), USER_NAME);

		// CANCELLAZIONE DEL FILTRO 1
		taskService.complete(cancellaFiltro.getId());
		cancellaFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaFiltro").list();
		assertEquals(cancellaFiltri.size(), 0);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreCancellazioneFiltroAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneFiltroAAmministratore")
				.list();
		assertEquals(notificaErroreCancellazioneFiltroAAmministratore.size(), 1);

		// VERIFICO CHE NON SIANO PRESENTI I TASK DI CANCELLAZIONE
		// DEL FILTRO
		cancellaFiltri = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaFiltro")
				.includeProcessVariables().list();
		assertEquals(cancellaFiltri.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}
}
