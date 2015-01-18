/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.webservice.wsdata;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.dto.GeoLocation;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author 00917377
 */
@XmlType(name = "GeoReference")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoReference implements Serializable {

	private static final long serialVersionUID = -5461983007561344307L;
	String name;
	@XmlElement(required = true)
	String surname;
	String mobile;
	String phone;
	String email;
	@XmlElement(required = true)
	GeoLocation geoLocation;

	public GeoReference() {
		this.name = "";
		this.surname = "";
		this.mobile = "";
		this.phone = "";
		this.email = "";
		this.geoLocation = new GeoLocation();
	}

	public GeoReference(DBGeoLocation sgotLoc) {
		this.name = sgotLoc.getName();
		this.surname = sgotLoc.getSurname();
		this.mobile = sgotLoc.getMobile();
		this.phone = sgotLoc.getPhone();
		this.email = sgotLoc.getEmail();
		this.geoLocation = new GeoLocation(sgotLoc);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public GeoLocation getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
	}

}
