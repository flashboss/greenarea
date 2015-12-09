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
package it.vige.greenarea.ln.model;

import java.util.Locale;

import org.w3c.dom.Element;

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.GeoLocationInterface;

public class LNLDAPSite extends LNNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2573104970981869201L;
	String country;
	String region;
	String city;
	String street;
	String number;
	double lat;
	double lon;
	long radius;

	public LNLDAPSite(String name) {
		this();
		super.setName(name);
	}

	public LNLDAPSite() {
		country = Locale.getDefault().getCountry();
		region = "";
		city = "";
		street = "";
		number = "";
		lat = 0.;
		lon = 0.;
		radius = 0;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getLatitude() {
		return lat;
	}

	public void setLatitude(double lat) {
		this.lat = lat;
	}

	public double getLongitude() {
		return lon;
	}

	public void setLongitude(double lon) {
		this.lon = lon;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public long getRadius() {
		return radius;
	}

	public void setRadius(long radius) {
		this.radius = radius;
	}

	@Override
	public void loadElement(Element elt) {
		super.loadElement(elt);
		number = elt.getAttribute("number");
		city = elt.getAttribute("city");
		country = elt.getAttribute("country");
		region = elt.getAttribute("region");
		street = elt.getAttribute("street");
		lat = Double.parseDouble(elt.getAttribute("lat"));
		lon = Double.parseDouble(elt.getAttribute("lon"));
		radius = Long.parseLong(elt.getAttribute("radius"));
	}

	@Override
	public Element toElement() {
		Element elt = super.toElement();
		elt.setAttribute("country", country);
		elt.setAttribute("region", region);
		elt.setAttribute("city", city);
		elt.setAttribute("street", street);
		elt.setAttribute("number", number);
		elt.setAttribute("lat", Double.toString(lat));
		elt.setAttribute("lon", Double.toString(lon));
		elt.setAttribute("radius", Long.toString(radius));
		return elt;
	}

	@Override
	public boolean includes(GeoLocationInterface location) {
		return false;
	}

	@Override
	public GeoLocation locate() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
