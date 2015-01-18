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

import static it.vige.greenarea.Constants.ANONYMOUS;
import static it.vige.greenarea.Constants.OPERATORE_LOGISTICO;
import static it.vige.greenarea.Constants.SOCIETA_DI_TRASPORTO;
import static it.vige.greenarea.Constants.TRASPORTATORE_AUTONOMO;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_HOME;
import static it.vige.greenarea.bpm.custom.GreenareaViewManager.MAIN_NAVIGATION_HOME;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_MENU;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_OPERATORE_LOGISTICO_HEADER_PROFILE_BOX;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_PA_HEADER_PROFILE_BOX;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_SOCIETA_DI_TRASPORTO_HEADER_PROFILE_BOX;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.HEADER_LOGOUT;
import static org.activiti.explorer.Messages.PASSWORD_CHANGE;
import static org.activiti.explorer.Messages.PROFILE_EDIT;
import static org.activiti.explorer.Messages.PROFILE_SHOW;
import static org.activiti.explorer.ViewManager.MAIN_NAVIGATION_MANAGE;
import static org.activiti.explorer.ViewManager.MAIN_NAVIGATION_PROCESS;
import static org.activiti.explorer.ViewManager.MAIN_NAVIGATION_REPORT;
import static org.activiti.explorer.ViewManager.MAIN_NAVIGATION_TASK;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_HEADER_PROFILE_MENU;
import it.vige.greenarea.bpm.UserConverter;
import it.vige.greenarea.bpm.custom.GreenareaMessages;
import it.vige.greenarea.bpm.custom.GreenareaViewManager;

