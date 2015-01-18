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
package it.vige.greenarea.bpm.pa.test;

import static it.vige.greenarea.Constants.OPERAZIONE;
import static it.vige.greenarea.dto.Operazione.AGGIUNGI;
import static it.vige.greenarea.dto.Operazione.CANCELLAZIONE;
import static it.vige.greenarea.dto.Operazione.MODIFICA;
import static java.util.Arrays.asList;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.pa.gestisciparametri.EmptyCancellaParametro;
import it.vige.greenarea.bpm.pa.gestisciparametri.EmptyInserisciParametro;
import it.vige.greenarea.bpm.pa.gestisciparametri.EmptyModificaParametro;
import it.vige.greenarea.bpm.pa.service.gestisciparametri.CancellaParametroConNotificaErroreCancellazione;
import it.vige.greenarea.bpm.pa.service.gestisciparametri.InserisciParametroConNotificaErroreInserimentoGrave;
import it.vige.greenarea.bpm.pa.service.gestisciparametri.InserisciParametroConNotificaErroreInserimentoLieve;
import it.vige.greenarea.bpm.pa.service.gestisciparametri.ModificaParametroConNotificaErroreModifica;
import it.vige.greenarea.bpm.pa.service.gestisciparametri.RecuperaParametriConNotificaErroreRecuperoGrave;
import it.vige.greenarea.bpm.pa.service.gestisciparametri.RecuperaParametriConNotificaErroreRecuperoLieve;
import it.vige.greenarea.bpm.pa.service.gestisciparametri.RecuperaParametriPopolati;
import it.vige.greenarea.dto.Parametro;

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

