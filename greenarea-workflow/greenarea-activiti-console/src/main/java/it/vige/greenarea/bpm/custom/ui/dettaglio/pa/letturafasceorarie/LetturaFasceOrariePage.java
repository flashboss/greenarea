package it.vige.greenarea.bpm.custom.ui.dettaglio.pa.letturafasceorarie;

import static it.vige.greenarea.bpm.custom.GreenareaMessages.FASCE_ORARIE_ERRORE_MISSIONI_ASSOCIATE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATINONCORRETTI;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.custom.GreenareaViewManager;
import it.vige.greenarea.bpm.custom.ui.dettaglio.DettaglioPage;
import it.vige.greenarea.bpm.risultato.Messaggio;

import org.activiti.engine.HistoryService;
import org.activiti.engine.task.Task;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.NotificationManager;
import org.slf4j.Logger;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public class LetturaFasceOrariePage extends DettaglioPage {

	private static final long serialVersionUID = 6491993834546632634L;

	private Logger logger = getLogger(getClass());

	private HistoryService historyService = getDefaultProcessEngine()
			.getHistoryService();

	private NotificationManager notificationManager = get()
			.getNotificationManager();
	protected I18nManager i18nManager = get().getI18nManager();

	public LetturaFasceOrariePage(String processInstanceId, Label mainTitle) {
		super(processInstanceId, mainTitle);
	}

	@Override
	protected Component createDetailComponent(String id) {
		Task task = taskService.createTaskQuery().taskId(id).singleResult();
		Component detailComponent = new LetturaFasceOrariePanel(task, this);
		return detailComponent;
	}

	@Override
	public void refreshSelectNext() {
		Messaggio messaggio = null;
		try {
			messaggio = (Messaggio) historyService
					.createHistoricProcessInstanceQuery()
					.includeProcessVariables().orderByProcessInstanceEndTime()
					.desc().list().get(0).getProcessVariables()
					.get("messaggio");
		} catch (Exception ex) {
			logger.debug("Processo non trovato");
		}
		if (messaggio != null)
			if (messaggio.getTipo().equals(ERROREDATINONCORRETTI))
				notificationManager
						.showErrorNotification(
								messaggio.getCategoria() + "",
								i18nManager
										.getMessage(FASCE_ORARIE_ERRORE_MISSIONI_ASSOCIATE
												+ ""));
		((GreenareaViewManager) get().getViewManager()).showHomePage("4");
	}

}
