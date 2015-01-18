/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgrl.webservices;

import it.vige.greenarea.dto.GeoLocation;

/**
 * 
 * @author 00917308
 */
public class SGRLExchangeStop extends SgrlNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6277537244364420453L;
	private GeoLocation location;
	private String locationNotes;

	public GeoLocation getLocation() {
		return location;
	}

	public void setLocation(GeoLocation location) {
		this.location = location;
	}

	public String getLocationNotes() {
		return locationNotes;
	}

	public void setLocationNotes(String locationNotes) {
		this.locationNotes = locationNotes;
	}

}
