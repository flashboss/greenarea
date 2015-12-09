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

import org.w3c.dom.Element;

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.GeoLocationInterface;
import it.vige.greenarea.geo.GisService;

public class LNSite extends LNNode implements GeoLocationInterface {

	private static final long serialVersionUID = 6779115177104865643L;
	private GeoLocation l = new GeoLocation(0., 0.);

	public LNSite() {
		super();
	}

	public LNSite(String name) {
		this();
		setName(name);
	}

	public void setLocation(GeoLocationInterface gli) {
		if (gli != null)
			this.l = new GeoLocation(gli);
	}

	@Override
	public GeoLocation locate() {
		return this.l;
	}

	@Override
	public boolean includes(GeoLocationInterface location) {
		double result = GisService.getDistance(l.getLatitude(), l.getLongitude(), location.getLatitude(),
				location.getLongitude());
		return result < l.getRadius();
	}

	@Override
	public String getAdminAreaLevel1() {
		return l.getAdminAreaLevel1();
	}

	@Override
	public void setAdminAreaLevel1(String adminAreaLevel1) {
		l.setAdminAreaLevel1(adminAreaLevel1);
	}

	@Override
	public String getAdminAreaLevel2() {
		return l.getAdminAreaLevel2();
	}

	@Override
	public void setAdminAreaLevel2(String adminAreaLevel2) {
		l.setAdminAreaLevel2(adminAreaLevel2);
	}

	@Override
	public String getCity() {
		return l.getCity();
	}

	@Override
	public void setCity(String city) {
		l.setCity(city);
	}

	@Override
	public String getCountry() {
		return l.getCountry();
	}

	@Override
	public void setCountry(String country) {
		l.setCountry(country);
	}

	@Override
	public double getLatitude() {
		return l.getLatitude();
	}

	@Override
	public void setLatitude(double lat) {
		l.setLatitude(lat);
	}

	@Override
	public double getLongitude() {
		return l.getLongitude();
	}

	@Override
	public void setLongitude(double lon) {
		l.setLongitude(lon);
	}

	@Override
	public String getNumber() {
		return l.getNumber();
	}

	@Override
	public void setNumber(String number) {
		l.setNumber(number);
	}

	@Override
	public long getRadius() {
		return l.getRadius();
	}

	@Override
	public void setRadius(long radius) {
		l.setRadius(radius);
	}

	@Override
	public String getStreet() {
		return l.getStreet();
	}

	@Override
	public void setStreet(String street) {
		l.setStreet(street);
	}

	@Override
	public String getZipCode() {
		return l.getZipCode();
	}

	@Override
	public void setZipCode(String zipCode) {
		l.setZipCode(zipCode);
	}

	@Override
	public Element toElement() {
		Element nodeDescriptor = super.toElement();
		nodeDescriptor.setAttribute("number", l.getNumber());
		nodeDescriptor.setAttribute("street", l.getStreet());
		nodeDescriptor.setAttribute("city", l.getCity());
		nodeDescriptor.setAttribute("country", l.getCountry());
		nodeDescriptor.setAttribute("region", l.getAdminAreaLevel1());
		nodeDescriptor.setAttribute("zipCode", l.getZipCode());
		nodeDescriptor.setAttribute("province", l.getAdminAreaLevel2());
		nodeDescriptor.setAttribute("lat", Double.toString(l.getLatitude()));
		nodeDescriptor.setAttribute("lon", Double.toString(l.getLongitude()));
		nodeDescriptor.setAttribute("radius", Long.toString(l.getRadius()));
		return nodeDescriptor;
	}

	@Override
	public void loadElement(Element el) {
		super.loadElement(el);
		l.setNumber(el.getAttribute("number"));
		l.setStreet(el.getAttribute("street"));
		l.setCity(el.getAttribute("city"));
		l.setCountry(el.getAttribute("country"));
		l.setAdminAreaLevel1(el.getAttribute("region"));
		l.setZipCode(el.getAttribute("zipCode"));
		l.setAdminAreaLevel2(el.getAttribute("province"));
		l.setLatitude(Double.parseDouble(el.getAttribute("lat")));
		l.setLongitude(Double.parseDouble(el.getAttribute("lon")));
		l.setRadius(Long.parseLong(el.getAttribute("radius")));
	}
}
