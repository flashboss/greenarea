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
package it.vige.greenarea.dto;

import java.io.Serializable;

public class Address implements Serializable {

	private static final long serialVersionUID = -3285135234387020803L;

	protected String street;
	protected String number;
	protected String zipCode;
	protected String city;
	protected String province;
	protected String region;
	protected String country;

	public Address() {

	}

	public Address(String street, String number, String city) {
		this.city = city;
		this.street = street;
		this.number = number;
	}

	public Address(String street, String number, String zip, String city) {
		this(street, number, city);
		this.zipCode = zip;
	}

	public Address(String street, String number, String zip, String city, String province) {
		this(street, number, zip, city);
		this.province = province;
	}

	public Address(String street, String number, String zipCode, String city, String province, String region) {
		this(street, number, zipCode, city, province);
		this.region = region;
	}

	public Address(String street, String number, String zipCode, String city, String province, String region,
			String country) {
		this(street, number, zipCode, city, province, region);
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		if ((city != null) && (city.length() > 60)) {
			city = city.substring(0, 60);// serve per evitare errori con nomi
											// troppo lunghi.
		}
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		if ((province != null) && (province.length() > 40)) {
			province = province.substring(0, 40);// serve per evitare errori con
													// nomi troppo lunghi.
		}
		this.province = province;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zip) {
		if ((zip != null) && (zip.length() > 5)) {
			zip = zip.substring(0, 5);// serve per evitare errori con nomi
										// troppo lunghi.
		}
		this.zipCode = zip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		if ((country != null) && (country.length() > 30)) {
			country = country.substring(0, 30);// serve per evitare errori con
												// nomi troppo lunghi.
		}
		this.country = country;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		if ((number != null) && (number.length() > 10)) {
			number = number.substring(0, 10);// serve per evitare errori con
												// nomi troppo lunghi.
		}
		this.number = number;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		if ((street != null) && (street.length() > 60)) {
			street = street.substring(0, 60);// serve per evitare errori con
												// nomi troppo lunghi.
		}
		this.street = street;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	/*
	 * @Override public int hashCode() { int hash = 0; hash += (street != null ?
	 * street.hashCode() : 0); hash += (number != null ? number.hashCode() : 0);
	 * hash += (city != null ? city.hashCode() : 0); return hash; }
	 */

	@Override
	public boolean equals(Object object) { // TODO: Warning - this method won't
											// work in the case the id fields
											// are not set
		if (!(object instanceof Address)) {
			return false;
		}
		Address other = (Address) object;
		if (((this.street == null && other.street != null)
				|| (this.street != null && !this.street.equals(other.street)))
				|| ((this.number == null && other.number != null)
						|| (this.number != null && !this.number.equals(other.number)))
				|| ((this.city == null && other.city != null)
						|| (this.city != null && !this.city.equals(other.city)))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (street != null && (street.length() > 0)) {
			sb.append(street);
		}
		if ((number != null) && (number.length() > 0)) {
			sb.append(" n. ").append(number);
		}
		if (city != null && (city.length() > 0)) {
			sb.append(" - ").append(city).append(" ");
		}
		if (province != null && province.length() > 0) {
			sb.append(" (").append(province).append(")");
		}
		if (region != null && region.length() > 0) {
			sb.append(" - ").append(region);
		}
		return sb.toString();
	}
}
