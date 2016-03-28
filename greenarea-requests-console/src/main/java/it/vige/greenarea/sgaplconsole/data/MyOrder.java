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
package it.vige.greenarea.sgaplconsole.data;

import static it.vige.greenarea.sgaplconsole.controllers.utils.Converters.convertLocation;
import static it.vige.greenarea.sgaplconsole.controllers.utils.Converters.convertOrderData;
import static it.vige.greenarea.sgaplconsole.controllers.utils.Converters.convertShippingItem;
import static it.vige.greenarea.sgaplconsole.controllers.utils.Converters.convertShippingItemList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingItemData;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderData;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderDetails;

public class MyOrder implements Serializable {

	private static final long serialVersionUID = -5293928839264325950L;
	private String id;
	private DBGeoLocation from;
	private DBGeoLocation to;
	private String clientAccountName;
	private List<FreightItem> shippingItems;
	private String creationTimestamp;
	private String note;
	private String stato;
	private String transportID;
	private String cost; // contiene il costo del trasporto come restituito da
							// GAT
	private String trackingURL;
	private ShippingOrderData orderData;

	public MyOrder() {

		id = "";
		from = new DBGeoLocation();
		to = new DBGeoLocation();
		clientAccountName = "Multitaly";
		creationTimestamp = "";
		note = "";
		stato = "NEW";
		transportID = "";
		;
		cost = "0"; // contiene il costo del trasporto come restituito da GAT
		trackingURL = "...";
		shippingItems = new ArrayList<FreightItem>();

		orderData = new ShippingOrderData();
		orderData.setTerminiDiConsegna(new ShippingOrderData.TerminiDiConsegna());
	}

	public MyOrder(ShippingOrderDetails sd) {
		this.clientAccountName = sd.getOrdinante();
		this.id = sd.getId();
		this.from = convertLocation(sd.getMittente());
		this.to = convertLocation(sd.getDestinatario());
		this.cost = sd.getCost();
		this.creationTimestamp = sd.getCreationTimestamp();
		this.note = sd.getNote();
		this.stato = sd.getStato();
		this.trackingURL = sd.getTrackingURL();
		this.transportID = sd.getTransportID();
		this.orderData = convertOrderData(sd);
		this.shippingItems = convertShippingItemList(sd.getItemsList());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DBGeoLocation getFrom() {
		return from;
	}

	public void setFrom(DBGeoLocation from) {
		this.from = from;
	}

	public DBGeoLocation getTo() {
		return to;
	}

	public void setTo(DBGeoLocation to) {
		this.to = to;
	}

	public String getClientAccountName() {
		return clientAccountName;
	}

	public void setClientAccountName(String clientAccountName) {
		this.clientAccountName = clientAccountName;
	}

	public String getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(String creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getTransportID() {
		return transportID;
	}

	public void setTransportID(String transportID) {
		this.transportID = transportID;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getTrackingURL() {
		return trackingURL;
	}

	public void setTrackingURL(String trackingURL) {
		this.trackingURL = trackingURL;
	}

	public ShippingOrderData getOrderData() {
		return orderData;
	}

	public void setOrderData(ShippingOrderData orderData) {
		this.orderData = orderData;
	}

	public void refreshData(ShippingOrderDetails showOrderDetails) {
		this.creationTimestamp = showOrderDetails.getCreationTimestamp();
		this.note = showOrderDetails.getNote();
		this.stato = showOrderDetails.getStato();
		this.transportID = showOrderDetails.getTransportID();
		this.trackingURL = showOrderDetails.getTrackingURL();
		if (!showOrderDetails.getItemsList().isEmpty()) {
			List<ShippingItemData> shList = showOrderDetails.getItemsList();
			for (ShippingItemData sid : shList) {
				this.shippingItems.add(convertShippingItem(sid));
			}
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Order [");
		sb.append(this.id).append("-").append(clientAccountName).append("] ");
		sb.append("FROM: ").append(from.toString());
		// sb.append(" - ").append(fromAddress.toString());
		sb.append(" TO: ").append(to.toString());
		// sb.append(" - ").append(toAddress.toString());
		sb.append(" (").append(orderData.getNote()).append(") ");
		sb.append("ITEMS: ");
		for (Iterator<ShippingItemData> it = orderData.getShippingItems().iterator(); it.hasNext();) {
			ShippingItemData si = it.next();
			sb.append(si.toString()).append(" | ");
		}
		if (!orderData.getTerminiDiConsegna().getEntry().isEmpty()) {
			sb.append("TERMINI DI CONSEGNA: ");
			for (ShippingOrderData.TerminiDiConsegna.Entry t : orderData.getTerminiDiConsegna().getEntry()) {
				sb.append(t.toString()).append(" | ");
			}
		}
		return sb.toString();
	}

}
