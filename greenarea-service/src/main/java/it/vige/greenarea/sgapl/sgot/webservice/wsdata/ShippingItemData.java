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

public class ShippingItemData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String itemID; // quello che mi passa l'ecomm e che si trova scritto
							// sulla scatola
	private String descrizione;
	private HashMap<String, String> attributi; // include dimensioni, peso
												// ecc....

	public ShippingItemData() {
		this.attributi = new HashMap<String, String>();
	}

	public ShippingItemData(String itemID, String descrizione, HashMap<String, String> attributi) {
		this.itemID = itemID;
		this.descrizione = descrizione;
		this.attributi = attributi;
	}

	public HashMap<String, String> getAttributi() {
		return attributi;
	}

	public void setAttributi(HashMap<String, String> attributi) {
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
