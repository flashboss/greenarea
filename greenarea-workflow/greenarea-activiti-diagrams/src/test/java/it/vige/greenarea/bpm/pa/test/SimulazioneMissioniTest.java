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
import static it.vige.greenarea.dto.Operazione.CHIUDI;
import static it.vige.greenarea.dto.Operazione.ELENCO;
import static it.vige.greenarea.dto.Operazione.SIMULAZIONE;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.pa.service.simulazionemissioni.NotificaErrorePolicyMancanti;
import it.vige.greenarea.bpm.pa.service.simulazionemissioni.NotificaErroreRecuperoDatiMissioni;
import it.vige.greenarea.bpm.pa.service.simulazionemissioni.NotificaErroreRecuperoDatiPolicy;
import it.vige.greenarea.bpm.pa.service.simulazionemissioni.RecuperoDatiMissioniPopolate;
import it.vige.greenarea.bpm.pa.service.simulazionemissioni.RecuperoDatiPolicyPopolate;
import it.vige.greenarea.bpm.pa.service.simulazionemissioni.SimulazionePopolata;
import it.vige.greenarea.bpm.pa.simulazionemissioni.EmptyRecuperoDatiPolicy;
import it.vige.greenarea.dto.Missione;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.test.ResourceActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.subethamail.smtp.server.SMTPServer;

