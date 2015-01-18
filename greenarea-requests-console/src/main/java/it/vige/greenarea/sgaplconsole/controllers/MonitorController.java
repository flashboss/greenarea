/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgaplconsole.controllers;

import it.vige.greenarea.sgaplconsole.data.MonitorMsg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.inject.Named;

/**
 * 
 * @author 00917377
 */
@Singleton
@Named("monitorController")
public class MonitorController implements Serializable {

	private static final long serialVersionUID = -4854465558465574842L;

	private List<MonitorMsg> sbMonitor = new ArrayList<MonitorMsg>();

	public List<MonitorMsg> getSbMonitor() {
		return sbMonitor;
	}

	public void setSbMonitor(List<MonitorMsg> sbMonitor) {
		this.sbMonitor = sbMonitor;
	}

	public void clear() {
		sbMonitor = new ArrayList<MonitorMsg>();
	}

}