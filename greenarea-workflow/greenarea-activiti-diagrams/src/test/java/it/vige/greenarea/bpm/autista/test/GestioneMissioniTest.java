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
package it.vige.greenarea.bpm.autista.test;

import static it.vige.greenarea.dto.StatoMissione.COMPLETED;
import static it.vige.greenarea.dto.StatoMissione.STARTED;
import static it.vige.greenarea.dto.StatoMissione.WAITING;
import static it.vige.greenarea.dto.StatoRichiesta.ACCETTATO;
import static it.vige.greenarea.dto.StatoRichiesta.RIFIUTATO;
import static org.activiti.bpmn.model.ImplementationType.IMPLEMENTATION_TYPE_CLASS;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.autista.gestioneconsegne.EmptyAggiornaConsegna;
import it.vige.greenarea.bpm.autista.gestioneconsegne.EmptyAggiornaStatoInCarico;
import it.vige.greenarea.bpm.autista.gestioneconsegne.EmptyNotificaAggiornamentoConsegnaAOperatoreLogistico;
import it.vige.greenarea.bpm.autista.gestionemissioni.EmptyAggiornaMissione;
import it.vige.greenarea.bpm.autista.gestionemissioni.EmptyNotificaAggiornamentoMissioneAOperatoreLogistico;
import it.vige.greenarea.bpm.autista.gestionemissioni.EmptyPresaInCaricoMissione;
import it.vige.greenarea.bpm.autista.service.gestionemissioni.AggiornaMissioneConNotificaErrore;
import it.vige.greenarea.bpm.autista.service.gestionemissioni.PopolamentoMissioni;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.Richiesta;

import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.test.ResourceActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.subethamail.smtp.server.SMTPServer;

