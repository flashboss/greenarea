package it.vige.greenarea.bpm.custom.ui.mainlayout;

import static it.vige.greenarea.Constants.GENOVA;
import static it.vige.greenarea.Constants.MILANO;
import static it.vige.greenarea.Constants.PA;
import static it.vige.greenarea.Constants.TORINO;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_NAV;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_MAIN_CONTENT;
import it.vige.greenarea.bpm.UserConverter;

import org.activiti.explorer.ui.mainlayout.MainLayout;
import org.activiti.explorer.ui.mainlayout.MainMenuBarFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

public class GreenareaMainLayout extends MainLayout {

	private static final long serialVersionUID = 9133717133432376603L;

	protected VerticalLayout header;
	VerticalLayout head;
	VerticalLayout center;
	private CustomLayout customLayout;

	@Override
	public void setSizeFull() {
		setWidth(80, UNITS_PERCENTAGE);
		setHeight(100, UNITS_PERCENTAGE);
	}

	@Override
	protected void initFooter() {
	}

	@Override
	protected void initHeader() {
		customLayout = new CustomLayout("home");
		addComponent(customLayout);
		initTitle2();

		this.mainMenuBar = get().getComponentFactory(MainMenuBarFactory.class)
				.create();
		customLayout.addComponent(mainMenuBar, "nav");

	}

	protected void initCenter() {
		center = new VerticalLayout();
		center.addStyleName(STYLE_NAV);

		this.mainMenuBar = get().getComponentFactory(MainMenuBarFactory.class)
				.create();
		center.addComponent(mainMenuBar);

		head.addComponent(center);
	}

	protected void initTitle2() {
		CustomLayout title = null;
		UserConverter userConverter = new UserConverter();
		if (userConverter
				.isUserInGroup(get().getLoggedInUser().getGroups(), PA)) {

			if (userConverter.isUserInGroup(
					get().getLoggedInUser().getGroups(), TORINO))
				title = new CustomLayout("logotorino");
			else if (userConverter.isUserInGroup(get().getLoggedInUser()
					.getGroups(), GENOVA))
				title = new CustomLayout("logogenova");
			else if (userConverter.isUserInGroup(get().getLoggedInUser()
					.getGroups(), MILANO))
				title = new CustomLayout("logomilano");
			customLayout.addComponent(title, "logo");

		}
	}

	@Override
	protected void initMainMenuBar() {
	}

	@Override
	public void setMainNavigation(String navigation) {
	}

	@Override
	public void setMainContent(Component mainContent) {
		customLayout.addComponent(mainContent, "main-wrap");
	}

	@Override
	protected void initMain() {
		customLayout.setSizeFull();
		customLayout.addStyleName(STYLE_MAIN_CONTENT);
		addComponent(customLayout);
		setExpandRatio(customLayout, 1.0f);
	}

}
