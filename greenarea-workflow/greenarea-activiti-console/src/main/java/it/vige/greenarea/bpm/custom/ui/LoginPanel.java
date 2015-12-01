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
package it.vige.greenarea.bpm.custom.ui;

import static com.vaadin.event.ShortcutAction.KeyCode.ENTER;
import static com.vaadin.ui.Alignment.MIDDLE_LEFT;
import static com.vaadin.ui.themes.Reindeer.LABEL_H1;
import static com.vaadin.ui.themes.Reindeer.LABEL_H2;
import static com.vaadin.ui.themes.Reindeer.LABEL_SMALL;
import static com.vaadin.ui.themes.Reindeer.PANEL_LIGHT;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.AREA_RISERVATA_TITLE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.POMEZIA_TITLE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.LOGIN;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.LIVORNO_TITLE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PASSWORD_TITLE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.SPERIMENTAZIONI_TITLE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.GUIDONIA_TITLE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.USER_NAME_TITLE;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_APPLICATION_MINI_POMEZIA;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_APPLICATION_MINI_LIVORNO;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_APPLICATION_MINI_GUIDONIA;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_APPLICATION_SPERIMENTAZIONI;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_APPLICATION_TITLE_POMEZIA;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_APPLICATION_TITLE_LIVORNO;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_APPLICATION_TITLE_GUIDONIA;
import static org.activiti.explorer.Environments.ALFRESCO;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.LOGIN_FAILED_HEADER;
import static org.activiti.explorer.Messages.LOGIN_FAILED_INVALID;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_WORKFLOW_CONSOLE_LOGO;

import org.activiti.explorer.I18nManager;
import org.activiti.explorer.NotificationManager;
import org.activiti.explorer.ViewManager;
import org.activiti.explorer.identity.LoggedInUser;
import org.activiti.explorer.ui.login.LoginHandler;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Component containing all events for a given task.
 * 
 * @author Joram Barrez
 */
