/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Administrator
 */
public class ShippingOrderDetails implements Serializable {

	private static final long serialVersionUID = -6332558769560666432L;
	private String id;
	private String creationTimestamp;
	private String ordinante; // nome di client account
	private List<ShippingItemData> itemsList = new ArrayList<ShippingItemData>();
	private DBGeoLocation mittente;
	private DBGeoLocation destinatario;
	private String operatoreLogistico;

	private HashMap<String, String> terminiDiConsegna = new HashMap<String, String>(); // es
																						// catena
																						// del
																						// freddo:si/no,
																						// deperibile:si/no,
																						// priorita'....
	private String note;
	private String stato;
	private String transportID;
	private String cost; // contiene il costo del trasporto come restituito da
							// GAT
	private String trackingURL;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(String creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public String getOrdinante() {
		return ordinante;
	}

	public void setOrdinante(String ordinante) {
		this.ordinante = ordinante;
	}

	public List<ShippingItemData> getItemsList() {
		return itemsList;
	}

	public void setItemsList(List<ShippingItemData> itemsList) {
		this.itemsList = itemsList;
	}

	public DBGeoLocation getMittente() {
		return mittente;
	}

	public void setMittente(DBGeoLocation mittente) {
		this.mittente = mittente;
	}

	public DBGeoLocation getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(DBGeoLocation destinatario) {
		this.destinatario = destinatario;
	}

	public HashMap<String, String> getTerminiDiConsegna() {
		return terminiDiConsegna;
	}

	public void setTerminiDiConsegna(HashMap<String, String> terminiDiConsegna) {
		this.terminiDiConsegna = terminiDiConsegna;
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

	public String getTransportID() {
		return transportID;
	}

	public void setTransportID(String transportID) {
		this.transportID = transportID;
	}
	
	public String getOperatoreLogistico() {
		return operatoreLogistico;
	}

	public void setOperatoreLogistico(String operatoreLogistico) {
		this.operatoreLogistico = operatoreLogistico;
	}

}
