/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.webservice.wsdata;

import it.vige.greenarea.cl.library.entities.Vehicle;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author 00917377
 */
@XmlType(name = "Truck")
@XmlAccessorType(XmlAccessType.FIELD)
public class TruckInfo implements Serializable {

	private static final long serialVersionUID = -854365897081145072L;
	@XmlElement(required = true)
	private String plateNumber;
	private String model;
	private String serviceClass;

	public TruckInfo() {
	}

	public TruckInfo(Vehicle t) {
		this.plateNumber = t.getPlateNumber();
		this.model = t.getServiceClass().getModelV();
		this.serviceClass = t.getServiceClass().getDescription();
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

}