public class LoginPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private I18nManager i18nManager;
	private ViewManager viewManager;
	private NotificationManager notificationManager;
	private LoginHandler loginHandler;
	private TextField userNameInputField;
	private PasswordField passwordInputField;
	private VerticalLayout loginPanel;
	private VerticalLayout sperimentazioni;

	public LoginPanel() {
		this.i18nManager = get().getI18nManager();
		this.viewManager = get().getViewManager();
		this.notificationManager = get().getNotificationManager();
		this.loginHandler = get().getLoginHandler();

		((VerticalLayout) getContent()).setSpacing(true);
		((VerticalLayout) getContent()).setMargin(true);
		setHeight(100, UNITS_PERCENTAGE);

		initLoginPanel();
		initSperimentazioni();
	}

	public void refresh() {
	}

	private void initLoginPanel() {
		loginPanel = new VerticalLayout();
		addTitle();
		addInputField();
		addComponent(loginPanel);
	}

	private void initSperimentazioni() {
		sperimentazioni = new VerticalLayout();
		sperimentazioni.setWidth("100%");
		sperimentazioni.addStyleName(STYLE_APPLICATION_SPERIMENTAZIONI);
		addTitle2();
		addRegions();
		addComponent(sperimentazioni);
	}

	private void addTitle() {
		Label areaRiservataTitle = new Label(
				i18nManager.getMessage(AREA_RISERVATA_TITLE));
		areaRiservataTitle.addStyleName(LABEL_H2);
		loginPanel.addComponent(areaRiservataTitle);
	}

	private void addTitle2() {
		Label sperimentazioniTitle = new Label(
				i18nManager.getMessage(SPERIMENTAZIONI_TITLE));
		sperimentazioniTitle.addStyleName(LABEL_H2);
		sperimentazioni.addComponent(sperimentazioniTitle);
	}

	private void addRegions() {
		initGuidonia();
		initLivorno();
		initPomezia();
	}

	private void initGuidonia() {
		HorizontalLayout panel = new HorizontalLayout();
		panel.setHeight(90, UNITS_PIXELS);
		Label title = new Label();
		title.addStyleName(LABEL_H1);
		title.setWidth(80, UNITS_PIXELS);

		if (get().getEnvironment().equals(ALFRESCO)) {
			title.addStyleName(STYLE_WORKFLOW_CONSOLE_LOGO);
		} else {
			title.addStyleName(STYLE_APPLICATION_MINI_GUIDONIA);
		}
		Label city = new Label();
		city.addStyleName(LABEL_H1);
		city.setValue(i18nManager.getMessage(GUIDONIA_TITLE));
		city.addStyleName(STYLE_APPLICATION_TITLE_GUIDONIA);
		city.setWidth(310, UNITS_PIXELS);

		panel.addComponent(title);
		panel.addComponent(city);

		sperimentazioni.addComponent(panel);
	}

	private void initLivorno() {
		HorizontalLayout panel = new HorizontalLayout();
		panel.setHeight(90, UNITS_PIXELS);
		Label title = new Label();
		title.addStyleName(LABEL_H1);
		title.setWidth(80, UNITS_PIXELS);

		if (get().getEnvironment().equals(ALFRESCO)) {
			title.addStyleName(STYLE_WORKFLOW_CONSOLE_LOGO);
		} else {
			title.addStyleName(STYLE_APPLICATION_MINI_LIVORNO);
		}
		Label city = new Label();
		city.addStyleName(LABEL_H1);
		city.setValue(i18nManager.getMessage(LIVORNO_TITLE));
		city.addStyleName(STYLE_APPLICATION_TITLE_LIVORNO);
		city.setWidth(310, UNITS_PIXELS);

		panel.addComponent(title);
		panel.addComponent(city);

		sperimentazioni.addComponent(panel);
	}

	private void initPomezia() {
		HorizontalLayout panel = new HorizontalLayout();
		panel.setHeight(90, UNITS_PIXELS);
		Label title = new Label();
		title.addStyleName(LABEL_H1);
		title.setWidth(80, UNITS_PIXELS);

		if (get().getEnvironment().equals(ALFRESCO)) {
			title.addStyleName(STYLE_WORKFLOW_CONSOLE_LOGO);
		} else {
			title.addStyleName(STYLE_APPLICATION_MINI_POMEZIA);
		}
		Label city = new Label();
		city.addStyleName(LABEL_H1);
		city.setValue(i18nManager.getMessage(POMEZIA_TITLE));
		city.addStyleName(STYLE_APPLICATION_TITLE_POMEZIA);
		city.setWidth(310, UNITS_PIXELS);

		panel.addComponent(title);
		panel.addComponent(city);

		sperimentazioni.addComponent(panel);
	}

	private void addInputField() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setWidth(100, UNITS_PERCENTAGE);
		loginPanel.addComponent(layout);

		Panel textFieldPanel = new Panel(); // Hack: actionHandlers can only be
											// attached to panels or windows
		textFieldPanel.addStyleName(PANEL_LIGHT);
		textFieldPanel.setContent(new VerticalLayout());
		textFieldPanel.setWidth(100, UNITS_PERCENTAGE);
		layout.addComponent(textFieldPanel);
		layout.setExpandRatio(textFieldPanel, 1.0f);

		Label labelUserName = new Label(i18nManager.getMessage(USER_NAME_TITLE));
		labelUserName.addStyleName(LABEL_SMALL);
		userNameInputField = new TextField();
		userNameInputField.setWidth(100, UNITS_PERCENTAGE);
		Label labelPassword = new Label(i18nManager.getMessage(PASSWORD_TITLE));
		labelPassword.addStyleName(LABEL_SMALL);
		passwordInputField = new PasswordField();
		passwordInputField.setWidth(100, UNITS_PERCENTAGE);
		textFieldPanel.addComponent(labelUserName);
		textFieldPanel.addComponent(userNameInputField);
		textFieldPanel.addComponent(labelPassword);
		textFieldPanel.addComponent(passwordInputField);

		// Hack to catch keyboard 'enter'
		textFieldPanel.addActionHandler(new Handler() {
			private static final long serialVersionUID = 6928598745792215505L;

			public void handleAction(Action action, Object sender, Object target) {
				login(userNameInputField.getValue().toString(),
						passwordInputField.getValue().toString());
			}

			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new ShortcutAction("enter", ENTER, null) };
			}
		});

		Button loginButton = new Button(i18nManager.getMessage(LOGIN));
		layout.addComponent(loginButton);
		layout.setComponentAlignment(loginButton, MIDDLE_LEFT);
		loginButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 7781253151592188006L;

			public void buttonClick(ClickEvent event) {
				login(userNameInputField.getValue().toString(),
						passwordInputField.getValue().toString());
			}
		});
	}

	private void login(String userName, String password) {
		LoggedInUser loggedInUser = loginHandler.authenticate(userName,
				password);
		if (loggedInUser != null) {
			get().setUser(loggedInUser);
			viewManager.showDefaultPage();
		} else {
			userNameInputField.setValue("");
			passwordInputField.setValue("");
			userNameInputField.focus();
			notificationManager.showErrorNotification(LOGIN_FAILED_HEADER,
					i18nManager.getMessage(LOGIN_FAILED_INVALID));
		}
		refresh();
	}

}
