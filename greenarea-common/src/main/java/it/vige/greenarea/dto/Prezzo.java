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

public class Prezzo implements Serializable {

	private static final long serialVersionUID = -2033861474989132035L;
	private FasciaOraria fasciaOraria;
	private Color color;
	private double maxPrice;
	private double minPrice;
	private double fixPrice;
	private String typeEntry;

	public Prezzo() {
		
	}
	
	public Prezzo(FasciaOraria fasciaOraria, Color color, double maxPrice,
			double minPrice, double fixPrice, String typeEntry) {
		super();
		this.fasciaOraria = fasciaOraria;
		this.color = color;
		this.maxPrice = maxPrice;
		this.minPrice = minPrice;
		this.fixPrice = fixPrice;
		this.typeEntry = typeEntry;
	}

	public FasciaOraria getFasciaOraria() {
		return fasciaOraria;
	}

	public void setFasciaOraria(FasciaOraria fasciaOraria) {
		this.fasciaOraria = fasciaOraria;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}

	public double getFixPrice() {
		return fixPrice;
	}

	public void setFixPrice(double fixPrice) {
		this.fixPrice = fixPrice;
	}

	public String getTypeEntry() {
		return typeEntry;
	}

	public void setTypeEntry(String typeEntry) {
		this.typeEntry = typeEntry;
	}
}
