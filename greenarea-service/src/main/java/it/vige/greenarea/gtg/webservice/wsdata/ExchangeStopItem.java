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
package it.vige.greenarea.gtg.webservice.wsdata;

import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.dto.GeoLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ExchangeStop")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeStopItem implements Serializable {
	
	private static final long serialVersionUID = -446417671520983480L;
	Long id;
	String description;
	GeoLocation geoLocation;
	List<String> collectingItems;
	List<String> deliveryItems;

	public ExchangeStopItem() {
		this.geoLocation = new GeoLocation();
		this.collectingItems = new ArrayList<String>();
		this.deliveryItems = new ArrayList<String>();
	}

	public ExchangeStopItem(ExchangeStop es) {
		this.id = es.getId();
		this.description = es.getLocation().getStreet();
		this.geoLocation = new GeoLocation(es.getLocation());

		this.collectingItems = new ArrayList<String>();
		for (Freight f : es.getCollectingList()) {
			collectingItems.add(f.getCodeId());
		}
		this.deliveryItems = new ArrayList<String>();
		for (Freight f : es.getDeliveryList()) {
			deliveryItems.add(f.getCodeId());
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public GeoLocation getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
	}

	public List<String> getCollectingItems() {
		return collectingItems;
	}

	public void setCollectingItems(List<String> collectingItems) {
		this.collectingItems = collectingItems;
	}

	public List<String> getDeliveryItems() {
		return deliveryItems;
	}

	public void setDeliveryItems(List<String> deliveryItems) {
		this.deliveryItems = deliveryItems;
	}

}
