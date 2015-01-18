package it.vige.greenarea.bpm.custom.ui.pa;

import static com.vaadin.ui.themes.Reindeer.LABEL_H2;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;

import org.activiti.engine.RepositoryService;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.ui.AbstractTablePage;
import org.activiti.explorer.ui.custom.DetailPanel;

import com.vaadin.data.Item;
import com.vaadin.ui.Label;

public class PADetailPanel extends DetailPanel {

	private static final long serialVersionUID = 7170006501646549644L;

	private I18nManager i18nManager = get().getI18nManager();

	private transient RepositoryService repositoryService = getDefaultProcessEngine()
			.getRepositoryService();

	private AbstractTablePage parentPage;

	public PADetailPanel(Item item, AbstractTablePage parentPage) {
		super();
		this.parentPage = parentPage;
		addTitle(item);
		addPage(item);
	}

	public AbstractTablePage getParentPage() {
		return parentPage;
	}

	public void setParentPage(AbstractTablePage parentPage) {
		this.parentPage = parentPage;
	}

	private void addTitle(Item item) {
		if (item != null) {
			String name = (String) item.getItemProperty("name").getValue();
			Label title = new Label(name.toUpperCase());
			title.addStyleName(LABEL_H2);
			addComponent(title);
		} else {
			String name = i18nManager.getMessage(MAIN_MENU_PA_HOME);
			Label title = new Label(name.toUpperCase());
			title.addStyleName(LABEL_H2);
			addComponent(title);
		}
	}

	private void addPage(Item item) {
		if (item != null) {
			String id = (String) item.getItemProperty("id").getValue();
			switch (id) {
			case "0-0":
				addDetailComponent(new DefinizioneAreaGeografica());
				break;
			case "0-1-0":
				String processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("letturaParametri")
						.latestVersion().singleResult().getId();
				addDetailComponent(new LetturaParametri(processDefinitionId,
						this));
				break;
			case "0-1-1":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("letturaFasceOrarie")
						.latestVersion().singleResult().getId();
				addDetailComponent(new LetturaFasceOrarie(processDefinitionId,
						this));
				break;
			case "1-0":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery()
						.active()
						.processDefinitionKey("visualizzaMissioniAutorizzatePA")
						.latestVersion().singleResult().getId();
				addDetailComponent(new VisualizzaMissioniAutorizzate(
						processDefinitionId, this));
				break;
			case "1-1":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("simulazioneMissioni")
						.latestVersion().singleResult().getId();
				addDetailComponent(new SimulazioneMissioni(processDefinitionId,
						this));
				break;
			case "3-0":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("accessoInGA")
						.latestVersion().singleResult().getId();
				addDetailComponent(new AccessoInGA(
						processDefinitionId, this));
				break;
			case "3-1":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("richiediReportMissioniPA")
						.latestVersion().singleResult().getId();
				addDetailComponent(new RichiediReportMissioni(
						processDefinitionId, this));
				break;
			case "3-2":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery()
						.active()
						.processDefinitionKey("impattoAmbientale")
						.latestVersion().singleResult().getId();
				addDetailComponent(new ImpattoAmbientale(
						processDefinitionId, this));
				break;
			}
		} else {
			addDetailComponent(new PAHomePage());
		}
	}
}
