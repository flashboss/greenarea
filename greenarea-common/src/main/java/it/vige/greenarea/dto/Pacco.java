/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Administrator
 */
public class Pacco implements Serializable {

	private static final long serialVersionUID = 6704336166653777548L;

	private String itemID; // quello che mi passa l'ecomm e che si trova scritto
							// sulla scatola
	private String descrizione;
	private Map<String, String> attributi; // include dimensioni, peso ecc....

	public Pacco() {
		this.attributi = new HashMap<String, String>();
	}

	public Pacco(String itemID, String descrizione,
			Map<String, String> attributi) {
		this.itemID = itemID;
		this.descrizione = descrizione;
		this.attributi = attributi;
	}

	public Map<String, String> getAttributi() {
		return attributi;
	}

	public void setAttributi(Map<String, String> attributi) {
		this.attributi = attributi;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

}
