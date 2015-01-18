package it.vige.greenarea.bpm.tempo.test;

import static org.activiti.bpmn.model.ImplementationType.IMPLEMENTATION_TYPE_CLASS;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.tempo.costruiscimissioni.EmptyAggiornamentoDellaMissione;
import it.vige.greenarea.bpm.tempo.costruiscimissioni.EmptyCostruzioneMissionePerIlVeicolo;
import it.vige.greenarea.bpm.tempo.costruiscimissioni.EmptyRecuperoDatiDeiVeicoliEAutisti;
import it.vige.greenarea.bpm.tempo.costruiscimissioni.EmptyRecuperoDelleConsegneEDeiRitiri;
import it.vige.greenarea.bpm.tempo.costruiscimissioni.EmptyRecuperoDellePolicy;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.NotificaConsegneERitiriMancanti;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.NotificaDatiAutistiEVeicoliMancanti;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.NotificaErroreAggiornamentoMissione;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.NotificaErroreInserimentoConsegnaPerLaMissione;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.NotificaErroreReperimentoConsegneERitiri;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.NotificaErroreReperimentoDatiAutistiEVeicoli;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.NotificaErroreReperimentoPolicy;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.RecuperoAutistiVeicoliConNotificaVeicoliInsufficienti;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.RecuperoAutistiVeicoliErroreGrave;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.RecuperoAutistiVeicoliPopolati;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.RecuperoConsegneERitiriErroreGrave;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.RecuperoConsegneRitiriConNotificaVeicoliInsufficienti;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.RecuperoConsegneRitiriPopolati;
import it.vige.greenarea.bpm.tempo.service.costruiscimissioni.SollecitoPolicyMancanti;

import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.test.ResourceActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.test.Deployment;
import org.subethamail.smtp.server.SMTPServer;

public class CostruisciMissioniTest extends ResourceActivitiTestCase {

