package it.vige.greenarea.bpm.operatorelogistico.test;

import static it.vige.greenarea.dto.Operazione.CHIUDI;
import it.vige.greenarea.bpm.GreenareaDemoData;
import it.vige.greenarea.bpm.mail.MyMessageHandlerFactory;
import it.vige.greenarea.bpm.operatorelogistico.service.anagrafeveicoli.RichiediVeicoliPopolati;
import it.vige.greenarea.dto.Veicolo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.engine.impl.test.ResourceActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.subethamail.smtp.server.SMTPServer;

public class AnagrafeVeicoliOperatoreLogisticoTest extends
		ResourceActivitiTestCase {

	private final static String USER_NAME = "tnt";

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	public AnagrafeVeicoliOperatoreLogisticoTest() {
		super("activiti.cfg-mem.xml");
	}

	@Deployment(resources = { "it/vige/greenarea/bpm/operatorelogistico/anagrafe_veicoli_operatore_logistico.bpmn20.xml" })
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
		repositoryService.deleteDeployment(deployment.getId());
		deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn", aggiornaStatoVeicoliModel)
				.deploy();

		// INIZIO PROCESSO
		runtimeService.startProcessInstanceByKey(
				"anagrafeVeicoliOperatoreLogistico", variables);

		// VERIFICO LA CREAZIONE DEL TASK DI ELENCO VEICOLI
		List<Task> elencoVeicoli = taskService.createTaskQuery()
				.taskDefinitionKey("elencoVeicoli").includeProcessVariables()
				.list();
		assertEquals(elencoVeicoli.size(), 1);

		// CHIUDO
		@SuppressWarnings("unchecked")
		List<Veicolo> veicoli = (List<Veicolo>) taskService.getVariable(
				elencoVeicoli.get(0).getId(), "veicoli");
		assertEquals(veicoli.size(), 2);
		Map<String, Object> operazione = new HashMap<String, Object>();
		operazione.put("operazione", CHIUDI);
		taskService.complete(elencoVeicoli.get(0).getId(), operazione);

		// RIPULISCO IL DB
		greenareaDemoData.deleteAllIdentities(identityService);
		greenareaDemoData.deleteAllHistories(historyService);
		greenareaDemoData.deleteAllIDeployments(repositoryService);

		// FERMO IL SERVER DI POSTA
		smtpServer.stop();
	}

}
