/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.webservice.wsdata;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.Transport;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author 00917377
 */
@XmlType(name = "Transport")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransportItem implements Serializable {
	
	private static final long serialVersionUID = -6057730583040319612L;
	@XmlElement(required = true)
	private String code;
	private String serviceClass;
	@XmlElement(required = true)
	private GeoReference sender;
	@XmlElement(required = true)
	private GeoReference receiver;

	public TransportItem() {
		this.code = "";
		this.serviceClass = "";
		this.sender = new GeoReference();
		this.receiver = new GeoReference();
	}

	public TransportItem(Transport t) {
		this.code = t.getAlfacode();
		this.serviceClass = t.getServiceClass().getDescription();
		this.sender = new GeoReference(new DBGeoLocation(t.getSource()));
		this.receiver = new GeoReference(new DBGeoLocation(t.getDestination()));
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public GeoReference getSender() {
		return sender;
	}

	public void setSender(GeoReference sender) {
		this.sender = sender;
	}

	public GeoReference getReceiver() {
		return receiver;
	}

	public void setReceiver(GeoReference receiver) {
		this.receiver = receiver;
	}

}