	private final static String USER_NAME = "amministratore";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public CostruisciMissioniTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(RecuperoAutistiVeicoliPopolati.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(RecuperoConsegneRitiriPopolati.class
						.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(EmptyAggiornamentoDellaMissione.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(EmptyCostruzioneMissionePerIlVeicolo.class
						.getName());
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy.setImplementation(EmptyRecuperoDellePolicy.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 0);

		// VERIFICO CHE LE EMAIL DI AVVENUTA COSTRUZIONE MISSIONI SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				2);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 2);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 2);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 10);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 10);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
	public void testErroreReperimentoPolicy() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy
				.setImplementation(NotificaErroreReperimentoPolicy.class
						.getName());
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(EmptyRecuperoDatiDeiVeicoliEAutisti.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(EmptyRecuperoDelleConsegneEDeiRitiri.class
						.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(EmptyAggiornamentoDellaMissione.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(EmptyCostruzioneMissionePerIlVeicolo.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				2);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 0);

		// VERIFICO CHE L'EMAIL DI AVVENUTA COSTRUZIONE MISSIONI NON E' STATA
		// MANDATA
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY NON E'
		// STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 0);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI NON E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE NON E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 0);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO NON E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
	public void testSollecitoPolicyMancanti() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy.setImplementation(SollecitoPolicyMancanti.class
				.getName());
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(EmptyRecuperoDatiDeiVeicoliEAutisti.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(EmptyRecuperoDelleConsegneEDeiRitiri.class
						.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(EmptyAggiornamentoDellaMissione.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(EmptyCostruzioneMissionePerIlVeicolo.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 2);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 2);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 0);

		// VERIFICO CHE L'EMAIL DI AVVENUTA COSTRUZIONE MISSIONI E' STATA
		// MANDATA
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 0);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 0);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
	public void testRecuperoDatiVeicoliAutistiMancanti() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(NotificaDatiAutistiEVeicoliMancanti.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(RecuperoConsegneRitiriPopolati.class
						.getName());
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy.setImplementation(EmptyRecuperoDellePolicy.class
				.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(EmptyAggiornamentoDellaMissione.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(EmptyCostruzioneMissionePerIlVeicolo.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 1);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 1);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE E' STATA MANDATA
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 2);

		// VERIFICO CHE L'EMAIL DI AVVENUTA COSTRUZIONE MISSIONI E' STATA
		// MANDATA
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				2);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 2);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 2);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 2);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
	public void testRecuperoDatiVeicoliAutistiErroreSistema() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(NotificaErroreReperimentoDatiAutistiEVeicoli.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(RecuperoConsegneRitiriPopolati.class
						.getName());
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy.setImplementation(EmptyRecuperoDellePolicy.class
				.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(EmptyAggiornamentoDellaMissione.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(EmptyCostruzioneMissionePerIlVeicolo.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				1);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				1);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE SONO STATE MANDATE
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 2);

		// VERIFICO CHE L'EMAIL DI AVVENUTA COSTRUZIONE MISSIONI SONO STATE
		// MANDATE
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				2);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 2);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 2);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 2);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
	public void testRecuperoDatiVeicoliAutistiErroreGrave() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(RecuperoAutistiVeicoliErroreGrave.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(RecuperoConsegneRitiriPopolati.class
						.getName());
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy.setImplementation(EmptyRecuperoDellePolicy.class
				.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(EmptyAggiornamentoDellaMissione.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(EmptyCostruzioneMissionePerIlVeicolo.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 0);

		// VERIFICO CHE L'EMAIL DI AVVENUTA COSTRUZIONE MISSIONI NON E' STATA
		// MANDATA
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI NON E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 0);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE NON E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 0);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO NON E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
	public void testNotificaErroreInserimentoConsegnaPerLaMissione() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(RecuperoAutistiVeicoliPopolati.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(RecuperoConsegneRitiriPopolati.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(NotificaErroreInserimentoConsegnaPerLaMissione.class
						.getName());
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy.setImplementation(EmptyRecuperoDellePolicy.class
				.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(EmptyAggiornamentoDellaMissione.class
						.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				1);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 0);

		// VERIFICO CHE L'EMAIL DI AVVENUTA COSTRUZIONE MISSIONI E' STATA
		// MANDATA
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				2);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 2);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 2);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 9);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 10);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
	public void testNotificaErroreAggiornamentoMissione() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(RecuperoAutistiVeicoliPopolati.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(RecuperoConsegneRitiriPopolati.class
						.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(NotificaErroreAggiornamentoMissione.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(EmptyCostruzioneMissionePerIlVeicolo.class
						.getName());
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy.setImplementation(EmptyRecuperoDellePolicy.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				1);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				1);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 0);

		// VERIFICO CHE L'EMAIL DI AVVENUTA COSTRUZIONE MISSIONI E' STATA
		// MANDATA
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				2);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 2);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 2);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 10);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 10);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
	public void testRecuperoDelleConsegneEDeiRitiriErroreSistema() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(RecuperoAutistiVeicoliPopolati.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(NotificaErroreReperimentoConsegneERitiri.class
						.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(EmptyAggiornamentoDellaMissione.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(EmptyCostruzioneMissionePerIlVeicolo.class
						.getName());
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy.setImplementation(EmptyRecuperoDellePolicy.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				1);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				1);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 0);

		// VERIFICO CHE L'EMAIL DI AVVENUTA COSTRUZIONE MISSIONI E' STATA
		// MANDATA
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				1);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 2);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 2);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 5);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 5);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
	public void testRecuperoDelleConsegneEDeiRitiriErroreGrave() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(RecuperoAutistiVeicoliPopolati.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(RecuperoConsegneERitiriErroreGrave.class
						.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(EmptyAggiornamentoDellaMissione.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(EmptyCostruzioneMissionePerIlVeicolo.class
						.getName());
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy.setImplementation(EmptyRecuperoDellePolicy.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 0);

		// VERIFICO CHE L'EMAIL DI AVVENUTA COSTRUZIONE MISSIONI NON E' STATA
		// MANDATA
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 2);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 1);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE NON E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 0);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO NON E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 0);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
	public void testRecuperoDelleConsegneEDeiRitiriErroreDatiMancanti() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(RecuperoAutistiVeicoliPopolati.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(NotificaConsegneERitiriMancanti.class
						.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(EmptyAggiornamentoDellaMissione.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(EmptyCostruzioneMissionePerIlVeicolo.class
						.getName());
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy.setImplementation(EmptyRecuperoDellePolicy.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 0);

		// VERIFICO CHE L'EMAIL DI AVVENUTA COSTRUZIONE MISSIONI E' STATA
		// MANDATA
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				1);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 2);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 2);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 5);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 5);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

	@Deployment(resources = { "it/vige/greenarea/bpm/tempo/costruisci_missioni.bpmn20.xml" })
	public void testNotificaVeicoliInsufficientiPerTutteLeConsegne() {
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

		// INIZIO IL PROCESSO
		ProcessDefinition costruisciMissioni = repositoryService
				.createProcessDefinitionQuery().singleResult();
		BpmnModel costruisciMissioniModel = repositoryService
				.getBpmnModel(costruisciMissioni.getId());
		org.activiti.engine.repository.Deployment deployment = repositoryService
				.createDeploymentQuery().singleResult();
		StartEvent timerCostruisciMissioni = (StartEvent) costruisciMissioniModel
				.getFlowElement("timerCostruisciMissioni");
		timerCostruisciMissioni.getEventDefinitions().clear();
		SequenceFlow flow8 = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flow8");
		SequenceFlow flowRecuperoPolicy = (SequenceFlow) costruisciMissioniModel
				.getFlowElement("flowRecuperoPolicy");
		flowRecuperoPolicy.setTargetRef(flow8.getTargetRef());
		flowRecuperoPolicy.getExecutionListeners().addAll(
				flow8.getExecutionListeners());
		Process costruisciMissioniProcess = costruisciMissioniModel
				.getProcesses().get(0);
		costruisciMissioniProcess.removeFlowElement("flow8");
		costruisciMissioniProcess
				.removeFlowElement("importaConsegneERitiriEseguito");
		ServiceTask recuperoDatiDeiVeicoliEAutisti = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDatiDeiVeicoliEAutisti");
		recuperoDatiDeiVeicoliEAutisti
				.setImplementation(RecuperoAutistiVeicoliConNotificaVeicoliInsufficienti.class
						.getName());
		ServiceTask recuperoDelleConsegneEDeiRitiri = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDelleConsegneEDeiRitiri");
		recuperoDelleConsegneEDeiRitiri
				.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
		recuperoDelleConsegneEDeiRitiri
				.setImplementation(RecuperoConsegneRitiriConNotificaVeicoliInsufficienti.class
						.getName());
		ServiceTask aggiornamentoDellaMissione = (ServiceTask) costruisciMissioniModel
				.getFlowElement("aggiornamentoDellaMissione");
		aggiornamentoDellaMissione
				.setImplementation(EmptyAggiornamentoDellaMissione.class
						.getName());
		ServiceTask costruzioneMissionePerIlVeicolo = (ServiceTask) costruisciMissioniModel
				.getFlowElement("costruzioneMissionePerIlVeicolo");
		costruzioneMissionePerIlVeicolo
				.setImplementation(EmptyCostruzioneMissionePerIlVeicolo.class
						.getName());
		ServiceTask recuperoDellePolicy = (ServiceTask) costruisciMissioniModel
				.getFlowElement("recuperoDellePolicy");
		recuperoDellePolicy.setImplementation(EmptyRecuperoDellePolicy.class
				.getName());
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", costruisciMissioniModel)
				.deploy();
		runtimeService.startProcessInstanceByKey("costruisciMissioni");

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO POLICY NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaErroreReperimentoPolicyAAmministratore")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaErroreReperimentoPolicyAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoPolicyAOperatoriLogistici")
				.list();
		assertEquals(notificaErroreReperimentoPolicyAOperatoriLogistici.size(),
				0);

		// VERIFICO CHE LE EMAIL DI SOLLECITO POLICY MANCANTI NON SONO STATE
		// MANDATE
		List<HistoricActivityInstance> sollecitoPolicyMancantiAPA = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAPA").list();
		assertEquals(sollecitoPolicyMancantiAPA.size(), 0);
		List<HistoricActivityInstance> sollecitoPolicyMancantiAOperatoriLogistici = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("sollecitoPolicyMancantiAOperatoriLogistici")
				.list();
		assertEquals(sollecitoPolicyMancantiAOperatoriLogistici.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA DATI AUTISTI MANCANTI NON SONO
		// STATE MANDATE
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAAmministratore")
				.list();
		assertEquals(notificaDatiAutistiMancantiAAmministratore.size(), 0);
		List<HistoricActivityInstance> notificaDatiAutistiMancantiAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaDatiAutistiMancantiAOperatoreLogistico")
				.list();
		assertEquals(notificaDatiAutistiMancantiAOperatoreLogistico.size(), 0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO DATI AUTISTI E
		// VEICOLI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAAmministratore
						.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoDatiAutistiEVeicoliAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE REPERIMENTO CONSEGNE E
		// RITIRI NON SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAAmministratore")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreReperimentoConsegneERitiriAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreReperimentoConsegneERitiriAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreReperimentoConsegneERitiriAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE LE EMAIL DI NOTIFICA ERRORE AGGIORNAMENTO MISSIONE NON
		// SONO STATE MANDATE
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAAmministratore = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAAmministratore")
				.list();
		assertEquals(notificaErroreAggiornamentoMissioneAAmministratore.size(),
				0);
		List<HistoricActivityInstance> notificaErroreAggiornamentoMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreAggiornamentoMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreAggiornamentoMissioneAOperatoreLogistico.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA ERRORE INSERIMENTO CONSEGNA PER LA
		// MISSIONE NON E' STATA MANDATA
		List<HistoricActivityInstance> notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico")
				.list();
		assertEquals(
				notificaErroreInserimentoConsegnaPerLaMissioneAOperatoreLogistico
						.size(),
				0);

		// VERIFICO CHE L'EMAIL DI NOTIFICA VEICOLI INSUFFICIENTI PER TUTTE LE
		// CONSEGNE E' STATA MANDATA
		List<HistoricActivityInstance> notificaVeicoliInsufficientiPerTutteLeConsegne = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("notificaVeicoliInsufficientiPerTutteLeConsegne")
				.list();
		assertEquals(notificaVeicoliInsufficientiPerTutteLeConsegne.size(), 2);

		// VERIFICO CHE L'EMAIL DI AVVENUTA COSTRUZIONE MISSIONI E' STATA
		// MANDATA
		List<HistoricActivityInstance> notificaAvvenutaCostruzioneMissioniAOperatoreLogistico = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(
						"notificaAvvenutaCostruzioneMissioniAOperatoreLogistico")
				.list();
		assertEquals(
				notificaAvvenutaCostruzioneMissioniAOperatoreLogistico.size(),
				2);

		// VERIFICO CHE IL RECUPERO DELLE POLICY E' STATO EFFETTUATO
		List<HistoricActivityInstance> recuperoDellePolicyHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDellePolicy").list();
		assertEquals(recuperoDellePolicyHistory.size(), 1);

		// VERIFICO CHE IL RECUPERO DATI VEICOLI E AUTISTI DELLE POLICY E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDatiDeiVeicoliEAutistiHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDatiDeiVeicoliEAutisti").list();
		assertEquals(recuperoDatiDeiVeicoliEAutistiHistory.size(), 2);

		// VERIFICO CHE IL RECUPERO DELLE CONSEGNE E DEI RITIRI E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> recuperoDelleConsegneEDeiRitiriHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("recuperoDelleConsegneEDeiRitiri").list();
		assertEquals(recuperoDelleConsegneEDeiRitiriHistory.size(), 2);

		// VERIFICO CHE L'AGGIORNAMENTO DELLA MISSIONE E' STATO EFFETTUATO
		List<HistoricActivityInstance> aggiornamentoDellaMissioneHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("aggiornamentoDellaMissione").list();
		assertEquals(aggiornamentoDellaMissioneHistory.size(), 2);

		// VERIFICO CHE LA COSTRUZIONE MISSIONE PER IL VEICOLO E' STATO
		// EFFETTUATO
		List<HistoricActivityInstance> costruzioneMissionePerIlVeicoloHistory = historyService
				.createHistoricActivityInstanceQuery()
				.activityId("costruzioneMissionePerIlVeicolo").list();
		assertEquals(costruzioneMissionePerIlVeicoloHistory.size(), 2);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();

	}

}
