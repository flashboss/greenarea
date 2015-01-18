package it.vige.greenarea.bpm.custom.ui.dettaglio.admin.definizionefiltri;

import it.vige.greenarea.bpm.custom.ui.dettaglio.DettaglioPage;

import org.activiti.engine.task.Task;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public class DefinizioneFiltriPage extends DettaglioPage {

	private static final long serialVersionUID = -3435304366197474391L;

	public DefinizioneFiltriPage(String processInstanceId, Label mainTitle) {
		super(processInstanceId, mainTitle);
	}

	@Override
	protected Component createDetailComponent(String id) {
		Task task = taskService.createTaskQuery().taskId(id).singleResult();
		Component detailComponent = new DefinizioneFiltriPanel(task, this);
		return detailComponent;
	}

}
