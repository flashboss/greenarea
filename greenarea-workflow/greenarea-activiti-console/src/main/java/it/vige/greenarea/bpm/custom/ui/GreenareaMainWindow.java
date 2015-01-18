package it.vige.greenarea.bpm.custom.ui;

import static it.vige.greenarea.Constants.BASE_URI_TAP;
import static it.vige.greenarea.Constants.OPERATORE_LOGISTICO;
import static it.vige.greenarea.Constants.PA;
import static it.vige.greenarea.Constants.SOCIETA_DI_TRASPORTO;
import static it.vige.greenarea.Constants.TRASPORTATORE_AUTONOMO;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.BLUE_THEME;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.GREEN_THEME;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.ORANGE_THEME;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_LOGIN_PAGE;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.THEME;
import it.vige.greenarea.bpm.UserConverter;
import it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaMainLayout;

import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.explorer.ui.MainWindow;

public class GreenareaMainWindow extends MainWindow {

	private static final long serialVersionUID = -3099584489453290517L;
	public static int i = 0;

	public GreenareaMainWindow() {
	}

	public void showDefaultContent() {
		UserConverter userConverter = new UserConverter();
		List<Group> groups = get().getLoggedInUser().getGroups();
		if (userConverter.isUserInGroup(groups, PA))
			setTheme(BLUE_THEME);
		else if (userConverter.isUserInGroup(groups, OPERATORE_LOGISTICO))
			setTheme(ORANGE_THEME);
		else if (userConverter.isUserInGroup(groups, SOCIETA_DI_TRASPORTO))
			setTheme(GREEN_THEME);
		else if (userConverter.isUserInGroup(groups, TRASPORTATORE_AUTONOMO))
			setTheme(GREEN_THEME);
		else
			setTheme(THEME);
		showingLoginPage = false;
		removeStyleName(STYLE_LOGIN_PAGE);
		addStyleName("Default style"); // Vaadin bug: must set something or old
										// style (eg. login page style) is not
										// overwritten

		// init general look and feel
		mainLayout = new GreenareaMainLayout();
		setContent(mainLayout);

		// init hidden components
		initHiddenComponents();

		if (i == 0) {
			changeImage();
			i++;
		} else {
			i = 0;
		}
	}

	private void changeImage() {
		String variables = "var images = ['banner_vige.png', 'torino_banner.png', 'tnt_banner.png', 'banner_iveco.png'];"
				+ "var i = 0; var header = document.getElementById('header'); ";
		String changeImage = "console.log(i);"
				+ "if (header != null) { header.style.backgroundImage = 'url(/greenarea-activiti-console/VAADIN/themes/activiti/img/'+images[i]+')';"
				+ "if(i < images.length-1) {i++;} else {i = 0;}}";
		String script = variables + "window.setInterval(\"console.log(i);"
				+ changeImage + "\", 10000);";
		executeJavaScript(script);
	}

	public void callCounter() {
		String variables = "var url = '"
				+ BASE_URI_TAP
				+ "/veicoliInGA';"
				+ "var client = new XMLHttpRequest();"
				+ "var counter = 0;"
				+ "var footer = document.getElementsByClassName('v-table-footer-container')[0];";
		String callCounter = "if (footer != null) { client.open('GET', url, false);"
				+ "client.send();"
				+ "counter = client.responseText;"
				+ "footer.textContent = counter;}";
		String script = variables + "window.setInterval(\"console.log(i);"
				+ callCounter + "\", 10000);";
		executeJavaScript(script);
	}

}
