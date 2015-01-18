/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 * 
 * @author 00917377
 */
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
