/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import it.vige.greenarea.dto.Address;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 
 * @author Administrator
 */
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

	public ShippingOrderData(String shipmentId,
			ShippingItemData[] shippingItems,
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
