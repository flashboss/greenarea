/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgaplconsole.controllers;

import static it.vige.greenarea.Utilities.createMockShippingId;
import it.vige.greenarea.sgaplconsole.data.MyOrder;

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
@ManagedBean(name = "sgoConsoleController")
@SessionScoped
public class SGOconsoleController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1466610203807032567L;
	@ManagedProperty(value = "#{sgaplConsoleController}")
	SGAPLconsoleController sgapl_controller;
	@ManagedProperty(value = "#{tabPanelController}")
	TabPanelController tabPanelController;

	/**
	 * Creates a new instance of SGOconsoleController
	 */
	public SGOconsoleController() {

	}

	public String loadOrders() {
		sgapl_controller.loadOrders();

		return "home";
	}

	public String removeOrder() {
		FacesContext context = FacesContext.getCurrentInstance();
		int currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{orderIndex}", int.class);
		sgapl_controller.getOrderList().remove(currentOrder);

		return "home";
	}

	public String estimateOrder() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyOrder currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{order}", MyOrder.class);
		FacesMessage msg = sgapl_controller.estimateOrder(currentOrder);
		context.addMessage(null, msg);

		return "home";
	}

	public String requestOrder() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyOrder currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{order}", MyOrder.class);
		currentOrder.setId(createMockShippingId());
		FacesMessage msg = sgapl_controller.requestOrder(currentOrder);
		context.addMessage(null, msg);
		tabPanelController.setTabViewIndex(tabPanelController.ORDER_LIST_INDEX);
		return "home";
	}

	public String confirmOrder() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyOrder currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{order}", MyOrder.class);

		FacesMessage msg = sgapl_controller.confirmOrder(currentOrder);
		context.addMessage(null, msg);
		return "home";
	}

	public String dropOrder() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyOrder currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{order}", MyOrder.class);

		FacesMessage msg = sgapl_controller.dropOrder(currentOrder);
		context.addMessage(null, msg);
		return "home";
	}

	public String locateOrder() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyOrder currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{order}", MyOrder.class);

		FacesMessage msg = sgapl_controller.locateOrder(currentOrder);
		context.addMessage(null, msg);
		return "home";
	}

	public boolean isSuspended() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyOrder currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{order}", MyOrder.class);
		if (currentOrder.getId() == null
				|| currentOrder.getId().trim().equals("")) {
			return false;
		}
		String status = currentOrder.getStato();// orderStatus();
		if (status == null) {
			return false;
		}
		return status.equals("suspended");
	}

	public boolean isReady() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyOrder currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{order}", MyOrder.class);
		if (currentOrder.getId() == null
				|| currentOrder.getId().trim().equals("")) {
			return false;
		}
		String status = currentOrder.getStato();// orderStatus();
		if (status == null) {
			return false;
		}
		return status.equals("ready");
	}

	public boolean isUnknown() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyOrder currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{order}", MyOrder.class);
		if (currentOrder.getId() == null
				|| currentOrder.getId().trim().equals("")) {
			return true;
		}
		String status = currentOrder.getStato();// orderStatus();
		if (status == null) {
			return false;
		}
		return status.equals("unknown");
	}

	public boolean isOnDelivery() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyOrder currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{order}", MyOrder.class);
		if (currentOrder.getId() == null
				|| currentOrder.getId().trim().equals("")) {
			return false;
		}
		String status = currentOrder.getStato();// orderStatus();
		if (status == null) {
			return false;
		}
		return status.equals("ongoing");
	}

	public boolean isCompleted() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyOrder currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{order}", MyOrder.class);
		if (currentOrder.getId() == null
				|| currentOrder.getId().trim().equals("")) {
			return false;
		}
		String status = currentOrder.getStato();// orderStatus();
		if (status == null) {
			return false;
		}
		return status.equals("completed");
	}

	public boolean isReturning() {
		FacesContext context = FacesContext.getCurrentInstance();
		MyOrder currentOrder = context.getApplication().evaluateExpressionGet(
				context, "#{order}", MyOrder.class);
		if (currentOrder.getId() == null
				|| currentOrder.getId().trim().equals("")) {
			return false;
		}
		String status = currentOrder.getStato();// orderStatus();
		if (status == null) {
			return false;
		}
		return status.equals("returning");
	}

	public SGAPLconsoleController getSgapl_controller() {
		return sgapl_controller;
	}

	public void setSgapl_controller(SGAPLconsoleController sgapl_controller) {
		this.sgapl_controller = sgapl_controller;
	}

	public TabPanelController getTabPanelController() {
		return tabPanelController;
	}

	public void setTabPanelController(TabPanelController tabPanelController) {
		this.tabPanelController = tabPanelController;
	}
}
