/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgaplconsole.data;

import it.vige.greenarea.sgapl.sgot.webservice.TransportInfo;

import java.io.Serializable;

/**
 * 
 * @author 00917377
 */
public class MyTransport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4297880829470530461L;
	String id;
	String orderID;
	String status;
	String vettore;
	String sourceSite;
	String destinationSite;

	public MyTransport(TransportInfo trInfo) {
		this.id = trInfo.getId();
		this.orderID = trInfo.getOrderID();
		this.status = trInfo.getStatus();
		this.sourceSite = trInfo.getSourceSite();
		this.destinationSite = trInfo.getDestinationSite();
		this.vettore = trInfo.getVettore();
	}

	public MyTransport() {

	}

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
