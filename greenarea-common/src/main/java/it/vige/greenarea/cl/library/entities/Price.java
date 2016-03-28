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

import static javax.persistence.GenerationType.AUTO;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import it.vige.greenarea.dto.AccessoVeicoli;
import it.vige.greenarea.dto.Color;

@Entity
@XmlRootElement
@XmlAccessorType(FIELD)
public class Price implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = AUTO)
	@XmlElement
	private Integer idPrice;
	@XmlElement
	@ManyToOne
	private TimeSlot ts;
	@XmlElement
	private Color color;
	@XmlElement
	private double maxPrice;
	@XmlElement
	private double minPrice;
	@XmlElement
	private double fixPrice;
	@XmlElement
	private AccessoVeicoli typeEntry;

	public TimeSlot getTs() {
		return ts;
	}

	public void setTs(TimeSlot ts) {
		this.ts = ts;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return idPrice;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the maxPrice
	 */
	public double getMaxPrice() {
		return maxPrice;
	}

	/**
	 * @param maxPrice
	 *            the maxPrice to set
	 */
	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}

	/**
	 * @return the minPrice
	 */
	public double getMinPrice() {
		return minPrice;
	}

	/**
	 * @param minPrice
	 *            the minPrice to set
	 */
	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}

	/**
	 * @return the fixPrice
	 */
	public double getFixPrice() {
		return fixPrice;
	}

	/**
	 * @param fixPrice
	 *            the fixPrice to set
	 */
	public void setFixPrice(double fixPrice) {
		this.fixPrice = fixPrice;
	}

	/**
	 * @return the typeEntry
	 */
	public AccessoVeicoli getTypeEntry() {
		return typeEntry;
	}

	/**
	 * @param typeEntry
	 *            the typeEntry to set
	 */
	public void setTypeEntry(AccessoVeicoli typeEntry) {
		this.typeEntry = typeEntry;
	}

}
