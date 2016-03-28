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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class TabPanelController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3938976759683442063L;
	final int ORDER_LIST_INDEX = 0;
	final int GAT_CONSOLE_INDEX = 1;
	final int NEW_ORDER_INDEX = 2;

	int tabViewIndex = ORDER_LIST_INDEX;

	// @ManagedProperty(value = "#{newOrderConrtoller}")
	// NewOrderConrtoller newOrderConrtoller;

	/**
	 * Creates a new instance of HomeBeanController
	 */
	public TabPanelController() {
	}

	public int getTabViewIndex() {
		return tabViewIndex;
	}

	public void setTabViewIndex(int tabViewIndex) {
		this.tabViewIndex = tabViewIndex;
	}

	/*
	 * public NewOrderConrtoller getNewOrderConrtoller() { return
	 * newOrderConrtoller; }
	 * 
	 * public void setNewOrderConrtoller(NewOrderConrtoller newOrderConrtoller)
	 * { this.newOrderConrtoller = newOrderConrtoller; }
	 */
}