public class GestioneMissioniTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "autista1";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public GestioneMissioniTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = {
			"bpm/autista/gestione_missioni.bpmn20.xml",
			"bpm/autista/gestione_consegne.bpmn20.xml" })
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

		// POPOLO LA MISSIONE E INIZIO IL PROCESSO
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition gestioneMissioni = processiAttivi.get(1);
		ProcessDefinition gestioneConsegne = processiAttivi.get(0);
		BpmnModel gestioneMissioniModel = repositoryService
				.getBpmnModel(gestioneMissioni.getId());
		BpmnModel gestioneConsegneModel = repositoryService
				.getBpmnModel(gestioneConsegne.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent startGestioneMissioni = (StartEvent) gestioneMissioniModel
				.getFlowElement("startGestioneMissioni");
		ActivitiListener popolamentoMissioni = new ActivitiListener();
		popolamentoMissioni.setEvent("end");
		popolamentoMissioni.setImplementation(PopolamentoMissioni.class
				.getName());
		popolamentoMissioni.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		startGestioneMissioni.getExecutionListeners().add(popolamentoMissioni);
		UserTask aggiornaMissioneUserTask = (UserTask) gestioneMissioniModel
				.getFlowElement("aggiornaMissione");
		ActivitiListener aggiornaMissioneService = aggiornaMissioneUserTask
				.getTaskListeners().get(0);
		aggiornaMissioneService.setImplementation(EmptyAggiornaMissione.class
				.getName());
		ActivitiListener notificaAggiornamentoMissioneAOperatoreLogisticoService = aggiornaMissioneUserTask
				.getTaskListeners().get(1);
		notificaAggiornamentoMissioneAOperatoreLogisticoService
				.setImplementation(EmptyNotificaAggiornamentoMissioneAOperatoreLogistico.class
						.getName());
		ActivitiListener presaInCaricoMissioneService = aggiornaMissioneUserTask
				.getTaskListeners().get(2);
		presaInCaricoMissioneService
				.setImplementation(EmptyPresaInCaricoMissione.class.getName());
		UserTask aggiornaConsegnaUserTask = (UserTask) gestioneConsegneModel
				.getFlowElement("aggiornaConsegna");
		ActivitiListener aggiornaStatoInCaricoService = aggiornaConsegnaUserTask
				.getTaskListeners().get(0);
		aggiornaStatoInCaricoService
				.setImplementation(EmptyAggiornaStatoInCarico.class.getName());
		ActivitiListener aggiornaConsegnaService = aggiornaConsegnaUserTask
				.getTaskListeners().get(1);
		aggiornaConsegnaService.setImplementation(EmptyAggiornaConsegna.class
				.getName());
		ActivitiListener notificaAggiornamentoConsegnaAOperatoreLogisticoService = aggiornaConsegnaUserTask
				.getTaskListeners().get(2);
		notificaAggiornamentoConsegnaAOperatoreLogisticoService
				.setImplementation(EmptyNotificaAggiornamentoConsegnaAOperatoreLogistico.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneConsegneModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("gestioneMissioni");

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// MISSIONI
		List<Task> aggiornaMissioni = taskService.createTaskQuery()
				.taskName("Aggiorna Missione").includeProcessVariables().list();
		assertEquals(aggiornaMissioni.size(), 2);
		Task aggiornaMissione = aggiornaMissioni.get(0);
		Map<String, Object> variables = aggiornaMissione.getProcessVariables();
		@SuppressWarnings("unchecked")
		Missione missione = ((List<Missione>) variables.get("missioni")).get(0);
		assertEquals(missione.getStato(), STARTED);
		assertEquals(aggiornaMissione.getAssignee(), USER_NAME);
		Task aggiornaMissione2 = aggiornaMissioni.get(1);
		variables = aggiornaMissione2.getProcessVariables();
		@SuppressWarnings("unchecked")
		Missione missione2 = ((List<Missione>) variables.get("missioni"))
				.get(1);
		assertEquals(missione2.getStato(), WAITING);
		assertEquals(aggiornaMissione2.getAssignee(), USER_NAME);

		// MODIFICA DEI PARAMETRI DELLO STATO 1
		missione.setStato(COMPLETED);
		taskService.complete(aggiornaMissione.getId(), variables);
		aggiornaMissioni = taskService.createTaskQuery()
				.taskName("Aggiorna Missione").list();
		assertEquals(aggiornaMissioni.size(), 1);

		// MODIFICA DEI PARAMETRI DELLO STATO 2
		missione2.setStato(STARTED);
		taskService.complete(aggiornaMissione2.getId(), variables);
		aggiornaMissioni = taskService.createTaskQuery()
				.taskName("Aggiorna Missione").list();
		assertEquals(aggiornaMissioni.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DELLE MISSIONI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreMissioneAAutista = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreMissioneAAutista").list();
		assertEquals(notificaErroreMissioneAAutista.size(), 0);
		List<HistoricActivityInstance> notificaErroreMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreMissioneAOperatoreLogistico").list();
		assertEquals(notificaErroreMissioneAOperatoreLogistico.size(), 0);
		List<HistoricActivityInstance> notificaErroreMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreMissioneAAmministratore").list();
		assertEquals(notificaErroreMissioneAAmministratore.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO DELLE MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> aggiornaStato = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornaMissione").list();
		assertEquals(aggiornaStato.size(), 2);

		// PARTE IL TEST DI GESTIONE CONSEGNE
		gestioneConsegneTestOK();

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = {
			"bpm/autista/gestione_missioni.bpmn20.xml",
			"bpm/autista/gestione_consegne.bpmn20.xml" })
	public void testNotificaErrore() {
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

		// POPOLO LA MISSIONE, AGGIUNGO L'ERRORE DI AGGIORNAMENTO E INIZIO IL
		// PROCESSO
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition gestioneMissioni = processiAttivi.get(1);
		ProcessDefinition gestioneConsegne = processiAttivi.get(0);
		BpmnModel gestioneMissioniModel = repositoryService
				.getBpmnModel(gestioneMissioni.getId());
		BpmnModel gestioneConsegneModel = repositoryService
				.getBpmnModel(gestioneConsegne.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent startGestioneMissioni = (StartEvent) gestioneMissioniModel
				.getFlowElement("startGestioneMissioni");
		ActivitiListener popolamentoMissioni = new ActivitiListener();
		popolamentoMissioni.setEvent("end");
		popolamentoMissioni.setImplementation(PopolamentoMissioni.class
				.getName());
		popolamentoMissioni.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		startGestioneMissioni.getExecutionListeners().add(popolamentoMissioni);
		UserTask aggiornaMissioneUserTask = (UserTask) gestioneMissioniModel
				.getFlowElement("aggiornaMissione");
		ActivitiListener aggiornaMissioneService = aggiornaMissioneUserTask
				.getTaskListeners().get(0);
		aggiornaMissioneService
				.setImplementation(AggiornaMissioneConNotificaErrore.class
						.getName());
		ActivitiListener notificaAggiornamentoMissioneAOperatoreLogisticoService = aggiornaMissioneUserTask
				.getTaskListeners().get(1);
		notificaAggiornamentoMissioneAOperatoreLogisticoService
				.setImplementation(EmptyNotificaAggiornamentoMissioneAOperatoreLogistico.class
						.getName());
		ActivitiListener presaInCaricoMissioneService = aggiornaMissioneUserTask
				.getTaskListeners().get(2);
		presaInCaricoMissioneService
				.setImplementation(EmptyPresaInCaricoMissione.class.getName());
		UserTask aggiornaConsegnaUserTask = (UserTask) gestioneConsegneModel
				.getFlowElement("aggiornaConsegna");
		ActivitiListener aggiornaStatoInCaricoService = aggiornaConsegnaUserTask
				.getTaskListeners().get(0);
		aggiornaStatoInCaricoService
				.setImplementation(EmptyAggiornaStatoInCarico.class.getName());
		ActivitiListener aggiornaConsegnaService = aggiornaConsegnaUserTask
				.getTaskListeners().get(1);
		aggiornaConsegnaService.setImplementation(EmptyAggiornaConsegna.class
				.getName());
		ActivitiListener notificaAggiornamentoConsegnaAOperatoreLogisticoService = aggiornaConsegnaUserTask
				.getTaskListeners().get(2);
		notificaAggiornamentoConsegnaAOperatoreLogisticoService
				.setImplementation(EmptyNotificaAggiornamentoConsegnaAOperatoreLogistico.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneMissioniModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneConsegneModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("gestioneMissioni");

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// MISSIONI
		List<Task> aggiornaMissioni = taskService.createTaskQuery()
				.taskName("Aggiorna Missione").includeProcessVariables().list();
		assertEquals(aggiornaMissioni.size(), 2);
		Task aggiornaMissione = aggiornaMissioni.get(0);
		Map<String, Object> variables = aggiornaMissione.getProcessVariables();
		@SuppressWarnings("unchecked")
		Missione missione = ((List<Missione>) variables.get("missioni")).get(0);
		assertEquals(missione.getStato(), STARTED);
		assertEquals(aggiornaMissione.getAssignee(), USER_NAME);
		Task aggiornaMissione2 = aggiornaMissioni.get(1);
		variables = aggiornaMissione2.getProcessVariables();
		@SuppressWarnings("unchecked")
		Missione missione2 = ((List<Missione>) variables.get("missioni"))
				.get(1);
		assertEquals(missione2.getStato(), WAITING);
		assertEquals(aggiornaMissione2.getAssignee(), USER_NAME);

		// MODIFICA DEI PARAMETRI DELLO STATO 1
		missione.setStato(COMPLETED);
		taskService.complete(aggiornaMissione.getId(), variables);
		aggiornaMissioni = taskService.createTaskQuery()
				.taskName("Aggiorna Missione").list();
		assertEquals(aggiornaMissioni.size(), 1);

		// MODIFICA DEI PARAMETRI DELLO STATO 2
		missione2.setStato(STARTED);
		taskService.complete(aggiornaMissione2.getId(), variables);
		aggiornaMissioni = taskService.createTaskQuery()
				.taskName("Aggiorna Missione").list();
		assertEquals(aggiornaMissioni.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DELLE MISSIONI SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreMissioneAAutista = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreMissioneAAutista").list();
		assertEquals(notificaErroreMissioneAAutista.size(), 1);
		List<HistoricActivityInstance> notificaErroreMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreMissioneAOperatoreLogistico").list();
		assertEquals(notificaErroreMissioneAOperatoreLogistico.size(), 1);
		List<HistoricActivityInstance> notificaErroreMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreMissioneAAmministratore").list();
		assertEquals(notificaErroreMissioneAAmministratore.size(), 1);

		// VERIFICO CHE L'AGGIORNAMENTO DELLE MISSIONI E' STATO ESEGUITO
		List<HistoricActivityInstance> aggiornaStato = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornaMissione").list();
		assertEquals(aggiornaStato.size(), 2);

		// PARTE IL TEST DI GESTIONE CONSEGNE
		gestioneConsegneTestOK();

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	public void gestioneConsegneTestOK() {
		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// CONSEGNE
		List<Task> aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").includeProcessVariables().list();
		assertEquals(aggiornaConsegne.size(), 4);

		Task aggiornaConsegna = aggiornaConsegne.get(0);
		Map<String, Object> variables = aggiornaConsegna.getProcessVariables();
		Missione missione = (Missione) variables.get("missione");
		Richiesta richiesta = missione.getRichieste().get(0);
		assertEquals(richiesta.getStato(), ACCETTATO.name());
		assertEquals(richiesta.getMotivazione(), "bello");

		Task aggiornaConsegna2 = aggiornaConsegne.get(1);
		variables = aggiornaConsegna2.getProcessVariables();
		missione = (Missione) variables.get("missione");
		Richiesta richiesta2 = missione.getRichieste().get(1);
		assertEquals(richiesta2.getStato(), ACCETTATO.name());
		assertEquals(richiesta2.getMotivazione(), "mi piace");

		Task aggiornaConsegna3 = aggiornaConsegne.get(2);
		variables = aggiornaConsegna3.getProcessVariables();
		missione = (Missione) variables.get("missione");
		Richiesta richiesta3 = missione.getRichieste().get(0);
		assertEquals(richiesta3.getStato(), ACCETTATO.name());
		assertEquals(richiesta3.getMotivazione(), "bello");

		Task aggiornaConsegna4 = aggiornaConsegne.get(3);
		variables = aggiornaConsegna4.getProcessVariables();
		missione = (Missione) variables.get("missione");
		Richiesta richiesta4 = missione.getRichieste().get(1);
		assertEquals(richiesta4.getStato(), ACCETTATO.name());
		assertEquals(richiesta4.getMotivazione(), "mi piace");

		// MODIFICA DEI PARAMETRI DELLO STATO 1
		richiesta.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna.getId(), variables);
		aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").list();
		assertEquals(aggiornaConsegne.size(), 3);

		// MODIFICA DEI PARAMETRI DELLO STATO 2
		richiesta2.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna2.getId(), variables);
		aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").list();
		assertEquals(aggiornaConsegne.size(), 2);

		// MODIFICA DEI PARAMETRI DELLO STATO 3
		richiesta3.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna3.getId(), variables);
		aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").list();
		assertEquals(aggiornaConsegne.size(), 1);

		// MODIFICA DEI PARAMETRI DELLO STATO 4
		richiesta4.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna4.getId(), variables);
		aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").list();
		assertEquals(aggiornaConsegne.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DELLE CONSEGNE NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAAutista = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAAutista").list();
		assertEquals(notificaErroreAAutista.size(), 0);
		List<HistoricActivityInstance> notificaErroreAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAOperatoreLogistico").list();
		assertEquals(notificaErroreAOperatoreLogistico.size(), 0);
		List<HistoricActivityInstance> notificaErroreAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAAmministratore").list();
		assertEquals(notificaErroreAAmministratore.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO DELLE CONSEGNE E' STATO ESEGUITO
		List<HistoricActivityInstance> aggiornaStato = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornaConsegna").list();
		assertEquals(aggiornaStato.size(), 4);

	}

}
