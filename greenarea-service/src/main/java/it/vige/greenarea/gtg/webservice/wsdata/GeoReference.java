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
package it.vige.greenarea.gtg.webservice.wsdata;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.dto.GeoLocation;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

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
