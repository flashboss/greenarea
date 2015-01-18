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
package it.vige.greenarea.sgaplconsole.controllers;

import it.vige.greenarea.itseasy.lib.configurationData.SGAPLconstants;
import it.vige.greenarea.sgaplconsole.data.MyTransport;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class GATconsoleController implements Serializable, SGAPLconstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = -913369824436717402L;
	@ManagedProperty(value = "#{sgaplConsoleController}")
	SGAPLconsoleController sgapl_controller;

	// @ManagedProperty(value = "#{tabPanelController}")
	// TabPanelController tabPanelController;

	/**
	 * Creates a new instance of GATconsoleController
	 */
	public GATconsoleController() {
	}

	public String startTransport() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyTransport currentTrasport = context.getApplication()
				.evaluateExpressionGet(context, "#{transport}",
						MyTransport.class);
		FacesMessage msg = sgapl_controller.startTransport(
				currentTrasport.getId(), currentTrasport.getVettore());
		context.addMessage(null, msg);
		// tabPanelController.setTabViewCurrentIndex(tabPanelController.GAT_CONSOLE_TAB);
		return "home";
	}

	public String doneTransport() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyTransport currentTrasport = context.getApplication()
				.evaluateExpressionGet(context, "#{transport}",
						MyTransport.class);
		FacesMessage msg = sgapl_controller.doneTransport(
				currentTrasport.getId(), currentTrasport.getVettore());
		context.addMessage(null, msg);
		// tabPanelController.setTabViewCurrentIndex(tabPanelController.GAT_CONSOLE_TAB);
		return "home";
	}

	public String rejectTransport() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyTransport currentTrasport = context.getApplication()
				.evaluateExpressionGet(context, "#{transport}",
						MyTransport.class);
		FacesMessage msg = sgapl_controller.rejectTransport(
				currentTrasport.getId(), currentTrasport.getVettore());
		context.addMessage(null, msg);
		// tabPanelController.setTabViewCurrentIndex(tabPanelController.GAT_CONSOLE_TAB);
		return "home";
	}

	public boolean isTransportReady() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyTransport currentTrasport = context.getApplication()
				.evaluateExpressionGet(context, "#{transport}",
						MyTransport.class);
		return currentTrasport.getStatus().equals(READY_STATUS);
	}

	public boolean isTransportOnDelivery() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyTransport currentTrasport = context.getApplication()
				.evaluateExpressionGet(context, "#{transport}",
						MyTransport.class);
		return currentTrasport.getStatus().equals(ON_DELIVERY_STATUS);
	}

	public boolean isTransportDone() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyTransport currentTrasport = context.getApplication()
				.evaluateExpressionGet(context, "#{transport}",
						MyTransport.class);
		return currentTrasport.getStatus().equals(DONE_STATUS);
	}

	public boolean isTransportRejected() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyTransport currentTrasport = context.getApplication()
				.evaluateExpressionGet(context, "#{transport}",
						MyTransport.class);
		return currentTrasport.getStatus().equals(REJECT_STATUS);
	}

	public SGAPLconsoleController getSgapl_controller() {
		return sgapl_controller;
	}

	public void setSgapl_controller(SGAPLconsoleController sgapl_controller) {
		this.sgapl_controller = sgapl_controller;
	}

	/*
	 * public TabPanelController getTabPanelController() { return
	 * tabPanelController; }
	 * 
	 * public void setTabPanelController(TabPanelController tabPanelController)
	 * { this.tabPanelController = tabPanelController; }
	 */
}
