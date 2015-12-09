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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderData.TerminiDiConsegna;
import it.vige.greenarea.sgaplconsole.controllers.utils.Converters;
import it.vige.greenarea.sgaplconsole.data.FreightItem;
import it.vige.greenarea.sgaplconsole.data.MyOrder;

@ManagedBean
@SessionScoped
public class NewOrderController implements Serializable {

	private static final long serialVersionUID = 7485919985576546278L;
	List<FreightItem> itemList;
	Set<Map.Entry<String, String>> termList;
	MyOrder newOrderForm;
	TerminiDiConsegna.Entry currentTerm;
	FreightItem currentFrItem;
	@ManagedProperty(value = "#{sgaplConsoleController}")
	SGAPLconsoleController sgapl_controller;
	@ManagedProperty(value = "#{tabPanelController}")
	TabPanelController tabPanelController;

	/**
	 * Creates a new instance of NewOrderConrtoller
	 */
	public NewOrderController() {
		newOrderForm = new MyOrder();
		currentTerm = new TerminiDiConsegna.Entry();
		currentFrItem = new FreightItem();
		itemList = new ArrayList<FreightItem>();

	}

	public TabPanelController getTabPanelController() {
		return tabPanelController;
	}

	public void setTabPanelController(TabPanelController tabPanelController) {
		this.tabPanelController = tabPanelController;
	}

	public void resetForm() {
		newOrderForm = new MyOrder();
		currentTerm = new TerminiDiConsegna.Entry();
		currentFrItem = new FreightItem();
		newOrderForm.getOrderData().getTerminiDiConsegna().getEntry().clear();
		newOrderForm.getOrderData().getShippingItems().clear();
	}

	public String addOrder() {
		newOrderForm.getOrderData().getShippingItems().addAll(Converters.convertFreightItemList(itemList));

		sgapl_controller.getOrderList().add(newOrderForm);
		resetForm();
		tabPanelController.setTabViewIndex(tabPanelController.ORDER_LIST_INDEX);
		return "home";
	}

	public String addItem() {

		currentFrItem = new FreightItem();
		return null;
	}

	public List<FreightItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<FreightItem> itemList) {
		this.itemList.clear();
		this.itemList.addAll(itemList);
	}

	public List<TerminiDiConsegna.Entry> getTermList() {
		return newOrderForm.getOrderData().getTerminiDiConsegna().getEntry();
	}

	public MyOrder getNewOrderForm() {
		return newOrderForm;
	}

	public void setNewOrderForm(MyOrder newOrderForm) {
		this.newOrderForm = newOrderForm;
	}

	public TerminiDiConsegna.Entry getCurrentTerm() {
		return currentTerm;
	}

	public void setCurrentTerm(TerminiDiConsegna.Entry currentTerm) {
		this.currentTerm = currentTerm;
	}

	public FreightItem getCurrentFrItem() {
		return currentFrItem;
	}

	public void setCurrentFrItem(FreightItem currentFrItem) {
		this.currentFrItem = currentFrItem;
	}

	public SGAPLconsoleController getSgapl_controller() {
		return sgapl_controller;
	}

	public void setSgapl_controller(SGAPLconsoleController sgapl_controller) {
		this.sgapl_controller = sgapl_controller;
	}
}
