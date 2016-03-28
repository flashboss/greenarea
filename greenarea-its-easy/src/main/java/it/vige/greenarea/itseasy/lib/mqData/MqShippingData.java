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
package it.vige.greenarea.itseasy.lib.mqData;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;

public class MqShippingData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8266402398613710063L;
	private String shId;
	private String trId;
	private DBGeoLocation sourceLocation;
	private DBGeoLocation destinationLocation;
	private DBGeoLocation pickup;
	private DBGeoLocation dropdown;
	private String serviceClass;
	private List<MqFreightData> shippingItems;
	private Map<String, String> attributes;
	private List<MY_Attachment> attachments;

	public MqShippingData(String shId, String trId, DBGeoLocation sourceLocation, DBGeoLocation destinationLocation,
			DBGeoLocation pickup, DBGeoLocation dropdown, String serviceClass, List<MqFreightData> shippingItems,
			Map<String, String> attributes, List<MY_Attachment> attachments) {
		this.shId = shId;
		this.trId = trId;
		this.sourceLocation = sourceLocation;
		this.destinationLocation = destinationLocation;
		this.pickup = pickup;
		this.dropdown = dropdown;
		this.serviceClass = serviceClass;
		this.shippingItems = shippingItems;
		this.attributes = attributes;
		this.attachments = attachments;
	}

	public String getShId() {
		return shId;
	}

	public void setShId(String shId) {
		this.shId = shId;
	}

	public String getTrId() {
		return trId;
	}

	public void setTrId(String trId) {
		this.trId = trId;
	}

	public DBGeoLocation getSourceLocation() {
		return sourceLocation;
	}

	public void setSourceLocation(DBGeoLocation sourceLocation) {
		this.sourceLocation = sourceLocation;
	}

	public DBGeoLocation getDestinationLocation() {
		return destinationLocation;
	}

	public void setDestinationLocation(DBGeoLocation destinationLocation) {
		this.destinationLocation = destinationLocation;
	}

	public DBGeoLocation getPickup() {
		return pickup;
	}

	public void setPickup(DBGeoLocation pickup) {
		this.pickup = pickup;
	}

	public DBGeoLocation getDropdown() {
		return dropdown;
	}

	public void setDropdown(DBGeoLocation dropdown) {
		this.dropdown = dropdown;
	}

	public List<MqFreightData> getShippingItems() {
		return shippingItems;
	}

	public void setShippingItems(List<MqFreightData> shippingItems) {
		this.shippingItems = shippingItems;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public List<MY_Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<MY_Attachment> attachments) {
		this.attachments = attachments;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("ShippingOrderData :");
		if (this.shId != null) {
			sb.append("\n- shId: ").append(shId);
		}
		if (this.trId != null) {
			sb.append("\n- trId: ").append(trId);
		}
		if (this.sourceLocation != null) {
			sb.append("\n- mittente: ").append(sourceLocation.toString());
		}
		if (this.destinationLocation != null) {
			sb.append("\n- destinatario: ").append(destinationLocation.toString());
		}
		if (this.pickup != null) {
			sb.append("\n- pickUp: ").append(pickup.toString());
		}
		if (this.dropdown != null) {
			sb.append("\n- dropDown: ").append(dropdown.toString());
		}
		if (this.serviceClass != null) {
			sb.append("\n- serviceClass: ").append(serviceClass.toString());
		}

		if ((shippingItems != null) && (!shippingItems.isEmpty())) {
			sb.append("\n******* Items: ");
			for (MqFreightData sid : shippingItems) {
				sb.append("\n\t\t- ").append(sid.toString());
			}
		}
		if ((attributes != null) && (!attributes.isEmpty())) {
			sb.append("\nATTRIBUTES: ").append(attributes.toString());
		}
		if ((attachments != null) && (!attachments.isEmpty())) {
			sb.append("\nATTACHMENTS: ");
			for (MY_Attachment att : attachments) {
				sb.append(" ").append(att.getName());
			}
		}
		return sb.toString();
	}
}
