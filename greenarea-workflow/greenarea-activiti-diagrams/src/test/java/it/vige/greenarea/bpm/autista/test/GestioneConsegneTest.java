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

import static it.vige.greenarea.dto.StatoRichiesta.ACCETTATO;
import static it.vige.greenarea.dto.StatoRichiesta.RIFIUTATO;
import static org.activiti.bpmn.model.ImplementationType.IMPLEMENTATION_TYPE_CLASS;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.autista.gestioneconsegne.EmptyAggiornaConsegna;
import it.vige.greenarea.bpm.autista.gestioneconsegne.EmptyAggiornaStatoInCarico;
import it.vige.greenarea.bpm.autista.gestioneconsegne.EmptyNotificaAggiornamentoConsegnaAOperatoreLogistico;
import it.vige.greenarea.bpm.autista.service.gestioneconsegne.AggiornaConsegnaConNotificaErrore;
import it.vige.greenarea.bpm.autista.service.gestioneconsegne.PopolamentoMissione;
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

public class GestioneConsegneTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "autista1";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public GestioneConsegneTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/autista/gestione_consegne.bpmn20.xml" })
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
		ProcessDefinition gestioneConsegne = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel gestioneConsegneModel = repositoryService
				.getBpmnModel(gestioneConsegne.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent startGestioneConsegne = (StartEvent) gestioneConsegneModel
				.getFlowElement("messagestarteventGestioneConsegne");
		ActivitiListener popolamentoMissione = new ActivitiListener();
		popolamentoMissione.setEvent("end");
		popolamentoMissione.setImplementation(PopolamentoMissione.class
				.getName());
		popolamentoMissione.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		startGestioneConsegne.getEventDefinitions().clear();
		startGestioneConsegne.getExecutionListeners().add(popolamentoMissione);
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
				.addBpmnModel("dynamic-model.bpmn", gestioneConsegneModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("gestioneConsegne");

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// CONSEGNE
		List<Task> aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").includeProcessVariables().list();
		assertEquals(aggiornaConsegne.size(), 2);
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

		// MODIFICA DEI PARAMETRI DELLO STATO 1
		richiesta.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna.getId(), variables);
		aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").list();
		assertEquals(aggiornaConsegne.size(), 1);

		// MODIFICA DEI PARAMETRI DELLO STATO 2
		richiesta2.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna2.getId(), variables);
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
		assertEquals(aggiornaStato.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/autista/gestione_consegne.bpmn20.xml" })
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
		ProcessDefinition gestioneConsegne = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel gestioneConsegneModel = repositoryService
				.getBpmnModel(gestioneConsegne.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent startGestioneConsegne = (StartEvent) gestioneConsegneModel
				.getFlowElement("messagestarteventGestioneConsegne");
		ActivitiListener popolamentoMissione = new ActivitiListener();
		popolamentoMissione.setEvent("end");
		popolamentoMissione.setImplementation(PopolamentoMissione.class
				.getName());
		popolamentoMissione.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		startGestioneConsegne.getEventDefinitions().clear();
		startGestioneConsegne.getExecutionListeners().add(popolamentoMissione);
		UserTask aggiornaConsegnaUserTask = (UserTask) gestioneConsegneModel
				.getFlowElement("aggiornaConsegna");
		ActivitiListener aggiornaStatoInCaricoService = aggiornaConsegnaUserTask
				.getTaskListeners().get(0);
		aggiornaStatoInCaricoService
				.setImplementation(EmptyAggiornaStatoInCarico.class.getName());
		ActivitiListener aggiornaConsegnaService = aggiornaConsegnaUserTask
				.getTaskListeners().get(1);
		aggiornaConsegnaService
				.setImplementation(AggiornaConsegnaConNotificaErrore.class
						.getName());
		ActivitiListener notificaAggiornamentoConsegnaAOperatoreLogisticoService = aggiornaConsegnaUserTask
				.getTaskListeners().get(2);
		notificaAggiornamentoConsegnaAOperatoreLogisticoService
				.setImplementation(EmptyNotificaAggiornamentoConsegnaAOperatoreLogistico.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", gestioneConsegneModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("gestioneConsegne");

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI VISUALIZZAZIONE
		// CONSEGNE
		List<Task> aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").includeProcessVariables().list();
		assertEquals(aggiornaConsegne.size(), 2);
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

		// MODIFICA DEI PARAMETRI DELLO STATO 1
		richiesta.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna.getId(), variables);
		aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").list();
		assertEquals(aggiornaConsegne.size(), 1);

		// MODIFICA DEI PARAMETRI DELLO STATO 2
		richiesta2.setStato(RIFIUTATO.name());
		taskService.complete(aggiornaConsegna2.getId(), variables);
		aggiornaConsegne = taskService.createTaskQuery()
				.taskName("Aggiorna Consegna").list();
		assertEquals(aggiornaConsegne.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DELLE CONSEGNE SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAAutista = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAAutista").list();
		assertEquals(notificaErroreAAutista.size(), 1);
		List<HistoricActivityInstance> notificaErroreAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAOperatoreLogistico").list();
		assertEquals(notificaErroreAOperatoreLogistico.size(), 1);
		List<HistoricActivityInstance> notificaErroreAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreAAmministratore").list();
		assertEquals(notificaErroreAAmministratore.size(), 1);

		// VERIFICO CHE L'AGGIORNAMENTO DELLE CONSEGNE E' STATO ESEGUITO
		List<HistoricActivityInstance> aggiornaStato = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornaConsegna").list();
		assertEquals(aggiornaStato.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}
}