public class SimulazioneMissioniTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "paguidonia";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public SimulazioneMissioniTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/simulazione_missioni.bpmn20.xml" })
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
		String ga = "ga";
		variables.put("ga", ga);
		String operatoreLogistico = "veicolo";
		variables.put("operatorelogistico", operatoreLogistico);
		String veicolo = "veicolo";
		variables.put("veicolo", veicolo);
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);

		// IL TEST NON PREVEDE L'INJECTION. RESETTO I SERVIZI
		ProcessDefinition simulazioneMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel simulazioneMissioniModel = repositoryService
				.getBpmnModel(simulazioneMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoDatiMissioniService = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("recuperoDatiMissioni");
		recuperoDatiMissioniService
				.setImplementation(RecuperoDatiMissioniPopolate.class.getName());
		ServiceTask recuperoDatiPolicyService = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("recuperoDatiPolicy");
		recuperoDatiPolicyService
				.setImplementation(RecuperoDatiPolicyPopolate.class.getName());
		ServiceTask simulazione = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("simulazione");
		simulazione.setImplementation(SimulazionePopolata.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", simulazioneMissioniModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("simulazioneMissioni",
				variables);

		// VERIFICO CHE CI SIA SEMPRE UN UNICO PROCESSO DI REPORT ATTIVO
		List<ProcessInstance> reportProcessInstances = runtimeService
				.createProcessInstanceQuery().variableValueEquals("report")
				.list();
		assertEquals(reportProcessInstances.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI REPORT
		List<Task> visualizzaReports = taskService.createTaskQuery()
				.taskName("Visualizza Report").includeProcessVariables().list();
		assertEquals(visualizzaReports.size(), 1);
		Task visualizzaReport = visualizzaReports.get(0);
		variables = visualizzaReport.getProcessVariables();
		assertEquals(visualizzaReport.getAssignee(), USER_NAME);

		// CREAZIONE DEL REPORT DI SIMULAZIONE
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put(OPERAZIONE, SIMULAZIONE);
		taskService.complete(visualizzaReport.getId(), operazione);

		// VERIFICO L'ESISTENZA DEL REPORT DI SIMULAZIONE
		List<Task> simulazioni = taskService.createTaskQuery()
				.taskName("Visualizza Simulazione").includeProcessVariables()
				.list();
		assertEquals(simulazioni.size(), 1);
		visualizzaReports = taskService.createTaskQuery()
				.taskName("Visualizza Report").includeProcessVariables().list();
		assertEquals(visualizzaReports.size(), 0);

		// RITORNO ALL'ELENCO
		operazione.put(OPERAZIONE, ELENCO);
		taskService.complete(simulazioni.get(0).getId(), operazione);

		// VERIFICO L'ESISTENZA DEL REPORT
		simulazioni = taskService.createTaskQuery()
				.taskName("Visualizza Simulazione").includeProcessVariables()
				.list();
		assertEquals(simulazioni.size(), 0);
		visualizzaReports = taskService.createTaskQuery()
				.taskName("Visualizza Report").includeProcessVariables().list();
		assertEquals(visualizzaReports.size(), 1);

		// VERIFICO CHE L'ELENCO DELLE MISSIONI VENGA RESETTATO
		@SuppressWarnings("unchecked")
		Collection<Missione> missioniSelezionate = (Collection<Missione>) taskService
				.getVariable(visualizzaReports.get(0).getId(),
						"missioniselezionate");
		assertEquals(missioniSelezionate.size(), 2);

		// CHIUDO IL TASK
		operazione.put(OPERAZIONE, CHIUDI);
		visualizzaReport = visualizzaReports.get(0);
		taskService.complete(visualizzaReport.getId(), operazione);

		// VERIFICO L'ESISTENZA DEL REPORT
		simulazioni = taskService.createTaskQuery()
				.taskName("Visualizza Simulazione").includeProcessVariables()
				.list();
		assertEquals(simulazioni.size(), 0);
		visualizzaReports = taskService.createTaskQuery()
				.taskName("Visualizza Report").includeProcessVariables().list();
		assertEquals(visualizzaReports.size(), 0);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiMissioniAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiMissioniAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiMissioniAPA").list();
		assertEquals(notificaErroreReperimentoDatiMissioniAPA.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoDatiPolicyAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiPolicyAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiPolicyAPA").list();
		assertEquals(notificaErroreReperimentoDatiPolicyAPA.size(), 0);
		List<HistoricActivityInstance> notificaDatiPolicyMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiPolicyMancantiAAmministratore").list();
		assertEquals(notificaDatiPolicyMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiPolicyMancantiAPA").list();
		assertEquals(notificaDatiPolicyMancantiAPA.size(), 0);

		// VERIFICO CHE IL RECUPERO DATI MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDatiMissioni = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioni").list();
		assertEquals(recuperoDatiMissioni.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI POLICY E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDatiPolicy = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiPolicy").list();
		assertEquals(recuperoDatiPolicy.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/simulazione_missioni.bpmn20.xml" })
	public void testNotificaErroreReperimentoDatiMissioni() {
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
		String ga = "ga";
		variables.put("ga", ga);
		String operatoreLogistico = "veicolo";
		variables.put("operatorelogistico", operatoreLogistico);
		String veicolo = "veicolo";
		variables.put("veicolo", veicolo);
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);

		// AGGIUNGO UN ERRORE AL RECUPERO DATI
		ProcessDefinition simulazioneMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel simulazioneMissioniModel = repositoryService
				.getBpmnModel(simulazioneMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoDatiMissioniService = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("recuperoDatiMissioni");
		recuperoDatiMissioniService
				.setImplementation(NotificaErroreRecuperoDatiMissioni.class
						.getName());
		ServiceTask recuperoDatiPolicyService = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("recuperoDatiPolicy");
		recuperoDatiPolicyService
				.setImplementation(EmptyRecuperoDatiPolicy.class.getName());
		ServiceTask simulazione = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("simulazione");
		simulazione.setImplementation(SimulazionePopolata.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", simulazioneMissioniModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("simulazioneMissioni",
				variables);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiMissioniAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiMissioniAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiMissioniAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiMissioniAPA").list();
		assertEquals(notificaErroreReperimentoDatiMissioniAPA.size(), 1);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoDatiPolicyAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiPolicyAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiPolicyAPA").list();
		assertEquals(notificaErroreReperimentoDatiPolicyAPA.size(), 0);
		List<HistoricActivityInstance> notificaDatiPolicyMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiPolicyMancantiAAmministratore").list();
		assertEquals(notificaDatiPolicyMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiPolicyMancantiAPA").list();
		assertEquals(notificaDatiPolicyMancantiAPA.size(), 0);

		// VERIFICO LA CREAZIONE DEL REPORT
		List<Task> visualizzazioneReports = taskService.createTaskQuery()
				.taskName("Visualizza Report").list();
		assertEquals(visualizzazioneReports.size(), 0);

		// VERIFICO CHE IL RECUPERO DATI MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDatiMissioni = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioni").list();
		assertEquals(recuperoDatiMissioni.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI POLICY NON E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDatiPolicy = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiPolicy").list();
		assertEquals(recuperoDatiPolicy.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/simulazione_missioni.bpmn20.xml" })
	public void testNotificaErroreReperimentoDatiPolicy() {
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
		String ga = "ga";
		variables.put("ga", ga);
		String operatoreLogistico = "veicolo";
		variables.put("operatorelogistico", operatoreLogistico);
		String veicolo = "veicolo";
		variables.put("veicolo", veicolo);
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);

		// AGGIUNGO UN ERRORE AL RECUPERO DATI
		ProcessDefinition simulazioneMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel simulazioneMissioniModel = repositoryService
				.getBpmnModel(simulazioneMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoDatiMissioniService = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("recuperoDatiMissioni");
		recuperoDatiMissioniService
				.setImplementation(RecuperoDatiMissioniPopolate.class.getName());
		ServiceTask recuperoDatiPolicyService = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("recuperoDatiPolicy");
		recuperoDatiPolicyService
				.setImplementation(NotificaErroreRecuperoDatiPolicy.class
						.getName());
		ServiceTask simulazione = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("simulazione");
		simulazione.setImplementation(SimulazionePopolata.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", simulazioneMissioniModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("simulazioneMissioni",
				variables);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiMissioniAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiMissioniAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiMissioniAPA").list();
		assertEquals(notificaErroreReperimentoDatiMissioniAPA.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoDatiPolicyAAmministratore.size(),
				1);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiPolicyAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiPolicyAPA").list();
		assertEquals(notificaErroreReperimentoDatiPolicyAPA.size(), 1);
		List<HistoricActivityInstance> notificaDatiPolicyMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiPolicyMancantiAAmministratore").list();
		assertEquals(notificaDatiPolicyMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiPolicyMancantiAPA").list();
		assertEquals(notificaDatiPolicyMancantiAPA.size(), 0);

		// VERIFICO LA CREAZIONE DEL REPORT E CHIUDO IL REPORT
		List<Task> visualizzazioneReports = taskService.createTaskQuery()
				.taskName("Visualizza Report").list();
		assertEquals(visualizzazioneReports.size(), 1);
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put(OPERAZIONE, CHIUDI);
		taskService.complete(visualizzazioneReports.get(0).getId(), operazione);

		// VERIFICO CHE IL RECUPERO DATI MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDatiMissioni = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioni").list();
		assertEquals(recuperoDatiMissioni.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI POLICY E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDatiPolicy = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiPolicy").list();
		assertEquals(recuperoDatiPolicy.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/simulazione_missioni.bpmn20.xml" })
	public void testNotificaErrorePolicyMancanti() {
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
		String ga = "ga";
		variables.put("ga", ga);
		String operatoreLogistico = "veicolo";
		variables.put("operatorelogistico", operatoreLogistico);
		String veicolo = "veicolo";
		variables.put("veicolo", veicolo);
		Date dal = new Date();
		variables.put("dal", dal);
		Date al = new Date();
		variables.put("al", al);

		// AGGIUNGO UN ERRORE AL RECUPERO DATI
		ProcessDefinition simulazioneMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel simulazioneMissioniModel = repositoryService
				.getBpmnModel(simulazioneMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoDatiMissioniService = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("recuperoDatiMissioni");
		recuperoDatiMissioniService
				.setImplementation(RecuperoDatiMissioniPopolate.class.getName());
		ServiceTask recuperoDatiPolicyService = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("recuperoDatiPolicy");
		recuperoDatiPolicyService
				.setImplementation(NotificaErrorePolicyMancanti.class.getName());
		ServiceTask simulazione = (ServiceTask) simulazioneMissioniModel
				.getFlowElement("simulazione");
		simulazione.setImplementation(SimulazionePopolata.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", simulazioneMissioniModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("simulazioneMissioni",
				variables);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiMissioniAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiMissioniAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiMissioniAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiMissioniAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiMissioniAPA").list();
		assertEquals(notificaErroreReperimentoDatiMissioniAPA.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoDatiPolicyAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiPolicyAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoDatiPolicyAPA").list();
		assertEquals(notificaErroreReperimentoDatiPolicyAPA.size(), 0);
		List<HistoricActivityInstance> notificaDatiPolicyMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiPolicyMancantiAAmministratore").list();
		assertEquals(notificaDatiPolicyMancantiAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaDatiPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiPolicyMancantiAPA").list();
		assertEquals(notificaDatiPolicyMancantiAPA.size(), 1);

		// VERIFICO LA CREAZIONE DEL REPORT E CHIUDO IL REPORT
		List<Task> visualizzazioneReports = taskService.createTaskQuery()
				.taskName("Visualizza Report").list();
		assertEquals(visualizzazioneReports.size(), 1);
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put(OPERAZIONE, CHIUDI);
		taskService.complete(visualizzazioneReports.get(0).getId(), operazione);

		// VERIFICO CHE IL RECUPERO DATI MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDatiMissioni = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiMissioni").list();
		assertEquals(recuperoDatiMissioni.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI POLICY E' STATO ESEGUITO
		List<HistoricActivityInstance> recuperoDatiPolicy = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiPolicy").list();
		assertEquals(recuperoDatiPolicy.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

}
