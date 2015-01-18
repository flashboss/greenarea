/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.dto;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 
 * @author Administrator
 */

public class ExchangeSite implements Serializable {

	private static final long serialVersionUID = -6829215850802167430L;
	private String name;
	private String description;
	private GeoLocationInterface location;

	private HashMap<String, String> attributes;

	public ExchangeSite() {
		attributes = new HashMap<String, String>();
	}

	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GeoLocationInterface getLocation() {
		return location;
	}

	public void setLocation(GeoLocationInterface location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return name + ": " + description;
	}

}
