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

import it.vige.greenarea.cl.library.entities.Freight;

@XmlType(name = "Freight")
@XmlAccessorType(XmlAccessType.FIELD)
public class FreightItem implements Serializable {

	private static final long serialVersionUID = -7078478726388196603L;
	@XmlElement(required = true)
	private String code;
	private String description;
	private Integer volume;
	private Integer height;
	private Integer lenght;
	private Integer width;
	private Integer weight;
	private Boolean stackable;
	private Boolean keepUpStanding;
	private String type;
	@XmlElement(required = true)
	private String transportCode;
	@XmlElement(name = "state")
	private Integer freightState;

	public FreightItem() {
	}

	public FreightItem(Freight f) {
		this.code = f.getCodeId();
		this.description = f.getDescription();
		this.freightState = f.getFreightState().ordinal();
		this.transportCode = f.getTransport().getAlfacode();
		this.volume = (int) f.getVolume();
		this.height = (int) f.getHeight();
		this.lenght = (int) f.getLeng();
		this.width = (int) f.getWidth();
		this.weight = (int) f.getWeight();
		this.stackable = f.isStackable();
		this.keepUpStanding = f.isKeepUpStanding();
		this.type = f.getFt().name();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getLenght() {
		return lenght;
	}

	public void setLenght(Integer lenght) {
		this.lenght = lenght;
	}

	public Integer getFreightState() {
		return freightState;
	}

	public void setFreightState(Integer freightState) {
		this.freightState = freightState;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Boolean getStackable() {
		return stackable;
	}

	public void setStackable(Boolean stackable) {
		this.stackable = stackable;
	}

	public Boolean getKeepUpStanding() {
		return keepUpStanding;
	}

	public void setKeepUpStanding(Boolean keepUpStanding) {
		this.keepUpStanding = keepUpStanding;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTransportCode() {
		return transportCode;
	}

	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (code != null ? code.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Freight)) {
			return false;
		}
		FreightItem other = (FreightItem) object;
		if ((this.code == null && other.code != null) || (this.code != null && !this.code.equals(other.code))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[codeId = ").append(this.code);
		sb.append("] ").append(this.description);
		sb.append(" - status:").append(this.freightState);
		sb.append(" - transport:").append(this.transportCode);

		return sb.toString();
	}
}