public class GestisciParametriTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "patorino";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public GestisciParametriTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_parametri.bpmn20.xml",
			"it/vige/greenarea/bpm/pa/inserisci_parametri.bpmn20.xml" })
	public void testInserimentoOK() {
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

		// IL TEST NON PREVEDE L'INJECTION. RESETTO I SERVIZI
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition letturaParametri = processiAttivi.get(1);
		ProcessDefinition inserimentoParametri = processiAttivi.get(0);
		BpmnModel letturaParametriModel = repositoryService
				.getBpmnModel(letturaParametri.getId());
		BpmnModel inserimentoParametriModel = repositoryService
				.getBpmnModel(inserimentoParametri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask inserimentoParametroService = (ServiceTask) inserimentoParametriModel
				.getFlowElement("inserimentoParametroService");
		inserimentoParametroService
				.setImplementation(EmptyInserisciParametro.class.getName());
		ServiceTask recuperoParametriService = (ServiceTask) letturaParametriModel
				.getFlowElement("visualizzaParametriService");
		recuperoParametriService
				.setImplementation(RecuperaParametriPopolati.class.getName());
		ServiceTask modificaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("modificaParametroService");
		modificaParametroService.setImplementation(EmptyModificaParametro.class
				.getName());
		ServiceTask cancellaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("cancellaParametroService");
		cancellaParametroService.setImplementation(EmptyCancellaParametro.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaParametriModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", inserimentoParametriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaParametri");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametri").includeProcessVariables()
				.list();
		assertEquals(elencoParametri.size(), 1);

		// ESEGUO L'INSERIMENTO
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put(OPERAZIONE, AGGIUNGI.name());
		taskService.complete(elencoParametri.get(0).getId(), operazione);

		// VERIFICO LA CREAZIONE DEL TASK DI COMPILAZIONE PARAMETRO
		List<Task> compilazioneParametro = taskService.createTaskQuery()
				.taskDefinitionKey("compilaParametro")
				.includeProcessVariables().list();
		assertEquals(compilazioneParametro.size(), 1);

		// INSERIMENTO FORM
		Map<String, Object> variables = new HashMap<String, Object>();
		String nome = "nome";
		variables.put("nome", nome);
		String descrizione = "descrizione";
		variables.put("descrizione", descrizione);
		String unitamisura = "unitamisura";
		variables.put("unitamisura", unitamisura);
		String statoattivazione = "statoattivazione";
		variables.put("statoattivazione", statoattivazione);
		taskService.complete(compilazioneParametro.get(0).getId(), variables);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreInserimentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreInserimentoAAmministratore").list();
		assertEquals(notificaErroreInserimentoAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreInserimentoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreInserimentoAPA").list();
		assertEquals(notificaErroreInserimentoAPA.size(), 0);

		// VERIFICO CHE L'INSERIMENTO E' STATO ESEGUITO
		List<HistoricActivityInstance> inserimentoParametroServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("inserimentoParametroService").list();
		assertEquals(inserimentoParametroServiceHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_parametri.bpmn20.xml" })
	public void testLetturaEModificaOK() {
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

		// POPOLO I PARAMETRI
		ProcessDefinition letturaParametri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaParametriModel = repositoryService
				.getBpmnModel(letturaParametri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoParametriService = (ServiceTask) letturaParametriModel
				.getFlowElement("visualizzaParametriService");
		recuperoParametriService
				.setImplementation(RecuperaParametriPopolati.class.getName());
		ServiceTask modificaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("modificaParametroService");
		modificaParametroService.setImplementation(EmptyModificaParametro.class
				.getName());
		ServiceTask cancellaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("cancellaParametroService");
		cancellaParametroService.setImplementation(EmptyCancellaParametro.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaParametriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaParametri");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametri").includeProcessVariables()
				.list();
		assertEquals(elencoParametri.size(), 1);

		// SELEZIONO 1 PARAMETRO
		@SuppressWarnings("unchecked")
		List<Parametro> parametri = (List<Parametro>) taskService.getVariable(
				elencoParametri.get(0).getId(), "parametri");
		List<Parametro> parametriDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametri.get(0) }));
		assertEquals(parametri.size(), 2);
		Map<String, Object> selezioneParametri = new HashMap<String, Object>();
		selezioneParametri.put("parametriselezionati", parametriDaSelezionare);
		selezioneParametri.put(OPERAZIONE, MODIFICA.name());
		taskService
				.complete(elencoParametri.get(0).getId(), selezioneParametri);

		// VERIFICO LA CREAZIONE DEI TASK DI GESTIONE
		List<Task> gestioneParametri = taskService.createTaskQuery()
				.taskDefinitionKey("gestisciParametro")
				.includeProcessVariables().list();
		assertEquals(gestioneParametri.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI GESTIONE
		// DEL PARAMETRO 1
		Task gestioneParametro = gestioneParametri.get(0);
		Map<String, Object> variables = gestioneParametro.getProcessVariables();
		@SuppressWarnings("unchecked")
		List<Parametro> parametriGestione = (List<Parametro>) variables
				.get("parametri");
		Parametro parametroGestione1 = parametriGestione.get(0);
		assertEquals(parametroGestione1.getNome(), "nome1");
		assertEquals(parametroGestione1.getDescrizione(), "descrizione1");
		assertEquals(parametroGestione1.getUnitaMisura(), "unitaMisura1");
		assertEquals(parametroGestione1.isAttivo(), true);
		assertEquals(gestioneParametro.getAssignee(), USER_NAME);

		// VERIFICO CHE LE EMAIL DI ERRORE DI MODIFICA NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAAmministratore").list();
		assertEquals(notificaErroreModificaAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAPA").list();
		assertEquals(notificaErroreModificaAPA.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CANCELLAZIONE NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreCancellazioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneAAmministratore")
				.list();
		assertEquals(notificaErroreCancellazioneAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreCancellazioneAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneAPA").list();
		assertEquals(notificaErroreCancellazioneAPA.size(), 0);

		// MODIFICA DEL PARAMETRO 1
		variables.put("nome", "nome3");
		variables.put("descrizione", "descrizione3");
		variables.put("unitamisura", "unitamisura3");
		variables.put("statoattivazione", "statoattivazione3");
		taskService.complete(gestioneParametro.getId(), variables);
		gestioneParametri = taskService.createTaskQuery()
				.taskDefinitionKey("gestisciParametro")
				.includeProcessVariables().list();
		assertEquals(gestioneParametri.size(), 0);

		// VERIFICO CHE LE EMAIL DI MODIFICA NON SONO STATE MANDATE
		notificaErroreModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAAmministratore").list();
		assertEquals(notificaErroreModificaAAmministratore.size(), 0);
		notificaErroreModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAPA").list();
		assertEquals(notificaErroreModificaAPA.size(), 0);

		// VERIFICO CHE IL RECUPERO DEI PARAMETRI E' STATO ESEGUITO
		List<HistoricActivityInstance> visualizzaParametriService = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("visualizzaParametriService").list();
		assertEquals(visualizzaParametriService.size(), 1);

		// VERIFICO CHE LE MODIFICHE DEI PARAMETRI SONO STATE ESEGUITE
		List<HistoricActivityInstance> modificaParametroServiceHistroty = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("modificaParametroService").list();
		assertEquals(modificaParametroServiceHistroty.size(), 1);

		// VERIFICO CHE LE CANCELLAZIONI DEI PARAMETRI NON SONO STATE ESEGUITE
		List<HistoricActivityInstance> cancellaParametroServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("cancellaParametroService").list();
		assertEquals(cancellaParametroServiceHistory.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_parametri.bpmn20.xml" })
	public void testLetturaECancellazioneOK() {
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

		// POPOLO I PARAMETRI
		ProcessDefinition letturaParametri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaParametriModel = repositoryService
				.getBpmnModel(letturaParametri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoParametriService = (ServiceTask) letturaParametriModel
				.getFlowElement("visualizzaParametriService");
		recuperoParametriService
				.setImplementation(RecuperaParametriPopolati.class.getName());
		ServiceTask modificaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("modificaParametroService");
		modificaParametroService.setImplementation(EmptyModificaParametro.class
				.getName());
		ServiceTask cancellaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("cancellaParametroService");
		cancellaParametroService.setImplementation(EmptyCancellaParametro.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaParametriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaParametri");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametri").includeProcessVariables()
				.list();
		assertEquals(elencoParametri.size(), 1);

		// SELEZIONO 1 PARAMETRO
		@SuppressWarnings("unchecked")
		List<Parametro> parametri = (List<Parametro>) taskService.getVariable(
				elencoParametri.get(0).getId(), "parametri");
		List<Parametro> parametriDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametri.get(0) }));
		assertEquals(parametri.size(), 2);
		Map<String, Object> selezioneParametri = new HashMap<String, Object>();
		selezioneParametri.put("parametriselezionati", parametriDaSelezionare);
		selezioneParametri.put(OPERAZIONE, CANCELLAZIONE.name());
		taskService
				.complete(elencoParametri.get(0).getId(), selezioneParametri);

		// VERIFICO CHE LE EMAIL DI ERRORE DI MODIFICA NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAAmministratore").list();
		assertEquals(notificaErroreModificaAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAPA").list();
		assertEquals(notificaErroreModificaAPA.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI CANCELLAZIONE NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreCancellazioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneAAmministratore")
				.list();
		assertEquals(notificaErroreCancellazioneAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreCancellazioneAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneAPA").list();
		assertEquals(notificaErroreCancellazioneAPA.size(), 0);

		// VERIFICO CHE IL RECUPERO DEI PARAMETRI E' STATO ESEGUITO
		List<HistoricActivityInstance> visualizzaParametriService = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("visualizzaParametriService").list();
		assertEquals(visualizzaParametriService.size(), 1);

		// VERIFICO CHE LE MODIFICHE DEI PARAMETRI NON SONO STATE ESEGUITE
		List<HistoricActivityInstance> modificaParametroServiceHistroty = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("modificaParametroService").list();
		assertEquals(modificaParametroServiceHistroty.size(), 0);

		// VERIFICO CHE LE CANCELLAZIONI DEI PARAMETRI SONO STATE ESEGUITE
		List<HistoricActivityInstance> cancellaParametroServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("cancellaParametroService").list();
		assertEquals(cancellaParametroServiceHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_parametri.bpmn20.xml" })
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
		ProcessDefinition letturaParametri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaParametriModel = repositoryService
				.getBpmnModel(letturaParametri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask letturaParametriService = (ServiceTask) letturaParametriModel
				.getFlowElement("visualizzaParametriService");
		letturaParametriService
				.setImplementation(RecuperaParametriConNotificaErroreRecuperoGrave.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaParametriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaParametri");

		// VERIFICO CHE NON SIANO CREATI I TASK DI GESTIONE
		List<Task> gestioneParametri = taskService.createTaskQuery()
				.taskDefinitionKey("gestisciParametro")
				.includeProcessVariables().list();
		assertEquals(gestioneParametri.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoAAmministratore").list();
		assertEquals(notificaErroreRecuperoAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreRecuperoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoAPA").list();
		assertEquals(notificaErroreRecuperoAPA.size(), 1);

		// VERIFICO CHE IL RECUPERO DEI PARAMETRI E' STATO ESEGUITO
		List<HistoricActivityInstance> visualizzaParametriService = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("visualizzaParametriService").list();
		assertEquals(visualizzaParametriService.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_parametri.bpmn20.xml" })
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
		ProcessDefinition letturaParametri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaParametriModel = repositoryService
				.getBpmnModel(letturaParametri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask letturaParametriService = (ServiceTask) letturaParametriModel
				.getFlowElement("visualizzaParametriService");
		letturaParametriService
				.setImplementation(RecuperaParametriConNotificaErroreRecuperoLieve.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaParametriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaParametri");

		// VERIFICO CHE NON SIANO CREATI I TASK DI GESTIONE
		List<Task> gestioneParametri = taskService.createTaskQuery()
				.taskDefinitionKey("gestisciParametro")
				.includeProcessVariables().list();
		assertEquals(gestioneParametri.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoAAmministratore").list();
		assertEquals(notificaErroreRecuperoAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreRecuperoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoAPA").list();
		assertEquals(notificaErroreRecuperoAPA.size(), 0);

		// VERIFICO CHE IL RECUPERO DEI PARAMETRI E' STATO ESEGUITO
		List<HistoricActivityInstance> visualizzaParametriService = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("visualizzaParametriService").list();
		assertEquals(visualizzaParametriService.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_parametri.bpmn20.xml",
			"it/vige/greenarea/bpm/pa/inserisci_parametri.bpmn20.xml" })
	public void testNotificaErroreInserimentoLieve() {
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

		// AGGIUNGO UN ERRORE ALL'INSERIMENTO
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition letturaParametri = processiAttivi.get(1);
		ProcessDefinition inserimentoParametri = processiAttivi.get(0);
		BpmnModel letturaParametriModel = repositoryService
				.getBpmnModel(letturaParametri.getId());
		BpmnModel inserimentoParametriModel = repositoryService
				.getBpmnModel(inserimentoParametri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask inserimentoParametroService = (ServiceTask) inserimentoParametriModel
				.getFlowElement("inserimentoParametroService");
		inserimentoParametroService
				.setImplementation(InserisciParametroConNotificaErroreInserimentoLieve.class
						.getName());
		ServiceTask recuperoParametriService = (ServiceTask) letturaParametriModel
				.getFlowElement("visualizzaParametriService");
		recuperoParametriService
				.setImplementation(RecuperaParametriPopolati.class.getName());
		ServiceTask modificaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("modificaParametroService");
		modificaParametroService.setImplementation(EmptyModificaParametro.class
				.getName());
		ServiceTask cancellaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("cancellaParametroService");
		cancellaParametroService.setImplementation(EmptyCancellaParametro.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaParametriModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", inserimentoParametriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaParametri");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametri").includeProcessVariables()
				.list();
		assertEquals(elencoParametri.size(), 1);

		// ESEGUO L'INSERIMENTO
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put(OPERAZIONE, AGGIUNGI.name());
		taskService.complete(elencoParametri.get(0).getId(), operazione);

		// VERIFICO LA CREAZIONE DEL TASK DI COMPILAZIONE PARAMETRO
		List<Task> compilazioneParametro = taskService.createTaskQuery()
				.taskDefinitionKey("compilaParametro")
				.includeProcessVariables().list();
		assertEquals(compilazioneParametro.size(), 1);

		// INSERIMENTO FORM
		Map<String, Object> variables = new HashMap<String, Object>();
		String nome = "nome";
		variables.put("nome", nome);
		String descrizione = "descrizione";
		variables.put("descrizione", descrizione);
		String unitamisura = "unitamisura";
		variables.put("unitamisura", unitamisura);
		String statoattivazione = "statoattivazione";
		variables.put("statoattivazione", statoattivazione);
		taskService.complete(compilazioneParametro.get(0).getId(), variables);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreInserimentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreInserimentoAAmministratore").list();
		assertEquals(notificaErroreInserimentoAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreInserimentoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreInserimentoAPA").list();
		assertEquals(notificaErroreInserimentoAPA.size(), 1);

		// VERIFICO CHE IL SERVIZIO DI INSERIMENTO SIA STATO ESEGUITO 2 VOLTE
		List<HistoricActivityInstance> inserisciParametroService = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("inserimentoParametroService").list();
		assertEquals(inserisciParametroService.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_parametri.bpmn20.xml",
			"it/vige/greenarea/bpm/pa/inserisci_parametri.bpmn20.xml" })
	public void testNotificaErroreInserimentoGrave() {
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

		// AGGIUNGO UN ERRORE ALL'INSERIMENTO
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition letturaParametri = processiAttivi.get(1);
		ProcessDefinition inserimentoParametri = processiAttivi.get(0);
		BpmnModel letturaParametriModel = repositoryService
				.getBpmnModel(letturaParametri.getId());
		BpmnModel inserimentoParametriModel = repositoryService
				.getBpmnModel(inserimentoParametri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask inserimentoParametroService = (ServiceTask) inserimentoParametriModel
				.getFlowElement("inserimentoParametroService");
		inserimentoParametroService
				.setImplementation(InserisciParametroConNotificaErroreInserimentoGrave.class
						.getName());
		ServiceTask recuperoParametriService = (ServiceTask) letturaParametriModel
				.getFlowElement("visualizzaParametriService");
		recuperoParametriService
				.setImplementation(RecuperaParametriPopolati.class.getName());
		ServiceTask modificaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("modificaParametroService");
		modificaParametroService.setImplementation(EmptyModificaParametro.class
				.getName());
		ServiceTask cancellaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("cancellaParametroService");
		cancellaParametroService.setImplementation(EmptyCancellaParametro.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaParametriModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", inserimentoParametriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaParametri");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametri").includeProcessVariables()
				.list();
		assertEquals(elencoParametri.size(), 1);

		// ESEGUO L'INSERIMENTO
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put(OPERAZIONE, AGGIUNGI.name());
		taskService.complete(elencoParametri.get(0).getId(), operazione);

		// VERIFICO LA CREAZIONE DEL TASK DI COMPILAZIONE PARAMETRO
		List<Task> compilazioneParametro = taskService.createTaskQuery()
				.taskDefinitionKey("compilaParametro")
				.includeProcessVariables().list();
		assertEquals(compilazioneParametro.size(), 1);

		// INSERIMENTO FORM
		Map<String, Object> variables = new HashMap<String, Object>();
		String nome = "nome";
		variables.put("nome", nome);
		String descrizione = "descrizione";
		variables.put("descrizione", descrizione);
		String unitamisura = "unitamisura";
		variables.put("unitamisura", unitamisura);
		String statoattivazione = "statoattivazione";
		variables.put("statoattivazione", statoattivazione);
		taskService.complete(compilazioneParametro.get(0).getId(), variables);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreInserimentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreInserimentoAAmministratore").list();
		assertEquals(notificaErroreInserimentoAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreInserimentoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreInserimentoAPA").list();
		assertEquals(notificaErroreInserimentoAPA.size(), 1);

		// VERIFICO CHE IL SERVIZIO DI INSERIMENTO SIA STATO ESEGUITO 1 VOLTA
		List<HistoricActivityInstance> inserisciParametroService = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("inserimentoParametroService").list();
		assertEquals(inserisciParametroService.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_parametri.bpmn20.xml" })
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

		// POPOLO I PARAMETRI E AGGIUNGO UN ERRORE ALLA CANCELLAZIONE
		ProcessDefinition letturaParametri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaParametriModel = repositoryService
				.getBpmnModel(letturaParametri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask visualizzaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("visualizzaParametriService");
		visualizzaParametroService
				.setImplementation(RecuperaParametriPopolati.class.getName());
		ServiceTask cancellaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("cancellaParametroService");
		cancellaParametroService
				.setImplementation(CancellaParametroConNotificaErroreCancellazione.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaParametriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaParametri");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametri").includeProcessVariables()
				.list();
		assertEquals(elencoParametri.size(), 1);

		// SELEZIONO 1 PARAMETRO
		@SuppressWarnings("unchecked")
		List<Parametro> parametri = (List<Parametro>) taskService.getVariable(
				elencoParametri.get(0).getId(), "parametri");
		List<Parametro> parametriDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametri.get(0) }));
		assertEquals(parametri.size(), 2);
		Map<String, Object> selezioneParametri = new HashMap<String, Object>();
		selezioneParametri.put("parametriselezionati", parametriDaSelezionare);
		selezioneParametri.put(OPERAZIONE, CANCELLAZIONE.name());
		taskService
				.complete(elencoParametri.get(0).getId(), selezioneParametri);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreCancellazioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneAAmministratore")
				.list();
		assertEquals(notificaErroreCancellazioneAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreCancellazioneAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneAPA").list();
		assertEquals(notificaErroreCancellazioneAPA.size(), 1);

		// VERIFICO CHE IL RECUPERO DEI PARAMETRI E' STATO ESEGUITO
		List<HistoricActivityInstance> visualizzaParametriService = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("visualizzaParametriService").list();
		assertEquals(visualizzaParametriService.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_parametri.bpmn20.xml" })
	public void testNotificaErroreModifica() {
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

		// POPOLO I PARAMETRI E AGGIUNGO UN ERRORE ALLA MODIFICA
		ProcessDefinition letturaParametri = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaParametriModel = repositoryService
				.getBpmnModel(letturaParametri.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask visualizzaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("visualizzaParametriService");
		visualizzaParametroService
				.setImplementation(RecuperaParametriPopolati.class.getName());
		ServiceTask modificaParametroService = (ServiceTask) letturaParametriModel
				.getFlowElement("modificaParametroService");
		modificaParametroService
				.setImplementation(ModificaParametroConNotificaErroreModifica.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaParametriModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaParametri");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametri").includeProcessVariables()
				.list();
		assertEquals(elencoParametri.size(), 1);

		// SELEZIONO 1 PARAMETRO
		@SuppressWarnings("unchecked")
		List<Parametro> parametri = (List<Parametro>) taskService.getVariable(
				elencoParametri.get(0).getId(), "parametri");
		List<Parametro> parametriDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametri.get(0) }));
		assertEquals(parametri.size(), 2);
		Map<String, Object> selezioneParametri = new HashMap<String, Object>();
		selezioneParametri.put("parametriselezionati", parametriDaSelezionare);
		selezioneParametri.put(OPERAZIONE, MODIFICA.name());
		taskService
				.complete(elencoParametri.get(0).getId(), selezioneParametri);

		// VERIFICO LA CREAZIONE DEI TASK DI GESTIONE
		List<Task> gestioneParametri = taskService.createTaskQuery()
				.taskDefinitionKey("gestisciParametro")
				.includeProcessVariables().list();
		assertEquals(gestioneParametri.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI GESTIONE
		// DEL PARAMETRO 1
		Task gestioneParametro1 = gestioneParametri.get(0);
		Map<String, Object> variables = gestioneParametro1
				.getProcessVariables();
		@SuppressWarnings("unchecked")
		List<Parametro> parametriModifica1 = (List<Parametro>) variables
				.get("parametri");
		Parametro parametroModifica1 = parametriModifica1.get(0);
		assertEquals(parametroModifica1.getNome(), "nome1");
		assertEquals(parametroModifica1.getDescrizione(), "descrizione1");
		assertEquals(parametroModifica1.getUnitaMisura(), "unitaMisura1");
		assertEquals(parametroModifica1.isAttivo(), true);
		assertEquals(gestioneParametro1.getAssignee(), USER_NAME);

		// VERIFICO LA MODIFICA DEL TASK DI GESTIONE 1
		taskService.complete(gestioneParametro1.getId());
		gestioneParametri = taskService.createTaskQuery()
				.taskDefinitionKey("gestisciParametro").list();
		assertEquals(gestioneParametri.size(), 0);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAAmministratore").list();
		assertEquals(notificaErroreModificaAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAPA").list();
		assertEquals(notificaErroreModificaAPA.size(), 1);

		// VERIFICO CHE IL RECUPERO DEI PARAMETRI E' STATO ESEGUITO
		List<HistoricActivityInstance> visualizzaParametriService = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("visualizzaParametriService").list();
		assertEquals(visualizzaParametriService.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

}
