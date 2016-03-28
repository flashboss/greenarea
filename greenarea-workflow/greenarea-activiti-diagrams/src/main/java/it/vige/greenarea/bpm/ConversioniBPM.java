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
package it.vige.greenarea.bpm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.Indirizzo;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Pacco;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.sgapl.sgot.webservice.Address;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingItemData;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingItemData.Attributi;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderData;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderData.TerminiDiConsegna;

public class ConversioniBPM {

	public static Address convertiIndirizzoToAddress(Indirizzo indirizzo) {
		Address address = new Address();
		address.setCity(indirizzo.getCity());
		address.setCountry(indirizzo.getCountry());
		address.setNumber(indirizzo.getNumber());
		address.setProvince(indirizzo.getProvince());
		address.setRegion(indirizzo.getRegion());
		address.setStreet(indirizzo.getStreet());
		address.setZipCode(indirizzo.getZipCode());
		return address;
	}

	public static ShippingOrderData convertiRichiesta(Richiesta richiesta, String operatoreLogistico) {
		ShippingOrderData shippingOrderData = new ShippingOrderData();
		shippingOrderData.setShipmentId(richiesta.getShipmentId());
		shippingOrderData.setNote(richiesta.getNote());
		shippingOrderData.setOperatoreLogistico(operatoreLogistico);
		TerminiDiConsegna terminiDiConsegna = new TerminiDiConsegna();
		Map<String, String> tcRichiesta = richiesta.getTerminiDiConsegna();
		for (String key : tcRichiesta.keySet()) {
			TerminiDiConsegna.Entry entry = new TerminiDiConsegna.Entry();
			entry.setKey(key);
			entry.setValue(tcRichiesta.get(key));
			terminiDiConsegna.getEntry().add(entry);
		}
		shippingOrderData.setTerminiDiConsegna(terminiDiConsegna);
		Pacco[] pacchi = richiesta.getPacchi();
		for (Pacco pacco : pacchi) {
			ShippingItemData shippingItemData = new ShippingItemData();
			shippingItemData.setDescrizione(pacco.getDescrizione());
			shippingItemData.setItemID(pacco.getItemID());
			Map<String, String> attributiRichiesta = pacco.getAttributi();
			Attributi attributi = new Attributi();
			for (String key : attributiRichiesta.keySet()) {
				Attributi.Entry entry = new Attributi.Entry();
				entry.setKey(key);
				entry.setValue(attributiRichiesta.get(key));
				attributi.getEntry().add(entry);
			}
			shippingItemData.setAttributi(attributi);
			shippingOrderData.getShippingItems().add(shippingItemData);
		}
		shippingOrderData.setToName(richiesta.getToName());
		shippingOrderData.setFromName(richiesta.getFromName());
		shippingOrderData.setToAddress(convertiIndirizzoToAddress(richiesta.getToAddress()));
		shippingOrderData.setFromAddress(convertiIndirizzoToAddress(richiesta.getFromAddress()));
		if (richiesta.getOperatoreLogistico() != null)
			shippingOrderData.setOperatoreLogistico(richiesta.getOperatoreLogistico().getId());
		return shippingOrderData;
	}

	public static Richiesta convertiRichiesta(ShippingOrderData shippingOrderData) {
		Richiesta richiesta = new Richiesta();
		richiesta.setNote(shippingOrderData.getNote());
		Map<String, String> terminiDiConsegna = new HashMap<String, String>();
		List<TerminiDiConsegna.Entry> tcRichiesta = shippingOrderData.getTerminiDiConsegna().getEntry();
		for (TerminiDiConsegna.Entry entry : tcRichiesta)
			terminiDiConsegna.put(entry.getKey(), entry.getValue());
		richiesta.setTerminiDiConsegna(terminiDiConsegna);
		List<ShippingItemData> shippingItems = shippingOrderData.getShippingItems();
		List<Pacco> pacchi = new ArrayList<Pacco>();
		for (ShippingItemData shippingItemData : shippingItems) {
			Pacco pacco = new Pacco();
			pacco.setDescrizione(shippingItemData.getDescrizione());
			pacco.setItemID(shippingItemData.getItemID());
			List<Attributi.Entry> attributi = shippingItemData.getAttributi().getEntry();
			Map<String, String> attributiRichiesta = new HashMap<String, String>();
			for (Attributi.Entry entry : attributi)
				attributiRichiesta.put(entry.getKey(), entry.getValue());
			pacco.setAttributi(attributiRichiesta);
			pacchi.add(pacco);
		}
		richiesta.setPacchi(pacchi.toArray(new Pacco[0]));
		richiesta.setToName(shippingOrderData.getToName());
		richiesta.setFromName(shippingOrderData.getFromName());
		richiesta.setToAddress(convertiIndirizzo(shippingOrderData.getToAddress(), richiesta.getToName()));
		richiesta.setFromAddress(convertiIndirizzo(shippingOrderData.getFromAddress(), richiesta.getFromName()));
		richiesta.setOperatoreLogistico(
				new OperatoreLogistico(new GreenareaUser(shippingOrderData.getOperatoreLogistico())));
		return richiesta;
	}

	public static Indirizzo convertiIndirizzo(Address address, String name) {
		Indirizzo indirizzo = new Indirizzo();
		indirizzo.setCity(address.getCity());
		indirizzo.setCountry(address.getCountry());
		indirizzo.setNumber(address.getNumber());
		indirizzo.setProvince(address.getProvince());
		indirizzo.setRegion(address.getRegion());
		indirizzo.setStreet(address.getStreet());
		indirizzo.setZipCode(address.getZipCode());
		return indirizzo;
	}

}
