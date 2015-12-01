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
package it.vige.greenarea.bpm.custom.ui.mainlayout;

import static it.vige.greenarea.Constants.POMEZIA;
import static it.vige.greenarea.Constants.LIVORNO;
import static it.vige.greenarea.Constants.PA;
import static it.vige.greenarea.Constants.GUIDONIA;
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
					get().getLoggedInUser().getGroups(), GUIDONIA))
				title = new CustomLayout("logoguidonia");
			else if (userConverter.isUserInGroup(get().getLoggedInUser()
					.getGroups(), POMEZIA))
				title = new CustomLayout("logopomezia");
			else if (userConverter.isUserInGroup(get().getLoggedInUser()
					.getGroups(), LIVORNO))
				title = new CustomLayout("logolivorno");
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
