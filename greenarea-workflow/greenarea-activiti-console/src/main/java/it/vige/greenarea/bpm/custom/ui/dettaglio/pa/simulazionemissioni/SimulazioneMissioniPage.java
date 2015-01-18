package it.vige.greenarea.bpm.custom.ui.dettaglio.pa.simulazionemissioni;

import static org.activiti.explorer.ExplorerApp.get;
import it.vige.greenarea.bpm.custom.GreenareaViewManager;
import it.vige.greenarea.bpm.custom.ui.dettaglio.DettaglioPage;

import org.activiti.engine.task.Task;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public class SimulazioneMissioniPage extends DettaglioPage {

	private static final long serialVersionUID = 1L;

	public SimulazioneMissioniPage(String processInstanceId,
			Label mainTitle) {
		super(processInstanceId, mainTitle);
	}

	@Override
	protected Component createDetailComponent(String id) {
		Task task = taskService.createTaskQuery().taskId(id).singleResult();
		Component detailComponent = new SimulazioneMissioniPanel(task,
				this);
		return detailComponent;
	}

	@Override
	public void refreshSelectNext() {
		((GreenareaViewManager) get().getViewManager()).showHomePage("7");
	}

}
