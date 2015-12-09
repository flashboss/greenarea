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

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class TruckLoadDescriptor implements Serializable {

	private static final long serialVersionUID = 723411266847605033L;
	private Double volume;
	private Double weight;
	private Long attachmentID;

	public TruckLoadDescriptor() {
		this.volume = new Double(0);
		this.weight = new Double(0);
		this.attachmentID = new Long(0);
	}

	public TruckLoadDescriptor(TruckLoadDescriptor truckLoad) {
		this.volume = new Double(truckLoad.getVolume());
		this.weight = new Double(truckLoad.getWeight());
		this.attachmentID = new Long(truckLoad.getAttachmentID());
	}

	public Long getAttachmentID() {
		return attachmentID;
	}

	public void setAttachmentID(Long attachmentID) {
		this.attachmentID = attachmentID;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attachmentID == null) ? 0 : attachmentID.hashCode());
		result = prime * result + ((volume == null) ? 0 : volume.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TruckLoadDescriptor other = (TruckLoadDescriptor) obj;
		if (attachmentID == null) {
			if (other.attachmentID != null)
				return false;
		} else if (!attachmentID.equals(other.attachmentID))
			return false;
		if (volume == null) {
			if (other.volume != null)
				return false;
		} else if (!volume.equals(other.volume))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TruckLoadDescriptor [volume=" + volume + ", weight=" + weight + ", attachmentID=" + attachmentID + "]";
	}

}
