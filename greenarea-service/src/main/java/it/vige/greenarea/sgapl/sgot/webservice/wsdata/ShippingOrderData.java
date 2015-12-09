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
package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import java.io.Serializable;
import java.util.HashMap;

import it.vige.greenarea.dto.Address;

public class ShippingOrderData implements Serializable {

	private String shipmentId;
	private static final long serialVersionUID = 1006666370581747020L;
	private ShippingItemData[] shippingItems;
	private HashMap<String, String> terminiDiConsegna; // es catena del
														// freddo:si/no,
														// deperibile:si/no,
														// priorita'....
	private String note;
	private String fromName;
	private String toName;
	private Address fromAddress;
	private Address toAddress;
	private String operatoreLogistico;

	public ShippingOrderData() {
	}

	public ShippingOrderData(String shipmentId) {
		this.shipmentId = shipmentId;
	}

	public ShippingOrderData(String shipmentId, ShippingItemData[] shippingItems,
			HashMap<String, String> terminiDiConsegna, String note) {
		this(shipmentId);
		this.shippingItems = shippingItems;
		this.terminiDiConsegna = terminiDiConsegna;
		this.note = note;
	}

	public String getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(String shipmentId) {
		this.shipmentId = shipmentId;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public Address getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(Address fromAddress) {
		this.fromAddress = fromAddress;
	}

	public Address getToAddress() {
		return toAddress;
	}

	public void setToAddress(Address toAddress) {
		this.toAddress = toAddress;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public ShippingItemData[] getShippingItems() {
		return shippingItems;
	}

	public void setShippingItems(ShippingItemData[] shippingItems) {
		this.shippingItems = shippingItems;
	}

	public HashMap<String, String> getTerminiDiConsegna() {
		return terminiDiConsegna;
	}

	public void setTerminiDiConsegna(HashMap<String, String> terminiDiConsegna) {
		this.terminiDiConsegna = terminiDiConsegna;
	}

	public String getOperatoreLogistico() {
		return operatoreLogistico;
	}

	public void setOperatoreLogistico(String operatoreLogistico) {
		this.operatoreLogistico = operatoreLogistico;
	}

}
