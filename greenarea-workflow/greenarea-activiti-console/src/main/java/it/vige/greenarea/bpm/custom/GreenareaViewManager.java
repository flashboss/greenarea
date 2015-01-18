package it.vige.greenarea.bpm.custom;

import it.vige.greenarea.bpm.custom.ui.dettaglio.DettaglioPage;
import it.vige.greenarea.bpm.custom.ui.home.DetailHomePage;

import org.activiti.explorer.DefaultViewManager;

public class GreenareaViewManager extends DefaultViewManager {

	private static final long serialVersionUID = -8345366082083562343L;

	public static final String MAIN_NAVIGATION_HOME = "home";
	public static final String MAIN_NAVIGATION_CONSORZIO = "consorzio";
	public static final String MAIN_NAVIGATION_LOGISTICA_URBANA = "logisticaurbana";

	public void showHomePage() {
		switchView(new DetailHomePage(), MAIN_NAVIGATION_HOME, null);
	}

	public void showHomePage(String id) {
		switchView(new DetailHomePage(id), MAIN_NAVIGATION_HOME, null);
	}

	public void showDettaglioPage(String processInstanceId) {
		switchView(new DettaglioPage(processInstanceId, null),
				MAIN_NAVIGATION_HOME, null);
	}

	@Override
	public void showDefaultPage() {
		mainWindow.showDefaultContent();
		showHomePage();
	}

}
