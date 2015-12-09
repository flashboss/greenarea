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
package it.vige.greenarea.cl.library.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;

import it.vige.greenarea.dto.GeoLocationInterface;

@Embeddable
public class DBGeoLocation implements GeoLocationInterface, Serializable {

	private static final long serialVersionUID = -4935784600837733010L;
	private String name;
	private String surname;
	private String mobile;
	private String phone;
	private String email;

	private String country;
	private String adminAreaLevel1;
	private String adminAreaLevel2;
	private String city;
	private String street;
	private String number;
	private String zipCode;
	private double latitude;
	private double longitude;
	private long radius = 20;

	public DBGeoLocation() {
	}

	public DBGeoLocation(String name, String surname, String phone, String mobile, String email,
			GeoLocationInterface add) {
		this(add);
		this.name = name;
		this.surname = surname;
		this.phone = phone;
		this.mobile = mobile;
		this.email = email;
	}

	public DBGeoLocation(double lat, double lon) {
		this.latitude = lat;
		this.longitude = lon;
	}

	public DBGeoLocation(GeoLocationInterface gli) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("(lat: ").append(Double.toString(latitude)).append(", lon: ").append(Double.toString(longitude));
		sb.append(") address: ").append(street).append(" ").append(number).append(", ").append(city).append(", ");
		sb.append(zipCode).append(" ( ").append(adminAreaLevel1).append(" )");
		return sb.toString();
	}
}
