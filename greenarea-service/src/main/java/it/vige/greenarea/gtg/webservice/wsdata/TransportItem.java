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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.Transport;

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
