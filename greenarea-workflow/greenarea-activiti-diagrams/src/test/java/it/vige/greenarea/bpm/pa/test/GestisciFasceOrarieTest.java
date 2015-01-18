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
import static it.vige.greenarea.dto.Operazione.AVANTI;
import static it.vige.greenarea.dto.Operazione.CANCELLAZIONE;
import static it.vige.greenarea.dto.Operazione.DETTAGLIO;
import static it.vige.greenarea.dto.Operazione.ELENCO;
import static it.vige.greenarea.dto.Operazione.INSERIMENTO;
import static it.vige.greenarea.dto.Operazione.MODIFICA;
import static it.vige.greenarea.dto.Peso.NESSUNO;
import static it.vige.greenarea.dto.Ripetizione.MAI;
import static it.vige.greenarea.dto.TipologiaParametro.BENEFICIO;
import static java.text.DateFormat.getDateInstance;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.pa.gestiscifasceorarie.EmptyCancellaFasciaOraria;
import it.vige.greenarea.bpm.pa.gestiscifasceorarie.EmptyInserisciFasciaOraria;
import it.vige.greenarea.bpm.pa.gestiscifasceorarie.EmptyModificaFasciaOraria;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.CancellaFasciaOrariaConNotificaErroreCancellazione;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.InserisciFasciaOrariaConNotificaErroreInserimentoGrave;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.InserisciFasciaOrariaConNotificaErroreInserimentoLieve;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.ModificaFasciaOrariaConNotificaErroreModificaGrave;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.ModificaFasciaOrariaConNotificaErroreModificaLieve;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.RecuperaFasceOrarieConNotificaErroreRecuperoGrave;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.RecuperaFasceOrarieConNotificaErroreRecuperoLieve;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.RecuperaFasceOrariePopolate;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.RecuperaParametriPerInserimentoConNotificaErroreRecupero;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.RecuperaParametriPerInserimentoPopolati;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.RecuperaParametriPerModificaConNotificaErroreRecupero;
import it.vige.greenarea.bpm.pa.service.gestiscifasceorarie.RecuperaParametriPerModificaPopolati;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Ripetizione;

import java.text.ParseException;
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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.subethamail.smtp.server.SMTPServer;