import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.explorer.identity.LoggedInUser;
import org.activiti.explorer.ui.mainlayout.MainMenuBar;
import org.activiti.explorer.ui.profile.ChangePasswordPopupWindow;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class GreenareaMainMenuBar extends MainMenuBar {

	private static final long serialVersionUID = -952431384449544684L;

	@Override
	protected void init() {
		addStyleName(STYLE_MENU);
		initButtons();
		initProfileButton();
	}

	@Override
	protected void initButtons() {
		// TODO: fixed widths based on i18n strings?
		Button homeButton = addMenuButton(MAIN_NAVIGATION_HOME,
				i18nManager.getMessage(MAIN_MENU_HOME), null, false, 80);
		homeButton.addListener(new ShowHomeClickListener());
		menuItemButtons.put(MAIN_NAVIGATION_HOME, homeButton);

		if (get().getLoggedInUser().isAdmin()) {

			Button taskButton = addMenuButton(MAIN_NAVIGATION_TASK,
					i18nManager.getMessage(GreenareaMessages.MAIN_MENU_TASKS),
					null, false, 80);
			taskButton.addListener(new ShowTasksClickListener());
			menuItemButtons.put(MAIN_NAVIGATION_TASK, taskButton);

			Button processButton = addMenuButton(MAIN_NAVIGATION_PROCESS,
					i18nManager.getMessage(GreenareaMessages.MAIN_MENU_PROCESS),
					null, false, 80);
			processButton
					.addListener(new ShowProcessDefinitionsClickListener());
			menuItemButtons.put(MAIN_NAVIGATION_PROCESS, processButton);

			Button reportingButton = addMenuButton(MAIN_NAVIGATION_REPORT,
					i18nManager.getMessage(GreenareaMessages.MAIN_MENU_REPORTS),
					null, false, 80);
			reportingButton.addListener(new ShowReportsClickListener());
			menuItemButtons.put(MAIN_NAVIGATION_REPORT, reportingButton);

			Button manageButton = addMenuButton(MAIN_NAVIGATION_MANAGE,
					i18nManager
							.getMessage(GreenareaMessages.MAIN_MENU_MANAGEMENT),
					null, false, 90);
			manageButton.addListener(new ShowManagementClickListener());
			menuItemButtons.put(MAIN_NAVIGATION_MANAGE, manageButton);
		}
	}

	// Listener classes
	private class ShowTasksClickListener implements ClickListener {
		private static final long serialVersionUID = -4255088050406693398L;

		public void buttonClick(ClickEvent event) {
			get().getViewManager().showInboxPage();
		}
	}

	private class ShowProcessDefinitionsClickListener implements ClickListener {
		private static final long serialVersionUID = 167694448432819383L;

		public void buttonClick(ClickEvent event) {
			get().getViewManager().showDeployedProcessDefinitionPage();
		}
	}

	private class ShowReportsClickListener implements ClickListener {
		private static final long serialVersionUID = 3851891385470927149L;

		public void buttonClick(ClickEvent event) {
			get().getViewManager().showRunReportPage();
		}
	}

	private class ShowManagementClickListener implements ClickListener {
		private static final long serialVersionUID = -7026043702896003524L;

		public void buttonClick(ClickEvent event) {
			get().getViewManager().showDatabasePage();
		}
	}

	private class ShowHomeClickListener implements ClickListener {
		private static final long serialVersionUID = -4255088050406698398L;

		public void buttonClick(ClickEvent event) {
			((GreenareaViewManager) get().getViewManager()).showHomePage();
		}
	}

	protected void initProfileButton() {

		if (useProfile()) {
			final LoggedInUser user = get().getLoggedInUser();

			// User name + link to profile
			MenuBar profileMenu = new MenuBar();
			UserConverter userConverter = new UserConverter();
			List<Group> groups = get().getLoggedInUser().getGroups();
			if (userConverter.isUserInGroup(groups, OPERATORE_LOGISTICO))
				profileMenu
						.addStyleName(STYLE_OPERATORE_LOGISTICO_HEADER_PROFILE_BOX);
			else if (userConverter.isUserInGroup(groups, SOCIETA_DI_TRASPORTO))
				profileMenu
						.addStyleName(STYLE_SOCIETA_DI_TRASPORTO_HEADER_PROFILE_BOX);
			else if (userConverter
					.isUserInGroup(groups, TRASPORTATORE_AUTONOMO))
				profileMenu
						.addStyleName(STYLE_SOCIETA_DI_TRASPORTO_HEADER_PROFILE_BOX);
			else
				profileMenu.addStyleName(STYLE_PA_HEADER_PROFILE_BOX);
			profileMenu.setHeight(14, UNITS_PIXELS);
			MenuItem rootItem = profileMenu.addItem(user.getFirstName() + " "
					+ user.getLastName(), null);
			rootItem.setStyleName(STYLE_HEADER_PROFILE_MENU);

			// Show profile
			rootItem.addItem(i18nManager.getMessage(PROFILE_SHOW),
					new Command() {
						private static final long serialVersionUID = 8748698561304992624L;

						public void menuSelected(MenuItem selectedItem) {
							get().getViewManager().showProfilePopup(
									user.getId());
						}
					});

			// Edit profile
			rootItem.addItem(i18nManager.getMessage(PROFILE_EDIT),
					new Command() {
						private static final long serialVersionUID = -5815196339877745007L;

						public void menuSelected(MenuItem selectedItem) {
							// TODO: Show in edit-mode
							get().getViewManager().showProfilePopup(
									user.getId());
						}
					});

			// Change password
			rootItem.addItem(i18nManager.getMessage(PASSWORD_CHANGE),
					new Command() {
						private static final long serialVersionUID = -1060329084309607294L;

						public void menuSelected(MenuItem selectedItem) {
							get().getViewManager().showPopupWindow(
									new ChangePasswordPopupWindow());
						}
					});

			rootItem.addSeparator();

			// Logout
			rootItem.addItem(i18nManager.getMessage(HEADER_LOGOUT),
					new Command() {
						private static final long serialVersionUID = 1221427524106192724L;

						public void menuSelected(MenuItem selectedItem) {
							get().close();
						}
					});

			addComponent(profileMenu);
			// header.setComponentAlignment(profileMenu, TOP_RIGHT);
		}
	}

	protected boolean useProfile() {
		boolean result = true;
		if (get().getLoggedInUser().getId().equals(ANONYMOUS))
			result = false;
		return result;
	}
}
