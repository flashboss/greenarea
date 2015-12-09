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
	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
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
