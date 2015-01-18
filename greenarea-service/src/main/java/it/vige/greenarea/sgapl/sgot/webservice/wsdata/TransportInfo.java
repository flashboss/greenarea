/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 */
public class TransportInfo implements Serializable {

	private static final long serialVersionUID = 7120048728439537362L;
	String id;
	String orderID;
	String status;
	String vettore;
	String sourceSite;
	String destinationSite;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVettore() {
		return vettore;
	}

	public void setVettore(String vettore) {
		this.vettore = vettore;
	}

	public String getSourceSite() {
		return sourceSite;
	}

	public void setSourceSite(String sourceSite) {
		this.sourceSite = sourceSite;
	}

	public String getDestinationSite() {
		return destinationSite;
	}

	public void setDestinationSite(String destinationSite) {
		this.destinationSite = destinationSite;
	}

}
