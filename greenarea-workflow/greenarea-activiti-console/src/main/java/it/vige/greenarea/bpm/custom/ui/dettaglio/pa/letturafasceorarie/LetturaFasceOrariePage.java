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
