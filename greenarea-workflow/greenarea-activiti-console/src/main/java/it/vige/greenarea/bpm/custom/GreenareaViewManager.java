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

import org.activiti.explorer.DefaultViewManager;

import it.vige.greenarea.bpm.custom.ui.dettaglio.DettaglioPage;
import it.vige.greenarea.bpm.custom.ui.home.DetailHomePage;

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
		switchView(new DettaglioPage(processInstanceId, null), MAIN_NAVIGATION_HOME, null);
	}

	@Override
	public void showDefaultPage() {
		mainWindow.showDefaultContent();
		showHomePage();
	}

}
