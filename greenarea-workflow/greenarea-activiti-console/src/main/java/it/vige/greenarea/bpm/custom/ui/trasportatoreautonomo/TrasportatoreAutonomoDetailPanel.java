package it.vige.greenarea.bpm.custom.ui.trasportatoreautonomo;

import static com.vaadin.ui.themes.Reindeer.LABEL_H2;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;

import org.activiti.engine.RepositoryService;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.ui.AbstractTablePage;
import org.activiti.explorer.ui.custom.DetailPanel;

import com.vaadin.data.Item;
import com.vaadin.ui.Label;

public class TrasportatoreAutonomoDetailPanel extends DetailPanel {

	private static final long serialVersionUID = 4842801758674679226L;

	private I18nManager i18nManager = get().getI18nManager();

	private transient RepositoryService repositoryService = getDefaultProcessEngine()
			.getRepositoryService();

	private AbstractTablePage parentPage;

	public TrasportatoreAutonomoDetailPanel(Item item,
			AbstractTablePage parentPage) {
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
			String name = i18nManager
					.getMessage(MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME);
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
				String processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("aggiornaStatoVeicolo")
						.latestVersion().singleResult().getId();
				addDetailComponent(new AggiornaStatoVeicolo(
						processDefinitionId, this));
				break;
			case "1-0":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery()
						.active()
						.processDefinitionKey("visualizzaMissioniAutorizzateTr")
						.latestVersion().singleResult().getId();
				addDetailComponent(new VisualizzaMissioniAutorizzate(
						processDefinitionId, this));
				break;
			case "1-1":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("monitoringMissioniTr")
						.latestVersion().singleResult().getId();
				addDetailComponent(new MonitoringMissioni(processDefinitionId,
						this));
				break;
			case "2-0":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("posizioneVeicoloTr")
						.latestVersion().singleResult().getId();
				addDetailComponent(new PosizioneVeicolo(processDefinitionId,
						this));
				break;
			case "2-1":
				processDefinitionId = repositoryService
						.createProcessDefinitionQuery().active()
						.processDefinitionKey("performanceVeicoliTr")
						.latestVersion().singleResult().getId();
				addDetailComponent(new PerformanceVeicoli(processDefinitionId,
						this));
				break;
			}
		} else {
			addDetailComponent(new TrasportatoreAutonomoHomePage());
		}
	}
}
