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

import static org.activiti.explorer.Environments.ALFRESCO;

import org.activiti.explorer.DefaultViewManager;
import org.activiti.explorer.ViewManager;
import org.activiti.explorer.ViewManagerFactoryBean;
import org.activiti.explorer.ui.alfresco.AlfrescoViewManager;

public class GreenareaViewManagerFactoryBean extends ViewManagerFactoryBean {

	private static final long serialVersionUID = -620536937856444318L;

	@Override
	public ViewManager getObject() throws Exception {
		DefaultViewManager viewManagerImpl;
		if (environment.equals(ALFRESCO)) {
			viewManagerImpl = new AlfrescoViewManager();
		} else {
			viewManagerImpl = new GreenareaViewManager();
		}
		viewManagerImpl.setMainWindow(mainWindow);
		return viewManagerImpl;
	}

}