public class GestisciFasceOrarieTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "patorino";

	private Logger logger = getLogger(getClass());

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public GestisciFasceOrarieTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml",
			"it/vige/greenarea/bpm/pa/inserisci_fasce_orarie.bpmn20.xml" })
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
		ProcessDefinition letturaFasceOrarie = processiAttivi.get(1);
		ProcessDefinition inserisciFasceOrarie = processiAttivi.get(0);
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		BpmnModel inserisciFasceOrarieModel = repositoryService
				.getBpmnModel(inserisciFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperaParametriPerInserimentoService = (ServiceTask) inserisciFasceOrarieModel
				.getFlowElement("recuperaParametriPerInserimentoService");
		recuperaParametriPerInserimentoService
				.setImplementation(RecuperaParametriPerInserimentoPopolati.class
						.getName());
		ServiceTask inserimentoFasciaOrariaService = (ServiceTask) inserisciFasceOrarieModel
				.getFlowElement("inserimentoFasciaOrariaService");
		inserimentoFasciaOrariaService
				.setImplementation(EmptyInserisciFasciaOraria.class.getName());
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(EmptyCancellaFasciaOraria.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", inserisciFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// ESEGUO L'INSERIMENTO
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put(OPERAZIONE, AGGIUNGI);
		taskService.complete(elencoFasceOrarie.get(0).getId(), operazione);

		// VERIFICO LA CREAZIONE DEL TASK DI COMPILAZIONE FASCIA ORARIA
		List<Task> compilazioneFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("compilaFasciaOraria")
				.includeProcessVariables().list();
		assertEquals(compilazioneFasciaOraria.size(), 1);

		// INSERIMENTO FORM
		Map<String, Object> variables = new HashMap<String, Object>();
		String ga = "nuova area geografica";
		variables.put("ga", ga);
		DateTime orarioInizio = new DateTime();
		variables.put("orarioinizio", orarioInizio);
		DateTime orarioFine = new DateTime();
		variables.put("orariofine", orarioFine);
		Date validitaPolicy = null;
		try {
			validitaPolicy = getDateInstance().parse("29-lug-2014");
			variables.put("validitapolicy", validitaPolicy);
		} catch (ParseException e) {
			logger.error("activiti diagram", e);
		}
		Ripetizione ripetitivitaPolicy = MAI;
		variables.put("ripetitivitapolicy", ripetitivitaPolicy);
		taskService
				.complete(compilazioneFasciaOraria.get(0).getId(), variables);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerInserimento")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// SELEZIONO 1 PARAMETRO
		@SuppressWarnings("unchecked")
		List<Parametro> parametriTs = (List<Parametro>) taskService
				.getVariable(elencoParametriTs.get(0).getId(), "parametrits");
		List<Parametro> parametriTsDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametriTs.get(0) }));
		assertEquals(parametriTs.size(), 2);
		Map<String, Object> propertiesForm = new HashMap<String, Object>();
		propertiesForm.put("parametritsselezionati", parametriTsDaSelezionare);
		propertiesForm.put(OPERAZIONE, INSERIMENTO);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO LA CREAZIONE DEI TASK DI INSERIMENTO PARAMETRI
		List<Task> inserimentoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("inserimentoParametri")
				.includeProcessVariables().list();
		assertEquals(inserimentoParametri.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI INSERIMENTO
		// DEL PARAMETRO 1
		Task inserimentoParametro = inserimentoParametri.get(0);
		variables = inserimentoParametro.getProcessVariables();
		@SuppressWarnings("unchecked")
		List<Parametro> parametri = (List<Parametro>) variables
				.get("parametrits");
		Parametro parametro1 = parametri.get(0);
		assertEquals(parametro1.getDescrizione(), "descrizionets1");
		assertEquals(parametro1.getNome(), "nomets1");
		assertEquals(parametro1.getTipo(), BENEFICIO.name());
		assertEquals(parametro1.getUnitaMisura(), "unitaMisuraTs1");
		assertEquals(parametro1.getValoreMassimo(), 0.0);
		assertEquals(parametro1.getValoreMinimo(), 0.0);
		assertEquals(parametro1.getPeso(), NESSUNO.name());

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerInserimentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoParametriPerInserimentoAAmministratore")
				.list();
		assertEquals(
				notificaErroreRecuperoParametriPerInserimentoAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerInserimentoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoParametriPerInserimentoAPA")
				.list();
		assertEquals(notificaErroreRecuperoParametriPerInserimentoAPA.size(), 0);

		// INSERIMENTO DEL PARAMETRO 1
		taskService.complete(inserimentoParametro.getId());
		inserimentoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("inserimentoParametri").list();
		assertEquals(inserimentoParametri.size(), 0);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerInserimento")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// SELEZIONO IL PARAMETRO CREATO PER LA MODIFICA
		@SuppressWarnings("unchecked")
		List<Parametro> parametriAggiunti = (List<Parametro>) taskService
				.getVariable(elencoParametriTs.get(0).getId(),
						"parametriaggiunti");
		List<Parametro> parametriAggiuntiDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametriAggiunti.get(0) }));
		assertEquals(parametriAggiunti.size(), 2);
		propertiesForm = new HashMap<String, Object>();
		propertiesForm.put("parametriaggiuntiselezionati",
				parametriAggiuntiDaSelezionare);
		propertiesForm.put(OPERAZIONE, MODIFICA);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO LA CREAZIONE DEI TASK DI MODIFICA PARAMETRI
		List<Task> modificaParametri = taskService.createTaskQuery()
				.taskDefinitionKey("modificaParametri")
				.includeProcessVariables().list();
		assertEquals(modificaParametri.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI MODIFICA
		// DEL PARAMETRO 1
		Task modificaParametro = modificaParametri.get(0);
		variables = modificaParametro.getProcessVariables();
		@SuppressWarnings("unchecked")
		List<Parametro> parametriModifica = (List<Parametro>) variables
				.get("parametriaggiunti");
		parametro1 = parametriModifica.get(0);
		assertEquals(parametro1.getDescrizione(), "descrizionets1");
		assertEquals(parametro1.getNome(), "nomets1");
		assertEquals(parametro1.getTipo(), BENEFICIO.name());
		assertEquals(parametro1.getUnitaMisura(), "unitaMisuraTs1");
		assertEquals(parametro1.getValoreMassimo(), 0.0);
		assertEquals(parametro1.getValoreMinimo(), 0.0);
		assertEquals(parametro1.getPeso(), NESSUNO.name());

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI NON SONO STATE
		// MANDATE
		notificaErroreRecuperoParametriPerInserimentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoParametriPerInserimentoAAmministratore")
				.list();
		assertEquals(
				notificaErroreRecuperoParametriPerInserimentoAAmministratore
						.size(),
				0);
		notificaErroreRecuperoParametriPerInserimentoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoParametriPerInserimentoAPA")
				.list();
		assertEquals(notificaErroreRecuperoParametriPerInserimentoAPA.size(), 0);

		// MODIFICA DEL PARAMETRO 1
		taskService.complete(modificaParametro.getId());
		modificaParametri = taskService.createTaskQuery()
				.taskDefinitionKey("modificaParametri").list();
		assertEquals(modificaParametri.size(), 0);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerInserimento")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// SELEZIONO 1 ALTRO PARAMETRO
		@SuppressWarnings("unchecked")
		List<Parametro> altriParametriTs = (List<Parametro>) taskService
				.getVariable(elencoParametriTs.get(0).getId(), "parametrits");
		parametriTsDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { altriParametriTs.get(0) }));
		assertEquals(altriParametriTs.size(), 2);
		propertiesForm = new HashMap<String, Object>();
		propertiesForm.put("parametritsselezionati", parametriTsDaSelezionare);
		propertiesForm.put(OPERAZIONE, INSERIMENTO);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO LA CREAZIONE DEI TASK DI INSERIMENTO PARAMETRI
		inserimentoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("inserimentoParametri")
				.includeProcessVariables().list();
		assertEquals(inserimentoParametri.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI INSERIMENTO
		// DEL PARAMETRO 1
		inserimentoParametro = inserimentoParametri.get(0);
		variables = inserimentoParametro.getProcessVariables();
		@SuppressWarnings("unchecked")
		List<Parametro> altriParametri = (List<Parametro>) variables
				.get("parametrits");
		parametro1 = altriParametri.get(0);
		assertEquals(parametro1.getDescrizione(), "descrizionets1");
		assertEquals(parametro1.getNome(), "nomets1");
		assertEquals(parametro1.getTipo(), BENEFICIO.name());
		assertEquals(parametro1.getUnitaMisura(), "unitaMisuraTs1");
		assertEquals(parametro1.getValoreMassimo(), 0.0);
		assertEquals(parametro1.getValoreMinimo(), 0.0);
		assertEquals(parametro1.getPeso(), NESSUNO.name());

		// INSERIMENTO DEL PARAMETRO 2
		taskService.complete(inserimentoParametro.getId());
		inserimentoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("inserimentoParametri").list();
		assertEquals(inserimentoParametri.size(), 0);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerInserimento")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// ACCEDO ALL'INSERIMENTO DEI PREZZI
		propertiesForm = new HashMap<String, Object>();
		propertiesForm.put(OPERAZIONE, AVANTI);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO LA CREAZIONE DEL TASK DI INSERIMENTO PREZZI
		List<Task> inserimentoPrezzi = taskService.createTaskQuery()
				.taskName("Inserimento Prezzi").includeProcessVariables()
				.list();
		assertEquals(inserimentoPrezzi.size(), 1);

		// INSERIMENTO DEL PREZZO
		Task inserimentoPrezzo = inserimentoPrezzi.get(0);
		taskService.complete(inserimentoPrezzo.getId());
		inserimentoPrezzi = taskService.createTaskQuery()
				.taskName("Inserimento Prezzi").list();
		assertEquals(inserimentoPrezzi.size(), 0);

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
		List<HistoricActivityInstance> inserimentoFasciaOrariaServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("inserimentoFasciaOrariaService").list();
		assertEquals(inserimentoFasciaOrariaServiceHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml",
			"it/vige/greenarea/bpm/pa/inserisci_fasce_orarie.bpmn20.xml" })
	public void testNotificaErroreRecuperoParametriInserimentoGrave() {
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
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition letturaFasceOrarie = processiAttivi.get(1);
		ProcessDefinition inserisciFasceOrarie = processiAttivi.get(0);
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		BpmnModel inserisciFasceOrarieModel = repositoryService
				.getBpmnModel(inserisciFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperaParametriPerInserimentoService = (ServiceTask) inserisciFasceOrarieModel
				.getFlowElement("recuperaParametriPerInserimentoService");
		recuperaParametriPerInserimentoService
				.setImplementation(RecuperaParametriPerInserimentoConNotificaErroreRecupero.class
						.getName());
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(EmptyCancellaFasciaOraria.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", inserisciFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// ESEGUO L'INSERIMENTO
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put(OPERAZIONE, AGGIUNGI);
		taskService.complete(elencoFasceOrarie.get(0).getId(), operazione);

		// VERIFICO LA CREAZIONE DEL TASK DI COMPILAZIONE FASCIA ORARIA
		List<Task> compilazioneFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("compilaFasciaOraria")
				.includeProcessVariables().list();
		assertEquals(compilazioneFasciaOraria.size(), 1);

		// INSERIMENTO FORM
		Map<String, Object> variables = new HashMap<String, Object>();
		String ga = "nuova area geografica";
		variables.put("ga", ga);
		DateTime orarioInizio = new DateTime();
		variables.put("orarioinizio", orarioInizio);
		DateTime orarioFine = new DateTime();
		variables.put("orariofine", orarioFine);
		Date validitaPolicy = null;
		try {
			validitaPolicy = getDateInstance().parse("29-lug-2014");
			variables.put("validitapolicy", validitaPolicy);
		} catch (ParseException e) {
			logger.error("activiti diagram", e);
		}
		Ripetizione ripetitivitaPolicy = MAI;
		variables.put("ripetitivitapolicy", ripetitivitaPolicy);
		taskService
				.complete(compilazioneFasciaOraria.get(0).getId(), variables);

		// VERIFICO CHE NON SIANO CREATI I TASK DI INSERIMENTO PARAMETRI
		List<Task> inserimentoParametri = taskService.createTaskQuery()
				.taskDefinitionKey("inserimentoParametri")
				.includeProcessVariables().list();
		assertEquals(inserimentoParametri.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO PARAMETRI SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerInserimentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoParametriPerInserimentoAAmministratore")
				.list();
		assertEquals(
				notificaErroreRecuperoParametriPerInserimentoAAmministratore
						.size(),
				1);
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerInserimentoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoParametriPerInserimentoAPA")
				.list();
		assertEquals(notificaErroreRecuperoParametriPerInserimentoAPA.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml" })
	public void testLetturaConDettaglioOK() {
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

		// POPOLO LE FASCE ORARIE
		ProcessDefinition letturaFasceOrarie = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(EmptyCancellaFasciaOraria.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// SELEZIONO 1 FASCIA ORARIA
		@SuppressWarnings("unchecked")
		List<FasciaOraria> fasceOrarie = (List<FasciaOraria>) taskService
				.getVariable(elencoFasceOrarie.get(0).getId(), "fasceorarie");
		List<FasciaOraria> fasceOrarieDaSelezionare = new ArrayList<FasciaOraria>(
				asList(new FasciaOraria[] { fasceOrarie.get(0) }));
		assertEquals(fasceOrarie.size(), 2);
		Map<String, Object> selezioneFasceOrarie = new HashMap<String, Object>();
		selezioneFasceOrarie.put("fasceorarieselezionate",
				fasceOrarieDaSelezionare);
		selezioneFasceOrarie.put(OPERAZIONE, DETTAGLIO);
		taskService.complete(elencoFasceOrarie.get(0).getId(),
				selezioneFasceOrarie);

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

		// DETTAGLIO DELLA FASCIA ORARIA 1
		List<Task> dettaglioFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("dettaglioFasciaOraria").list();
		assertEquals(dettaglioFasciaOraria.size(), 1);

		// RITORNO ALL'ELENCO DELLA FASCIA ORARIA 1
		selezioneFasceOrarie.put(OPERAZIONE, ELENCO);
		taskService.complete(dettaglioFasciaOraria.get(0).getId(),
				selezioneFasceOrarie);
		elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie").list();
		assertEquals(elencoFasceOrarie.size(), 0);
		dettaglioFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("dettaglioFasciaOraria").list();
		assertEquals(dettaglioFasciaOraria.size(), 0);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreCancellazioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneAAmministratore")
				.list();
		assertEquals(notificaErroreCancellazioneAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreCancellazioneAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneAPA").list();
		assertEquals(notificaErroreCancellazioneAPA.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml" })
	public void testLetturaConDettaglioECancellazioneOK() {
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

		// POPOLO LE FASCE ORARIE
		ProcessDefinition letturaFasceOrarie = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(EmptyCancellaFasciaOraria.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// SELEZIONO 1 FASCIA ORARIA
		@SuppressWarnings("unchecked")
		List<FasciaOraria> fasceOrarie = (List<FasciaOraria>) taskService
				.getVariable(elencoFasceOrarie.get(0).getId(), "fasceorarie");
		List<FasciaOraria> fasceOrarieDaSelezionare = new ArrayList<FasciaOraria>(
				asList(new FasciaOraria[] { fasceOrarie.get(0) }));
		assertEquals(fasceOrarie.size(), 2);
		Map<String, Object> selezioneFasceOrarie = new HashMap<String, Object>();
		selezioneFasceOrarie.put("fasceorarieselezionate",
				fasceOrarieDaSelezionare);
		selezioneFasceOrarie.put(OPERAZIONE, DETTAGLIO);
		taskService.complete(elencoFasceOrarie.get(0).getId(),
				selezioneFasceOrarie);

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

		// DETTAGLIO DELLA FASCIA ORARIA 1
		List<Task> dettaglioFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("dettaglioFasciaOraria").list();
		assertEquals(dettaglioFasciaOraria.size(), 1);

		// CANCELLAZIONE DELLA FASCIA ORARIA 1
		selezioneFasceOrarie.put(OPERAZIONE, CANCELLAZIONE);
		taskService.complete(dettaglioFasciaOraria.get(0).getId(),
				selezioneFasceOrarie);
		elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie").list();
		assertEquals(elencoFasceOrarie.size(), 0);
		dettaglioFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("dettaglioFasciaOraria").list();
		assertEquals(dettaglioFasciaOraria.size(), 0);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreCancellazioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneAAmministratore")
				.list();
		assertEquals(notificaErroreCancellazioneAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreCancellazioneAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreCancellazioneAPA").list();
		assertEquals(notificaErroreCancellazioneAPA.size(), 0);

		// VERIFICO CHE LA CANCELLAZIONE DELLE FASCE ORARIE E' STATA ESEGUITA
		List<HistoricActivityInstance> cancellaFasciaOrariaServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("cancellaFasciaOrariaService").list();
		assertEquals(cancellaFasciaOrariaServiceHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml" })
	public void testLetturaConCancellazioneOK() {
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

		// POPOLO LE FASCE ORARIE
		ProcessDefinition letturaFasceOrarie = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(EmptyCancellaFasciaOraria.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// SELEZIONO 1 FASCIA ORARIA
		@SuppressWarnings("unchecked")
		List<FasciaOraria> fasceOrarie = (List<FasciaOraria>) taskService
				.getVariable(elencoFasceOrarie.get(0).getId(), "fasceorarie");
		List<FasciaOraria> fasceOrarieDaSelezionare = new ArrayList<FasciaOraria>(
				asList(new FasciaOraria[] { fasceOrarie.get(0) }));
		assertEquals(fasceOrarie.size(), 2);
		Map<String, Object> selezioneFasceOrarie = new HashMap<String, Object>();
		selezioneFasceOrarie.put("fasceorarieselezionate",
				fasceOrarieDaSelezionare);
		selezioneFasceOrarie.put(OPERAZIONE, CANCELLAZIONE);
		taskService.complete(elencoFasceOrarie.get(0).getId(),
				selezioneFasceOrarie);

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

		// VERIFICO CHE LA CANCELLAZIONE DELLE FASCE ORARIE E' STATA ESEGUITA
		List<HistoricActivityInstance> cancellaFasciaOrariaServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("cancellaFasciaOrariaService").list();
		assertEquals(cancellaFasciaOrariaServiceHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml" })
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
		ProcessDefinition letturaFasceOrarie = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask letturaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		letturaFasciaOrariaService
				.setImplementation(RecuperaFasceOrarieConNotificaErroreRecuperoGrave.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO CHE NON SIANO CREATI I TASK DI CANCELLAZIONE
		List<Task> cancellaFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaFasciaOraria")
				.includeProcessVariables().list();
		assertEquals(cancellaFasceOrarie.size(), 0);

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

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml" })
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
		ProcessDefinition letturaFasceOrarie = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask letturaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		letturaFasciaOrariaService
				.setImplementation(RecuperaFasceOrarieConNotificaErroreRecuperoLieve.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO CHE NON SIANO CREATI I TASK DI CANCELLAZIONE
		List<Task> cancellaFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("visualizzaFasciaOraria")
				.includeProcessVariables().list();
		assertEquals(cancellaFasceOrarie.size(), 0);

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

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml",
			"it/vige/greenarea/bpm/pa/inserisci_fasce_orarie.bpmn20.xml" })
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
		ProcessDefinition letturaFasceOrarie = processiAttivi.get(1);
		ProcessDefinition inserisciFasceOrarie = processiAttivi.get(0);
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		BpmnModel inserisciFasceOrarieModel = repositoryService
				.getBpmnModel(inserisciFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperaParametriPerInserimentoService = (ServiceTask) inserisciFasceOrarieModel
				.getFlowElement("recuperaParametriPerInserimentoService");
		recuperaParametriPerInserimentoService
				.setImplementation(RecuperaParametriPerInserimentoPopolati.class
						.getName());
		ServiceTask inserimentoFasciaOrariaService = (ServiceTask) inserisciFasceOrarieModel
				.getFlowElement("inserimentoFasciaOrariaService");
		inserimentoFasciaOrariaService
				.setImplementation(InserisciFasciaOrariaConNotificaErroreInserimentoLieve.class
						.getName());
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(EmptyCancellaFasciaOraria.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", inserisciFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// ESEGUO L'INSERIMENTO
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put(OPERAZIONE, AGGIUNGI);
		taskService.complete(elencoFasceOrarie.get(0).getId(), operazione);

		// VERIFICO LA CREAZIONE DEL TASK DI COMPILAZIONE FASCIA ORARIA
		List<Task> compilazioneFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("compilaFasciaOraria")
				.includeProcessVariables().list();
		assertEquals(compilazioneFasciaOraria.size(), 1);

		// INSERIMENTO FORM
		Map<String, Object> variables = new HashMap<String, Object>();
		String ga = "nuova area geografica";
		variables.put("ga", ga);
		DateTime orarioInizio = new DateTime();
		variables.put("orarioinizio", orarioInizio);
		DateTime orarioFine = new DateTime();
		variables.put("orariofine", orarioFine);
		Date validitaPolicy = null;
		try {
			validitaPolicy = getDateInstance().parse("29-lug-2014");
			variables.put("validitapolicy", validitaPolicy);
		} catch (ParseException e) {
			logger.error("activiti diagram", e);
		}
		Ripetizione ripetitivitaPolicy = MAI;
		variables.put("ripetitivitapolicy", ripetitivitaPolicy);
		taskService
				.complete(compilazioneFasciaOraria.get(0).getId(), variables);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerInserimento")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// VADO ALLA FORM DEI PREZZI
		@SuppressWarnings("unchecked")
		List<Parametro> parametriTs = (List<Parametro>) taskService
				.getVariable(elencoParametriTs.get(0).getId(), "parametrits");
		List<Parametro> parametriTsDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametriTs.get(0) }));
		assertEquals(parametriTs.size(), 2);
		Map<String, Object> propertiesForm = new HashMap<String, Object>();
		propertiesForm.put("parametritsselezionati", parametriTsDaSelezionare);
		propertiesForm.put(OPERAZIONE, AVANTI);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerInserimentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoParametriPerInserimentoAAmministratore")
				.list();
		assertEquals(
				notificaErroreRecuperoParametriPerInserimentoAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerInserimentoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoParametriPerInserimentoAPA")
				.list();
		assertEquals(notificaErroreRecuperoParametriPerInserimentoAPA.size(), 0);

		// VERIFICO LA CREAZIONE DEL TASK DI INSERIMENTO PREZZI
		List<Task> inserimentoPrezzi = taskService.createTaskQuery()
				.taskName("Inserimento Prezzi").includeProcessVariables()
				.list();
		assertEquals(inserimentoPrezzi.size(), 1);

		// INSERIMENTO DEL PREZZO
		Task inserimentoPrezzo = inserimentoPrezzi.get(0);
		taskService.complete(inserimentoPrezzo.getId());
		inserimentoPrezzi = taskService.createTaskQuery()
				.taskName("Inserimento Prezzi").list();
		assertEquals(inserimentoPrezzi.size(), 0);

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
		List<HistoricActivityInstance> inserisciFasciaOrariaService = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("inserimentoFasciaOrariaService").list();
		assertEquals(inserisciFasciaOrariaService.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml",
			"it/vige/greenarea/bpm/pa/inserisci_fasce_orarie.bpmn20.xml" })
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
		ProcessDefinition letturaFasceOrarie = processiAttivi.get(1);
		ProcessDefinition inserisciFasceOrarie = processiAttivi.get(0);
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		BpmnModel inserisciFasceOrarieModel = repositoryService
				.getBpmnModel(inserisciFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperaParametriPerInserimentoService = (ServiceTask) inserisciFasceOrarieModel
				.getFlowElement("recuperaParametriPerInserimentoService");
		recuperaParametriPerInserimentoService
				.setImplementation(RecuperaParametriPerInserimentoPopolati.class
						.getName());
		ServiceTask inserimentoFasciaOrariaService = (ServiceTask) inserisciFasceOrarieModel
				.getFlowElement("inserimentoFasciaOrariaService");
		inserimentoFasciaOrariaService
				.setImplementation(InserisciFasciaOrariaConNotificaErroreInserimentoGrave.class
						.getName());
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(EmptyCancellaFasciaOraria.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", inserisciFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// ESEGUO L'INSERIMENTO
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put(OPERAZIONE, AGGIUNGI);
		taskService.complete(elencoFasceOrarie.get(0).getId(), operazione);

		// VERIFICO LA CREAZIONE DEL TASK DI COMPILAZIONE FASCIA ORARIA
		List<Task> compilazioneFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("compilaFasciaOraria")
				.includeProcessVariables().list();
		assertEquals(compilazioneFasciaOraria.size(), 1);

		// INSERIMENTO FORM
		Map<String, Object> variables = new HashMap<String, Object>();
		String ga = "nuova area geografica";
		variables.put("ga", ga);
		DateTime orarioInizio = new DateTime();
		variables.put("orarioinizio", orarioInizio);
		DateTime orarioFine = new DateTime();
		variables.put("orariofine", orarioFine);
		Date validitaPolicy = null;
		try {
			validitaPolicy = getDateInstance().parse("29-lug-2014");
			variables.put("validitapolicy", validitaPolicy);
		} catch (ParseException e) {
			logger.error("activiti diagram", e);
		}
		Ripetizione ripetitivitaPolicy = MAI;
		variables.put("ripetitivitapolicy", ripetitivitaPolicy);
		taskService
				.complete(compilazioneFasciaOraria.get(0).getId(), variables);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerInserimento")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// VADO ALLA FORM DEI PREZZI
		@SuppressWarnings("unchecked")
		List<Parametro> parametriTs = (List<Parametro>) taskService
				.getVariable(elencoParametriTs.get(0).getId(), "parametrits");
		List<Parametro> parametriTsDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametriTs.get(0) }));
		assertEquals(parametriTs.size(), 2);
		Map<String, Object> propertiesForm = new HashMap<String, Object>();
		propertiesForm.put("parametritsselezionati", parametriTsDaSelezionare);
		propertiesForm.put(OPERAZIONE, AVANTI);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerInserimentoAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoParametriPerInserimentoAAmministratore")
				.list();
		assertEquals(
				notificaErroreRecuperoParametriPerInserimentoAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerInserimentoAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoParametriPerInserimentoAPA")
				.list();
		assertEquals(notificaErroreRecuperoParametriPerInserimentoAPA.size(), 0);

		// VERIFICO LA CREAZIONE DEL TASK DI INSERIMENTO PREZZI
		List<Task> inserimentoPrezzi = taskService.createTaskQuery()
				.taskName("Inserimento Prezzi").includeProcessVariables()
				.list();
		assertEquals(inserimentoPrezzi.size(), 1);

		// INSERIMENTO DEL PREZZO
		Task inserimentoPrezzo = inserimentoPrezzi.get(0);
		taskService.complete(inserimentoPrezzo.getId());
		inserimentoPrezzi = taskService.createTaskQuery()
				.taskName("Inserimento Prezzi").list();
		assertEquals(inserimentoPrezzi.size(), 0);

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
		List<HistoricActivityInstance> inserisciFasciaOrariaService = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("inserimentoFasciaOrariaService").list();
		assertEquals(inserisciFasciaOrariaService.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml" })
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

		// POPOLO LE FASCE ORARIE E AGGIUNGO UN ERRORE ALLA CANCELLAZIONE
		ProcessDefinition letturaFasceOrarieDefinition = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarieDefinition.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(CancellaFasciaOrariaConNotificaErroreCancellazione.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// SELEZIONO 1 FASCIA ORARIA
		@SuppressWarnings("unchecked")
		List<FasciaOraria> fasceOrarie = (List<FasciaOraria>) taskService
				.getVariable(elencoFasceOrarie.get(0).getId(), "fasceorarie");
		List<FasciaOraria> fasceOrarieDaSelezionare = new ArrayList<FasciaOraria>(
				asList(new FasciaOraria[] { fasceOrarie.get(0) }));
		assertEquals(fasceOrarie.size(), 2);
		Map<String, Object> selezioneFasceOrarie = new HashMap<String, Object>();
		selezioneFasceOrarie.put("fasceorarieselezionate",
				fasceOrarieDaSelezionare);
		selezioneFasceOrarie.put(OPERAZIONE, CANCELLAZIONE);
		taskService.complete(elencoFasceOrarie.get(0).getId(),
				selezioneFasceOrarie);

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

		// VERIFICO CHE LA CANCELLAZIONE DELLE FASCE ORARIE E' STATA ESEGUITA
		List<HistoricActivityInstance> cancellaFasciaOrariaServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("cancellaFasciaOrariaService").list();
		assertEquals(cancellaFasciaOrariaServiceHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml",
			"it/vige/greenarea/bpm/pa/modifica_fasce_orarie.bpmn20.xml" })
	public void testModificaOK() {
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
		ProcessDefinition letturaFasceOrarie = processiAttivi.get(0);
		ProcessDefinition modificaFasceOrarie = processiAttivi.get(1);
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		BpmnModel modificaFasceOrarieModel = repositoryService
				.getBpmnModel(modificaFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperaParametriPerModificaService = (ServiceTask) modificaFasceOrarieModel
				.getFlowElement("recuperaParametriPerModificaService");
		recuperaParametriPerModificaService
				.setImplementation(RecuperaParametriPerModificaPopolati.class
						.getName());
		ServiceTask modificaFasciaOrariaService = (ServiceTask) modificaFasceOrarieModel
				.getFlowElement("modificaFasciaOrariaService");
		modificaFasciaOrariaService
				.setImplementation(EmptyModificaFasciaOraria.class.getName());
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(EmptyCancellaFasciaOraria.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", modificaFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// SELEZIONO LA FASCIA ORARIA
		@SuppressWarnings("unchecked")
		List<FasciaOraria> fasceOrarie = (List<FasciaOraria>) taskService
				.getVariable(elencoFasceOrarie.get(0).getId(), "fasceorarie");
		List<FasciaOraria> fasceOrarieDaSelezionare = new ArrayList<FasciaOraria>(
				asList(new FasciaOraria[] { fasceOrarie.get(0) }));
		assertEquals(fasceOrarie.size(), 2);
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put("fasceorarieselezionate", fasceOrarieDaSelezionare);
		operazione.put(OPERAZIONE, MODIFICA);
		taskService.complete(elencoFasceOrarie.get(0).getId(), operazione);

		// VERIFICO LA CREAZIONE DEL TASK DI MODIFICA FASCIA ORARIA
		List<Task> modificaFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("modificaFasciaOraria")
				.includeProcessVariables().list();
		assertEquals(modificaFasciaOraria.size(), 1);
		FasciaOraria fasciaOraria = (FasciaOraria) taskService.getVariable(
				modificaFasciaOraria.get(0).getId(), "fasciaoraria");
		assertNotNull(fasciaOraria);
		assertNotNull(fasciaOraria.getPrezzi());

		// MODIFICA FORM
		String ga = "nuova area geografica";
		fasciaOraria.setGa(ga);
		Date orarioInizio = new Date();
		fasciaOraria.setOrarioInizio(orarioInizio);
		Date orarioFine = new Date();
		fasciaOraria.setOrarioFine(orarioFine);
		fasciaOraria.setAperturaRichieste("29-lug-2014");
		Ripetizione ripetitivitaPolicy = MAI;
		fasciaOraria.setRipetitivitaPolicy(ripetitivitaPolicy.name());
		taskService.complete(modificaFasciaOraria.get(0).getId());

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerModifica")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// SELEZIONO 1 PARAMETRO
		@SuppressWarnings("unchecked")
		List<Parametro> parametriTs = (List<Parametro>) taskService
				.getVariable(elencoParametriTs.get(0).getId(), "parametrits");
		List<Parametro> parametriTsDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametriTs.get(0) }));
		assertEquals(parametriTs.size(), 2);
		Map<String, Object> propertiesForm = new HashMap<String, Object>();
		propertiesForm.put("parametritsselezionati", parametriTsDaSelezionare);
		propertiesForm.put(OPERAZIONE, INSERIMENTO);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO LA CREAZIONE DEI TASK DI INSERIMENTO PARAMETRI
		List<Task> inserisciParametri = taskService.createTaskQuery()
				.taskDefinitionKey("inserisciParametri")
				.includeProcessVariables().list();
		assertEquals(inserisciParametri.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI MODIFICA
		// DEL PARAMETRO 1
		Task inserisciParametro = inserisciParametri.get(0);
		Map<String, Object> variables = inserisciParametro
				.getProcessVariables();
		@SuppressWarnings("unchecked")
		List<Parametro> parametri = (List<Parametro>) variables
				.get("parametrits");
		Parametro parametro1 = parametri.get(0);
		assertEquals(parametro1.getDescrizione(), "descrizionets1");
		assertEquals(parametro1.getNome(), "nomets1");
		assertEquals(parametro1.getTipo(), BENEFICIO.name());
		assertEquals(parametro1.getUnitaMisura(), "unitaMisuraTs1");
		assertEquals(parametro1.getValoreMassimo(), 0.0);
		assertEquals(parametro1.getValoreMinimo(), 0.0);
		assertEquals(parametro1.getPeso(), NESSUNO.name());

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoParametriPerModificaAAmministratore")
				.list();
		assertEquals(
				notificaErroreRecuperoParametriPerModificaAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoParametriPerModificaAPA")
				.list();
		assertEquals(notificaErroreRecuperoParametriPerModificaAPA.size(), 0);

		// INSERIMENTO DEL PARAMETRO 1
		taskService.complete(inserisciParametro.getId());
		inserisciParametri = taskService.createTaskQuery()
				.taskDefinitionKey("inserisciParametri").list();
		assertEquals(inserisciParametri.size(), 0);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerModifica")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// SELEZIONO IL PARAMETRO CREATO PER LA MODIFICA
		@SuppressWarnings("unchecked")
		List<Parametro> parametriAggiunti = (List<Parametro>) taskService
				.getVariable(elencoParametriTs.get(0).getId(),
						"parametriaggiunti");
		List<Parametro> parametriAggiuntiDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametriAggiunti.get(0) }));
		assertEquals(parametriAggiunti.size(), 2);
		propertiesForm = new HashMap<String, Object>();
		propertiesForm.put("parametriaggiuntiselezionati",
				parametriAggiuntiDaSelezionare);
		propertiesForm.put(OPERAZIONE, MODIFICA);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO LA CREAZIONE DEI TASK DI MODIFICA PARAMETRI
		List<Task> modificaParametri = taskService.createTaskQuery()
				.taskDefinitionKey("aggiornaParametri")
				.includeProcessVariables().list();
		assertEquals(modificaParametri.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI MODIFICA
		// DEL PARAMETRO 1
		Task modificaParametro = modificaParametri.get(0);
		variables = modificaParametro.getProcessVariables();
		@SuppressWarnings("unchecked")
		List<Parametro> parametriModifica = (List<Parametro>) variables
				.get("parametriaggiunti");
		parametro1 = parametriModifica.get(0);
		assertEquals(parametro1.getDescrizione(), "descrizionets1");
		assertEquals(parametro1.getNome(), "nomets1");
		assertEquals(parametro1.getTipo(), BENEFICIO.name());
		assertEquals(parametro1.getUnitaMisura(), "unitaMisuraTs1");
		assertEquals(parametro1.getValoreMassimo(), 0.0);
		assertEquals(parametro1.getValoreMinimo(), 0.0);
		assertEquals(parametro1.getPeso(), NESSUNO.name());

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI NON SONO STATE
		// MANDATE
		notificaErroreRecuperoParametriPerModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoParametriPerModificaAAmministratore")
				.list();
		assertEquals(
				notificaErroreRecuperoParametriPerModificaAAmministratore
						.size(),
				0);
		notificaErroreRecuperoParametriPerModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoParametriPerModificaAPA")
				.list();
		assertEquals(notificaErroreRecuperoParametriPerModificaAPA.size(), 0);

		// MODIFICA DEL PARAMETRO 1
		taskService.complete(modificaParametro.getId());
		modificaParametri = taskService.createTaskQuery()
				.taskDefinitionKey("aggiornaParametri").list();
		assertEquals(modificaParametri.size(), 0);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerModifica")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// SELEZIONO 1 ALTRO PARAMETRO
		@SuppressWarnings("unchecked")
		List<Parametro> altriParametriTs = (List<Parametro>) taskService
				.getVariable(elencoParametriTs.get(0).getId(), "parametrits");
		parametriTsDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { altriParametriTs.get(0) }));
		assertEquals(altriParametriTs.size(), 2);
		propertiesForm = new HashMap<String, Object>();
		propertiesForm.put("parametritsselezionati", parametriTsDaSelezionare);
		propertiesForm.put(OPERAZIONE, INSERIMENTO);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO LA CREAZIONE DEI TASK DI INSERIMENTO PARAMETRI
		inserisciParametri = taskService.createTaskQuery()
				.taskDefinitionKey("inserisciParametri")
				.includeProcessVariables().list();
		assertEquals(inserisciParametri.size(), 1);

		// VERIFICO CHE SIANO RIEMPITE LE VARIABILI DEL TASK DI MODIFICA
		// DEL PARAMETRO 1
		inserisciParametro = inserisciParametri.get(0);
		variables = inserisciParametro.getProcessVariables();
		@SuppressWarnings("unchecked")
		List<Parametro> altriParametri = (List<Parametro>) variables
				.get("parametrits");
		parametro1 = altriParametri.get(0);
		assertEquals(parametro1.getDescrizione(), "descrizionets1");
		assertEquals(parametro1.getNome(), "nomets1");
		assertEquals(parametro1.getTipo(), BENEFICIO.name());
		assertEquals(parametro1.getUnitaMisura(), "unitaMisuraTs1");
		assertEquals(parametro1.getValoreMassimo(), 0.0);
		assertEquals(parametro1.getValoreMinimo(), 0.0);
		assertEquals(parametro1.getPeso(), NESSUNO.name());

		// INSERIMENTO DEL PARAMETRO 2
		taskService.complete(inserisciParametro.getId());
		inserisciParametri = taskService.createTaskQuery()
				.taskDefinitionKey("inserisciParametri").list();
		assertEquals(inserisciParametri.size(), 0);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerModifica")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// ACCEDO ALLA MODIFICA DEI PREZZI
		propertiesForm = new HashMap<String, Object>();
		propertiesForm.put(OPERAZIONE, AVANTI);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO LA CREAZIONE DEL TASK DI MODIFICA PREZZI
		List<Task> modificaPrezzi = taskService.createTaskQuery()
				.taskName("Modifica Prezzi").includeProcessVariables().list();
		assertEquals(modificaPrezzi.size(), 1);

		// MODIFICA DEL PREZZO
		Task modificaPrezzo = modificaPrezzi.get(0);
		taskService.complete(modificaPrezzo.getId());
		modificaPrezzi = taskService.createTaskQuery()
				.taskName("Modifica Prezzi").list();
		assertEquals(modificaPrezzi.size(), 0);

		// VERIFICO CHE LE EMAIL NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAAmministratore").list();
		assertEquals(notificaErroreModificaAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAPA").list();
		assertEquals(notificaErroreModificaAPA.size(), 0);

		// VERIFICO CHE LA MODIFICA E' STATO ESEGUITA
		List<HistoricActivityInstance> modificaFasciaOrariaServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("modificaFasciaOrariaService").list();
		assertEquals(modificaFasciaOrariaServiceHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml",
			"it/vige/greenarea/bpm/pa/modifica_fasce_orarie.bpmn20.xml" })
	public void testNotificaErroreRecuperoParametriModificaGrave() {
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
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition letturaFasceOrarie = processiAttivi.get(0);
		ProcessDefinition modificaFasceOrarie = processiAttivi.get(1);
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		BpmnModel modificaFasceOrarieModel = repositoryService
				.getBpmnModel(modificaFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperaParametriPerModificaService = (ServiceTask) modificaFasceOrarieModel
				.getFlowElement("recuperaParametriPerModificaService");
		recuperaParametriPerModificaService
				.setImplementation(RecuperaParametriPerModificaConNotificaErroreRecupero.class
						.getName());
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(EmptyCancellaFasciaOraria.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", modificaFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// SELEZIONO LA FASCIA ORARIA
		@SuppressWarnings("unchecked")
		List<FasciaOraria> fasceOrarie = (List<FasciaOraria>) taskService
				.getVariable(elencoFasceOrarie.get(0).getId(), "fasceorarie");
		List<FasciaOraria> fasceOrarieDaSelezionare = new ArrayList<FasciaOraria>(
				asList(new FasciaOraria[] { fasceOrarie.get(0) }));
		assertEquals(fasceOrarie.size(), 2);
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put("fasceorarieselezionate", fasceOrarieDaSelezionare);
		operazione.put(OPERAZIONE, MODIFICA);
		taskService.complete(elencoFasceOrarie.get(0).getId(), operazione);

		// VERIFICO LA CREAZIONE DEL TASK DI MODIFICA FASCIA ORARIA
		List<Task> modificaFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("modificaFasciaOraria")
				.includeProcessVariables().list();
		assertEquals(modificaFasciaOraria.size(), 1);
		FasciaOraria fasciaOraria = (FasciaOraria) taskService.getVariable(
				modificaFasciaOraria.get(0).getId(), "fasciaoraria");
		assertNotNull(fasciaOraria);
		assertNotNull(fasciaOraria.getPrezzi());

		// MODIFICA FORM
		String ga = "nuova area geografica";
		fasciaOraria.setGa(ga);
		Date orarioInizio = new Date();
		fasciaOraria.setOrarioInizio(orarioInizio);
		Date orarioFine = new Date();
		fasciaOraria.setOrarioFine(orarioFine);
		fasciaOraria.setAperturaRichieste("29-lug-2014");
		Ripetizione ripetitivitaPolicy = MAI;
		fasciaOraria.setRipetitivitaPolicy(ripetitivitaPolicy.name());
		taskService.complete(modificaFasciaOraria.get(0).getId());

		// VERIFICO CHE NON SIANO CREATI I TASK DI INSERIMENTO PARAMETRI
		List<Task> inserisciParametri = taskService.createTaskQuery()
				.taskDefinitionKey("inserisciParametri")
				.includeProcessVariables().list();
		assertEquals(inserisciParametri.size(), 0);

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO PARAMETRI SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoParametriPerModificaAAmministratore")
				.list();
		assertEquals(
				notificaErroreRecuperoParametriPerModificaAAmministratore
						.size(),
				1);
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoParametriPerModificaAPA")
				.list();
		assertEquals(notificaErroreRecuperoParametriPerModificaAPA.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml",
			"it/vige/greenarea/bpm/pa/modifica_fasce_orarie.bpmn20.xml" })
	public void testNotificaErroreModificaLieve() {
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

		// AGGIUNGO UN ERRORE ALLA MODIFICA
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition letturaFasceOrarie = processiAttivi.get(0);
		ProcessDefinition modificaFasceOrarie = processiAttivi.get(1);
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		BpmnModel inserisciFasceOrarieModel = repositoryService
				.getBpmnModel(modificaFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperaParametriPerModificaService = (ServiceTask) inserisciFasceOrarieModel
				.getFlowElement("recuperaParametriPerModificaService");
		recuperaParametriPerModificaService
				.setImplementation(RecuperaParametriPerModificaPopolati.class
						.getName());
		ServiceTask modificaFasciaOrariaService = (ServiceTask) inserisciFasceOrarieModel
				.getFlowElement("modificaFasciaOrariaService");
		modificaFasciaOrariaService
				.setImplementation(ModificaFasciaOrariaConNotificaErroreModificaLieve.class
						.getName());
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(EmptyCancellaFasciaOraria.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", inserisciFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// SELEZIONO LA FASCIA ORARIA
		@SuppressWarnings("unchecked")
		List<FasciaOraria> fasceOrarie = (List<FasciaOraria>) taskService
				.getVariable(elencoFasceOrarie.get(0).getId(), "fasceorarie");
		List<FasciaOraria> fasceOrarieDaSelezionare = new ArrayList<FasciaOraria>(
				asList(new FasciaOraria[] { fasceOrarie.get(0) }));
		assertEquals(fasceOrarie.size(), 2);
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put("fasceorarieselezionate", fasceOrarieDaSelezionare);
		operazione.put(OPERAZIONE, MODIFICA);
		taskService.complete(elencoFasceOrarie.get(0).getId(), operazione);

		// VERIFICO LA CREAZIONE DEL TASK DI MODIFICA FASCIA ORARIA
		List<Task> modificaFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("modificaFasciaOraria")
				.includeProcessVariables().list();
		assertEquals(modificaFasciaOraria.size(), 1);
		FasciaOraria fasciaOraria = (FasciaOraria) taskService.getVariable(
				modificaFasciaOraria.get(0).getId(), "fasciaoraria");
		assertNotNull(fasciaOraria);
		assertNotNull(fasciaOraria.getPrezzi());

		// MODIFICA FORM
		String ga = "nuova area geografica";
		fasciaOraria.setGa(ga);
		Date orarioInizio = new Date();
		fasciaOraria.setOrarioInizio(orarioInizio);
		Date orarioFine = new Date();
		fasciaOraria.setOrarioFine(orarioFine);
		fasciaOraria.setAperturaRichieste("29-lug-2014");
		Ripetizione ripetitivitaPolicy = MAI;
		fasciaOraria.setRipetitivitaPolicy(ripetitivitaPolicy.name());
		taskService.complete(modificaFasciaOraria.get(0).getId());

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerModifica")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// VADO ALLA FORM DEI PREZZI
		@SuppressWarnings("unchecked")
		List<Parametro> parametriTs = (List<Parametro>) taskService
				.getVariable(elencoParametriTs.get(0).getId(), "parametrits");
		List<Parametro> parametriTsDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametriTs.get(0) }));
		assertEquals(parametriTs.size(), 2);
		Map<String, Object> propertiesForm = new HashMap<String, Object>();
		propertiesForm.put("parametritsselezionati", parametriTsDaSelezionare);
		propertiesForm.put(OPERAZIONE, AVANTI);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoParametriPerModificaAAmministratore")
				.list();
		assertEquals(
				notificaErroreRecuperoParametriPerModificaAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoParametriPerModificaAPA")
				.list();
		assertEquals(notificaErroreRecuperoParametriPerModificaAPA.size(), 0);

		// VERIFICO LA CREAZIONE DEL TASK DI INSERIMENTO PREZZI
		List<Task> modificaPrezzi = taskService.createTaskQuery()
				.taskName("Modifica Prezzi").includeProcessVariables().list();
		assertEquals(modificaPrezzi.size(), 1);

		// MODIFICA DEL PREZZO
		Task modificaPrezzo = modificaPrezzi.get(0);
		taskService.complete(modificaPrezzo.getId());
		modificaPrezzi = taskService.createTaskQuery()
				.taskName("Inserimento Prezzi").list();
		assertEquals(modificaPrezzi.size(), 0);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAAmministratore").list();
		assertEquals(notificaErroreModificaAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAPA").list();
		assertEquals(notificaErroreModificaAPA.size(), 1);

		// VERIFICO CHE IL SERVIZIO DI INSERIMENTO SIA STATO ESEGUITO 2 VOLTE
		List<HistoricActivityInstance> modificaFasciaOrariaServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("modificaFasciaOrariaService").list();
		assertEquals(modificaFasciaOrariaServiceHistory.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

	@Deployment(resources = {
			"it/vige/greenarea/bpm/pa/lettura_fasce_orarie.bpmn20.xml",
			"it/vige/greenarea/bpm/pa/modifica_fasce_orarie.bpmn20.xml" })
	public void testNotificaErroreModificaGrave() {
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

		// AGGIUNGO UN ERRORE ALLA MODIFICA
		List<ProcessDefinition> processiAttivi = repositoryService
				.createProcessDefinitionQuery().list();
		ProcessDefinition letturaFasceOrarie = processiAttivi.get(0);
		ProcessDefinition modificaFasceOrarie = processiAttivi.get(1);
		BpmnModel letturaFasceOrarieModel = repositoryService
				.getBpmnModel(letturaFasceOrarie.getId());
		BpmnModel modificaFasceOrarieModel = repositoryService
				.getBpmnModel(modificaFasceOrarie.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		ServiceTask recuperaParametriPerModificaService = (ServiceTask) modificaFasceOrarieModel
				.getFlowElement("recuperaParametriPerModificaService");
		recuperaParametriPerModificaService
				.setImplementation(RecuperaParametriPerModificaPopolati.class
						.getName());
		ServiceTask modificaFasciaOrariaService = (ServiceTask) modificaFasceOrarieModel
				.getFlowElement("modificaFasciaOrariaService");
		modificaFasciaOrariaService
				.setImplementation(ModificaFasciaOrariaConNotificaErroreModificaGrave.class
						.getName());
		ServiceTask recuperoFasceOrarieService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("visualizzaFasceOrarieService");
		recuperoFasceOrarieService
				.setImplementation(RecuperaFasceOrariePopolate.class.getName());
		ServiceTask cancellaFasciaOrariaService = (ServiceTask) letturaFasceOrarieModel
				.getFlowElement("cancellaFasciaOrariaService");
		cancellaFasciaOrariaService
				.setImplementation(EmptyCancellaFasciaOraria.class.getName());
		repositoryService.deleteDeployment(deployment.getId());
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", letturaFasceOrarieModel)
				.deploy();
		repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", modificaFasceOrarieModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey("letturaFasceOrarie");

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO FASCE ORARIE
		List<Task> elencoFasceOrarie = taskService.createTaskQuery()
				.taskDefinitionKey("elencoFasceOrarie")
				.includeProcessVariables().list();
		assertEquals(elencoFasceOrarie.size(), 1);

		// SELEZIONO LA FASCIA ORARIA
		@SuppressWarnings("unchecked")
		List<FasciaOraria> fasceOrarie = (List<FasciaOraria>) taskService
				.getVariable(elencoFasceOrarie.get(0).getId(), "fasceorarie");
		List<FasciaOraria> fasceOrarieDaSelezionare = new ArrayList<FasciaOraria>(
				asList(new FasciaOraria[] { fasceOrarie.get(0) }));
		assertEquals(fasceOrarie.size(), 2);
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put("fasceorarieselezionate", fasceOrarieDaSelezionare);
		operazione.put(OPERAZIONE, MODIFICA);
		taskService.complete(elencoFasceOrarie.get(0).getId(), operazione);

		// VERIFICO LA CREAZIONE DEL TASK DI MODIFICA FASCIA ORARIA
		List<Task> modificaFasciaOraria = taskService.createTaskQuery()
				.taskDefinitionKey("modificaFasciaOraria")
				.includeProcessVariables().list();
		assertEquals(modificaFasciaOraria.size(), 1);
		FasciaOraria fasciaOraria = (FasciaOraria) taskService.getVariable(
				modificaFasciaOraria.get(0).getId(), "fasciaoraria");
		assertNotNull(fasciaOraria);
		assertNotNull(fasciaOraria.getPrezzi());

		// MODIFICA FORM
		String ga = "nuova area geografica";
		fasciaOraria.setGa(ga);
		Date orarioInizio = new Date();
		fasciaOraria.setOrarioInizio(orarioInizio);
		Date orarioFine = new Date();
		fasciaOraria.setOrarioFine(orarioFine);
		fasciaOraria.setAperturaRichieste("29-lug-2014");
		Ripetizione ripetitivitaPolicy = MAI;
		fasciaOraria.setRipetitivitaPolicy(ripetitivitaPolicy.name());
		taskService.complete(modificaFasciaOraria.get(0).getId());

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO PARAMETRI
		List<Task> elencoParametriTs = taskService.createTaskQuery()
				.taskDefinitionKey("elencoParametriPerModifica")
				.includeProcessVariables().list();
		assertEquals(elencoParametriTs.size(), 1);

		// VADO ALLA FORM DEI PREZZI
		@SuppressWarnings("unchecked")
		List<Parametro> parametriTs = (List<Parametro>) taskService
				.getVariable(elencoParametriTs.get(0).getId(), "parametrits");
		List<Parametro> parametriTsDaSelezionare = new ArrayList<Parametro>(
				asList(new Parametro[] { parametriTs.get(0) }));
		assertEquals(parametriTs.size(), 2);
		Map<String, Object> propertiesForm = new HashMap<String, Object>();
		propertiesForm.put("parametritsselezionati", parametriTsDaSelezionare);
		propertiesForm.put(OPERAZIONE, AVANTI);
		taskService.complete(elencoParametriTs.get(0).getId(), propertiesForm);

		// VERIFICO CHE LE EMAIL DI ERRORE DI RECUPERO DATI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreRecuperoParametriPerModificaAAmministratore")
				.list();
		assertEquals(
				notificaErroreRecuperoParametriPerModificaAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreRecuperoParametriPerModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreRecuperoParametriPerModificaAPA")
				.list();
		assertEquals(notificaErroreRecuperoParametriPerModificaAPA.size(), 0);

		// VERIFICO LA CREAZIONE DEL TASK DI INSERIMENTO PREZZI
		List<Task> modificaPrezzi = taskService.createTaskQuery()
				.taskName("Modifica Prezzi").includeProcessVariables().list();
		assertEquals(modificaPrezzi.size(), 1);

		// MODIFICA DEL PREZZO
		Task modificaPrezzo = modificaPrezzi.get(0);
		taskService.complete(modificaPrezzo.getId());
		modificaPrezzi = taskService.createTaskQuery()
				.taskName("Modifica Prezzi").list();
		assertEquals(modificaPrezzi.size(), 0);

		// VERIFICO CHE LE EMAIL SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreModificaAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAAmministratore").list();
		assertEquals(notificaErroreModificaAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreModificaAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreModificaAPA").list();
		assertEquals(notificaErroreModificaAPA.size(), 1);

		// VERIFICO CHE IL SERVIZIO DI INSERIMENTO SIA STATO ESEGUITO 1 VOLTA
		List<HistoricActivityInstance> modificaFasciaOrariaServiceHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("modificaFasciaOrariaService").list();
		assertEquals(modificaFasciaOrariaServiceHistory.size(), 1);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}
}
