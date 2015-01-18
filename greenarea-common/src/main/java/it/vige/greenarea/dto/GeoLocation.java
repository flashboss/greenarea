/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author 00917308
 */
@XmlType(name="GeoLocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoLocation implements GeoLocationInterface, Serializable {

	private static final long serialVersionUID = 2486518276595934321L;
	@XmlElement(required = true)
    private String country;
    private String adminAreaLevel1;
    private String adminAreaLevel2;
    @XmlElement(required = true)
    private String city;
    @XmlElement(required = true)
    private String street;
    private String number;
    private String zipCode;
    private double latitude;
    private double longitude;
    private long radius = new Long(20);

	public GeoLocation() {
	}

	public GeoLocation(double lat, double lon) {
		this.latitude = lat;
		this.longitude = lon;
	}

	public GeoLocation(GeoLocationInterface gli) {
		this(0., 0.);
		if (gli != null) {
			country = gli.getCountry();
			adminAreaLevel1 = gli.getAdminAreaLevel1();
			adminAreaLevel2 = gli.getAdminAreaLevel2();
			city = gli.getCity();
			street = gli.getStreet();
			number = gli.getNumber();
			zipCode = gli.getZipCode();
			latitude = gli.getLatitude();
			longitude = gli.getLongitude();
			radius = gli.getRadius();
		}
	}

	public GeoLocation(String street, String number, String city) {
		this.city = city;
		this.street = street;
		this.number = number;
	}

	public GeoLocation(String street, String number, String zip, String city) {
		this(street, number, city);
		this.zipCode = zip;
	}

	public GeoLocation(String street, String number, String zip, String city,
			String province) {
		this(street, number, zip, city);
		this.adminAreaLevel2 = province;
	}

	public GeoLocation(String street, String number, String zipCode,
			String city, String province, String region) {
		this(street, number, zipCode, city, province);
		this.adminAreaLevel1 = region;
	}

	public GeoLocation(String street, String number, String zipCode,
			String city, String province, String region, String country) {
		this(street, number, zipCode, city, province, region);
		this.country = country;
	}

	public GeoLocation(String street, String number, String city, double lat,
			double lon) {
		this(street, number, city);
		this.latitude = lat;
		this.longitude = lon;
	}

	public GeoLocation(String street, String number, String zip, String city,
			double lat, double lon) {
		this(street, number, zip, city);
		this.latitude = lat;
		this.longitude = lon;
	}

	public GeoLocation(String street, String number, String zip, String city,
			String province, double lat, double lon) {
		this(street, number, zip, city, province);
		this.latitude = lat;
		this.longitude = lon;
	}

	public GeoLocation(String street, String number, String zipCode,
			String city, String province, String region, double lat, double lon) {
		this(street, number, zipCode, city, province, region);
		this.latitude = lat;
		this.longitude = lon;
	}

	public GeoLocation(String street, String number, String zipCode,
			String city, String province, String region, String country,
			double lat, double lon) {
		this(street, number, zipCode, city, province, region, country);
		this.latitude = lat;
		this.longitude = lon;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String cap) {
		this.zipCode = cap;
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
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public long getRadius() {
		return radius;
	}

	public void setRadius(long radius) {
		this.radius = radius;
	}

	public String getAdminAreaLevel1() {
		return adminAreaLevel1;
	}

	public void setAdminAreaLevel1(String region) {
		this.adminAreaLevel1 = region;
	}

	public String getAdminAreaLevel2() {
		return adminAreaLevel2;
	}

	public void setAdminAreaLevel2(String adminAreaLevel2) {
		this.adminAreaLevel2 = adminAreaLevel2;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("(lat: ").append(Double.toString(latitude)).append(", lon: ")
				.append(Double.toString(longitude));
		sb.append(") address: ").append(street).append(" ").append(number)
				.append(", ").append(city).append(", ");
		sb.append(zipCode).append(" ( ").append(adminAreaLevel1).append(" )");
		return sb.toString();
	}
}
