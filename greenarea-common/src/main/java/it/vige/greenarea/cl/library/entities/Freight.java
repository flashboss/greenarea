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
package it.vige.greenarea.cl.library.entities;

import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Freight implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private String codeId;
	private String description;
	private double volume;
	private double height;
	private double leng;
	private double width;
	private double weight;
	private boolean stackable;
	private boolean keepUpStanding;
	private FreightType ft;
	private FreightItemState freightState;
	@ManyToOne
	private ExchangeStop pickUpPoint;
	@ManyToOne
	private ExchangeStop dropDownPoint;
	@ElementCollection(fetch = EAGER)
	private List<Attachment> attachments;
	@ManyToOne
	private Transport transport;

	public Freight() {
		attachments = new ArrayList<Attachment>();
		freightState = FreightItemState.AVAILABLE;
	}

	public Freight(String codeId) {
		this();
		this.codeId = codeId;
	}

	public String getCodeId() {
		return codeId;
	}

	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	/*
	 * public char[] getCodeId() { return codeId; }
	 * 
	 * public void setCodeId(char[] codeId) { this.codeId = codeId; }
	 */
	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public boolean isKeepUpStanding() {
		return keepUpStanding;
	}

	public void setKeepUpStanding(boolean keepUpStanding) {
		this.keepUpStanding = keepUpStanding;
	}

	public double getLeng() {
		return leng;
	}

	public void setLeng(double leng) {
		this.leng = leng;
	}

	public boolean isStackable() {
		return stackable;
	}

	public void setStackable(boolean stackable) {
		this.stackable = stackable;
	}

	public FreightType getFt() {
		return ft;
	}

	public void setFt(FreightType ft) {
		this.ft = ft;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public FreightItemState getFreightState() {
		return freightState;
	}

	public void setFreightState(FreightItemState freightState) {
		this.freightState = freightState;
	}

	public Transport getTransport() {
		return transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

	public ExchangeStop getDropDownPoint() {
		return dropDownPoint;
	}

	public void setDropDownPoint(ExchangeStop dropDownPoint) {
		this.dropDownPoint = dropDownPoint;
	}

	public ExchangeStop getPickUpPoint() {
		return pickUpPoint;
	}

	public void setPickUpPoint(ExchangeStop pickUpPoint) {
		this.pickUpPoint = pickUpPoint;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (codeId != null ? codeId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Freight)) {
			return false;
		}
		Freight other = (Freight) object;
		if ((this.codeId == null && other.codeId != null)
				|| (this.codeId != null && !this.codeId.equals(other.codeId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[codeId = ").append(this.codeId);
		sb.append("] ").append(this.description);
		sb.append(" - status:").append(this.freightState);
		sb.append(" - pickup:").append(this.pickUpPoint);
		sb.append(" - dropdown:").append(this.dropDownPoint);
		return sb.toString();
	}
}
