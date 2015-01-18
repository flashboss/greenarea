/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.ln.model;

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.GeoLocationInterface;

import org.w3c.dom.Element;

/**
 * 
 * @author 00917308
 */
public class LNArea extends LNNode implements GeoLocationInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1872441214157551414L;
	GeoLocation l = new GeoLocation(0., 0.);

	private boolean isZipIn(String zip, String zipCode) {
		if (zip == null || zip.isEmpty())
			return false;
		boolean result = true;
		for (int i = 0; i < 5; i++) {
			result = result
					&& (zipCode.charAt(i) == 'x' || zip.charAt(i) == zipCode
							.charAt(i));
		}
		return result;
	}

	public enum AreaLevel {
		country, adminAreaLevel1, adminAreaLevel2, city, zipCode
	};

	private AreaLevel areaLevel;

	public LNArea(String name, AreaLevel level) {
		this(name);
		areaLevel = level;
	}

	public LNArea(String name) {
		this();
		setName(name);
	}

	public LNArea() {
		super();
		areaLevel = AreaLevel.zipCode;
	}

	public AreaLevel getAreaLevel() {
		return areaLevel;
	}

	public void setAreaLevel(AreaLevel levelDetail) {
		this.areaLevel = levelDetail;
		switch (areaLevel) {
		case country:
			l.setAdminAreaLevel1("");
		case adminAreaLevel1:
			l.setAdminAreaLevel2("");
		case adminAreaLevel2:
			l.setCity("");
		case city:
			l.setZipCode("");
		case zipCode:
		}
	}

	@Override
	public String getCountry() {
		return l.getCountry();
	}

	@Override
	public void setCountry(String country) {
		switch (areaLevel) {
		case country:
		case adminAreaLevel1:
		case adminAreaLevel2:
		case city:
		case zipCode:
			l.setCountry(country);
		}
	}

	@Override
	public String getAdminAreaLevel1() {
		return l.getAdminAreaLevel1();
	}

	@Override
	public void setAdminAreaLevel1(String adminAreaLevel1) {
		switch (areaLevel) {
		case country:
			l.setAdminAreaLevel1("");
			break;
		case adminAreaLevel1:
		case adminAreaLevel2:
		case city:
		case zipCode:
			l.setAdminAreaLevel1(adminAreaLevel1);
		}
	}

	@Override
	public String getAdminAreaLevel2() {
		return l.getAdminAreaLevel2();
	}

	@Override
	public void setAdminAreaLevel2(String adminAreaLevel2) {
		switch (areaLevel) {
		case country:
		case adminAreaLevel1:
			l.setAdminAreaLevel2("");
			break;
		case adminAreaLevel2:
		case city:
		case zipCode:
			l.setAdminAreaLevel2(adminAreaLevel2);
		}
	}

	@Override
	public String getCity() {
		return l.getCity();
	}

	@Override
	public void setCity(String city) {
		switch (areaLevel) {
		case country:
		case adminAreaLevel1:
		case adminAreaLevel2:
			l.setCity("");
			break;
		case city:
		case zipCode:
			l.setCity(city);
		}
	}

	@Override
	public String getZipCode() {
		return l.getZipCode();
	}

	@Override
	public void setZipCode(String zipCode) {
		switch (areaLevel) {
		case country:
		case adminAreaLevel1:
		case adminAreaLevel2:
		case city:
			l.setZipCode("");
			break;
		case zipCode:
			l.setZipCode(zipCode);
		}
	}

	@Override
	public double getLatitude() {
		return l.getLatitude();
	}

	@Override
	public void setLatitude(double lat) {
		l.setLatitude(0.);
	}

	@Override
	public double getLongitude() {
		return l.getLongitude();
	}

	@Override
	public void setLongitude(double lon) {
		l.setLongitude(0.);
	}

	@Override
	public String getNumber() {
		return l.getNumber();
	}

	@Override
	public void setNumber(String number) {
		l.setNumber("");
	}

	@Override
	public long getRadius() {
		return l.getRadius();
	}

	@Override
	public void setRadius(long radius) {
		l.setRadius(-1);
	}

	@Override
	public String getStreet() {
		return l.getStreet();
	}

	@Override
	public void setStreet(String street) {
		l.setStreet("");
	}

	@Override
	public boolean includes(GeoLocationInterface location) {
		boolean result = true;
		switch (areaLevel) {
		case zipCode:
			result = isZipIn(location.getZipCode(), l.getZipCode());
			break;
		case city:
			result = result && l.getCity().equalsIgnoreCase(location.getCity());
		case adminAreaLevel2:
			result = result
					&& l.getAdminAreaLevel2().equalsIgnoreCase(
							location.getAdminAreaLevel2());
		case adminAreaLevel1:
			result = result
					&& l.getAdminAreaLevel1().equalsIgnoreCase(
							location.getAdminAreaLevel1());
		case country:
			result = result
					&& l.getCountry().equalsIgnoreCase(location.getCountry());
		}
		return result;
	}

	@Override
	public GeoLocation locate() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Element toElement() {
		Element el = super.toElement();
		el.setAttribute("areaSpan", areaLevel.toString());
		switch (areaLevel) {
		case zipCode:
			el.setAttribute("zipCode", l.getZipCode());
		case city:
			el.setAttribute("city", l.getCity());
		case adminAreaLevel2:
			el.setAttribute("province", l.getAdminAreaLevel2());
		case adminAreaLevel1:
			el.setAttribute("region", l.getAdminAreaLevel1());
		case country:
			el.setAttribute("country", l.getCountry());
		}
		return el;
	}

	@Override
	public void loadElement(Element el) {
		super.loadElement(el);
		areaLevel = AreaLevel.valueOf(el.getAttribute("areaSpan"));
		switch (areaLevel) {
		case zipCode:
			l.setZipCode(el.getAttribute("zipCode"));
		case city:
			l.setCity(el.getAttribute("city"));
		case adminAreaLevel2:
			l.setAdminAreaLevel2(el.getAttribute("province"));
		case adminAreaLevel1:
			l.setAdminAreaLevel1(el.getAttribute("region"));
		case country:
			l.setCountry(el.getAttribute("country"));
		}

	}
}
