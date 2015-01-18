/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.lib.mqData;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 00917377
 */
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

	public MqShippingData(String shId, String trId, DBGeoLocation sourceLocation,
			DBGeoLocation destinationLocation, DBGeoLocation pickup,
			DBGeoLocation dropdown, String serviceClass,
			List<MqFreightData> shippingItems, Map<String, String> attributes,
			List<MY_Attachment> attachments) {
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
			sb.append("\n- destinatario: ").append(
					destinationLocation.toString());
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
