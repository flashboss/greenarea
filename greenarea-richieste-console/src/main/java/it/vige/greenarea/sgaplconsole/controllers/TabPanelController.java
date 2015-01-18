/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgaplconsole.controllers;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author 00917377
 */
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
    
  //  @ManagedProperty(value = "#{newOrderConrtoller}")
  //  NewOrderConrtoller newOrderConrtoller;

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

/*    public NewOrderConrtoller getNewOrderConrtoller() {
        return newOrderConrtoller;
    }

    public void setNewOrderConrtoller(NewOrderConrtoller newOrderConrtoller) {
        this.newOrderConrtoller = newOrderConrtoller;
    }*/
}
