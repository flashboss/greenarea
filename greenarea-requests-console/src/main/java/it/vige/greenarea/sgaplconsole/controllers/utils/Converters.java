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
package it.vige.greenarea.sgaplconsole.controllers.utils;

import it.vige.greenarea.sgapl.sgot.webservice.Address;
import it.vige.greenarea.sgapl.sgot.webservice.DbGeoLocation;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingItemData;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderData;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderDetails;
import it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderDetails.TerminiDiConsegna.Entry;
import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.itseasy.lib.configurationData.FreightAttribs;
import it.vige.greenarea.sgaplconsole.data.Attributi;
import it.vige.greenarea.sgaplconsole.data.FreightItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Converters {

	public static Address convertAddress(DBGeoLocation s) {
		if (s == null) {
			return null;
		}
		Address a = new Address();
		a.setCity(s.getCity());
		a.setCountry(s.getCountry());
		a.setNumber(s.getNumber());
		a.setProvince(s.getAdminAreaLevel2());
		a.setRegion(s.getAdminAreaLevel1());
		a.setStreet(s.getStreet());
		a.setZipCode(s.getZipCode());
		return a;
	}

	public static DBGeoLocation convertLocation(DbGeoLocation s) {
		if (s == null) {
			return null;
		}
		DBGeoLocation sl;
		sl = new DBGeoLocation();
		sl.setAdminAreaLevel1(s.getAdminAreaLevel1());
		sl.setAdminAreaLevel2(s.getAdminAreaLevel2());
		sl.setCity(s.getCity());
		sl.setCountry(s.getCountry());
		sl.setEmail(s.getEmail());
		sl.setLatitude(s.getLatitude());
		sl.setLongitude(s.getLongitude());
		sl.setMobile(s.getMobile());
		sl.setName(s.getName());
		sl.setNumber(s.getNumber());
		sl.setPhone(s.getPhone());
		sl.setRadius(s.getRadius());
		sl.setStreet(s.getStreet());
		sl.setSurname(s.getSurname());
		sl.setZipCode(s.getZipCode());
		return sl;
	}

	public static ShippingOrderData convertOrderData(
			it.vige.greenarea.sgapl.sgot.webservice.ShippingOrderDetails s) {
		if (s == null) {
			return null;
		}
		ShippingOrderData sod;
		sod = new ShippingOrderData();
		sod.setNote(s.getNote());
		ShippingOrderData.TerminiDiConsegna tsdo = new ShippingOrderData.TerminiDiConsegna();
		Iterator<Entry> is = s.getTerminiDiConsegna().getEntry().iterator();
		ShippingOrderData.TerminiDiConsegna.Entry sode = new ShippingOrderData.TerminiDiConsegna.Entry();
		while (is.hasNext()) {
			ShippingOrderDetails.TerminiDiConsegna.Entry se = (ShippingOrderDetails.TerminiDiConsegna.Entry) is
					.next();
			sode.setKey(se.getKey());
			sode.setValue(se.getValue());
			tsdo.getEntry().add(sode);
		}
		sod.setTerminiDiConsegna(tsdo);
		sod.getShippingItems().addAll(s.getItemsList());
		return sod;
	}

	public static List<FreightItem> convertShippingItemList(
			List<ShippingItemData> sidL) {
		if (sidL == null) {
			return null;
		}
		List<FreightItem> fiL = new ArrayList<FreightItem>();
		for (ShippingItemData si : sidL) {
			fiL.add(convertShippingItem(si));
		}
		return fiL;
	}

	public static List<ShippingItemData> convertFreightItemList(
			List<FreightItem> fiL) {
		if (fiL == null) {
			return null;
		}
		List<ShippingItemData> sidL = new ArrayList<ShippingItemData>();
		for (FreightItem fi : fiL) {
			sidL.add(convertFreightItem(fi));
		}
		return sidL;
	}

	public static FreightItem convertShippingItem(ShippingItemData sid) {
		if (sid == null) {
			return null;
		}
		FreightItem fi = new FreightItem();
		fi.setCodeId(sid.getItemID());
		fi.setDescription(sid.getDescrizione());
		for (ShippingItemData.Attributi.Entry e : sid.getAttributi().getEntry()) {

			if (e.getKey().equals("Type")) {
				fi.setFt(e.getValue());
			}
			for (FreightAttribs fa : FreightAttribs.values()) {
				switch (fa) {
				case Height:
					if (e.getKey().equals(fa.name())) {
						fi.setHeight(new Integer(e.getValue() == null ? "0" : e
								.getValue()));
					}
					break;
				case Length:
					if (e.getKey().equals(fa.name())) {
						fi.setLenght(new Integer(e.getValue() == null ? "0" : e
								.getValue()));
					}
					break;
				case Volume:
					if (e.getKey().equals(fa.name())) {
						fi.setVolume(new Integer(e.getValue() == null ? "10"
								: e.getValue()));
					}
					break;
				case Weight:
					if (e.getKey().equals(fa.name())) {
						fi.setWeight(new Integer(e.getValue() == null ? "0" : e
								.getValue()));
					}
					break;
				case Width:
					if (e.getKey().equals(fa.name())) {
						fi.setWidth(new Integer(e.getValue() == null ? "0" : e
								.getValue()));
					}
					break;
				case KeepUpStanding:
					if (e.getKey().equals(fa.name())) {
						fi.setKeepUpStanding(new Boolean(
								e.getValue() == null ? "false" : e.getValue()));
					}
					break;
				case Stackable:
					if (e.getKey().equals(fa.name())) {
						fi.setStackable(new Boolean(
								e.getValue() == null ? "false" : e.getValue()));
					}
					break;
				}
			}
		}
		return fi;
	}

	public static ShippingItemData convertFreightItem(FreightItem fi) {
		ShippingItemData sid = new ShippingItemData();// sgotObj.createShippingItemData();
		sid.setItemID(fi.getCodeId());
		sid.setDescrizione(fi.getDescription());
		sid.setAttributi(new ShippingItemData.Attributi());// sgotObj.createShippingItemDataAttributi());
		ShippingItemData.Attributi.Entry shAttr = new ShippingItemData.Attributi.Entry();

		shAttr.setKey("Type");
		shAttr.setValue(String.valueOf(fi.getFt()));
		sid.getAttributi().getEntry().add(shAttr);

		shAttr = new ShippingItemData.Attributi.Entry();
		shAttr.setKey(FreightAttribs.Volume.name());
		shAttr.setValue(String.valueOf(fi.getVolume()));
		sid.getAttributi().getEntry().add(shAttr);

		shAttr = new ShippingItemData.Attributi.Entry();
		shAttr.setKey(FreightAttribs.Height.name());
		shAttr.setValue(String.valueOf(fi.getHeight()));
		sid.getAttributi().getEntry().add(shAttr);

		shAttr = new ShippingItemData.Attributi.Entry();
		shAttr.setKey(FreightAttribs.Length.name());
		shAttr.setValue(String.valueOf(fi.getLenght()));
		sid.getAttributi().getEntry().add(shAttr);

		shAttr = new ShippingItemData.Attributi.Entry();
		shAttr.setKey(FreightAttribs.Width.name());
		shAttr.setValue(String.valueOf(fi.getWidth()));
		sid.getAttributi().getEntry().add(shAttr);

		shAttr = new ShippingItemData.Attributi.Entry();
		shAttr.setKey(FreightAttribs.Weight.name());
		shAttr.setValue(String.valueOf(fi.getWeight()));
		sid.getAttributi().getEntry().add(shAttr);

		shAttr = new ShippingItemData.Attributi.Entry();
		shAttr.setKey(FreightAttribs.Stackable.name());
		shAttr.setValue(String.valueOf(fi.isStackable()));
		sid.getAttributi().getEntry().add(shAttr);

		shAttr = new ShippingItemData.Attributi.Entry();
		shAttr.setKey(FreightAttribs.KeepUpStanding.name());
		shAttr.setValue(String.valueOf(fi.isKeepUpStanding()));
		sid.getAttributi().getEntry().add(shAttr);

		for (Attributi a : fi.getAttributi()) {
			shAttr = new ShippingItemData.Attributi.Entry();// sgotObj.createShippingItemDataAttributiEntry();
			shAttr.setKey(a.getKey());
			shAttr.setValue(a.getVal());
			sid.getAttributi().getEntry().add(shAttr);
		}
		return sid;
	}
}
