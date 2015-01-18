package it.vige.greenarea.bpm.custom;

import static it.vige.greenarea.Constants.ANONYMOUS;
import static org.activiti.engine.impl.identity.Authentication.setAuthenticatedUserId;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.identity.LoggedInUser;

public class GreenareaExplorerApp extends ExplorerApp {

	private static final long serialVersionUID = -2184179337810106886L;

	@Override
	public void onRequestStart(HttpServletRequest request,
			HttpServletResponse response) {
		// Set current application object as thread-local to make it easy
		// accessible
		current.set(this);

		// Authentication: check if user is found, otherwise send to login page
		LoggedInUser user = (LoggedInUser) getUser();
		if (user == null) {
			// First, try automatic login
			user = loginHandler.authenticate(request, response);
			if (user == null) {
				if (mainWindow != null && !mainWindow.isShowingLoginPage()) {
					user = loginHandler.authenticate(ANONYMOUS, ANONYMOUS);
					setUser(user);
					viewManager.showDefaultPage();
				}
			} else {
				setUser(user);
			}
		}

		if (user != null) {
			setAuthenticatedUserId(user.getId());
			if (mainWindow != null && mainWindow.isShowingLoginPage()) {
				viewManager.showDefaultPage();
			}
		}

		// Callback to the login handler
		loginHandler.onRequestStart(request, response);
	}

}
