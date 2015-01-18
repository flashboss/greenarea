/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgaplconsole.controllers;

import it.vige.greenarea.itseasy.lib.configurationData.SGAPLconstants;
import it.vige.greenarea.sgaplconsole.data.MyTransport;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * 
 * @author 00917377
 */
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
