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
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import it.vige.greenarea.dto.Fuel;

@Entity
@XmlRootElement
@XmlAccessorType(FIELD)
public class TruckServiceClass implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;
	@XmlElement
	private String makeV;
	@XmlElement
	private String modelV;
	@XmlElement
	private double lenghtV;
	@XmlElement
	private double weightV;
	@XmlElement
	private double emissionV;
	@XmlElement
	private double consumption;
	@XmlElement
	private Fuel fuelV;
	@XmlElement
	private String EURO;
	@XmlElement
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEURO() {
		return EURO;
	}

	public void setEURO(String EURO) {
		this.EURO = EURO;
	}

	public double getEmissionV() {
		return emissionV;
	}

	public void setEmissionV(double emissionV) {
		this.emissionV = emissionV;
	}

	public Fuel getFuelV() {
		return fuelV;
	}

	public void setFuelV(Fuel fuelV) {
		this.fuelV = fuelV;
	}

	public double getLenghtV() {
		return lenghtV;
	}

	public void setLenghtV(double lenghtV) {
		this.lenghtV = lenghtV;
	}

	public String getMakeV() {
		return makeV;
	}

	public void setMakeV(String makeV) {
		this.makeV = makeV;
	}

	public String getModelV() {
		return modelV;
	}

	public void setModelV(String modelV) {
		this.modelV = modelV;
	}

	public double getWeightV() {
		return weightV;
	}

	public void setWeightV(double weightV) {
		this.weightV = weightV;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((makeV == null) ? 0 : makeV.hashCode());
		result = prime * result + ((modelV == null) ? 0 : modelV.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TruckServiceClass other = (TruckServiceClass) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (makeV == null) {
			if (other.makeV != null)
				return false;
		} else if (!makeV.equals(other.makeV))
			return false;
		if (modelV == null) {
			if (other.modelV != null)
				return false;
		} else if (!modelV.equals(other.modelV))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TruckServiceClass [id=" + id + ", makeV=" + makeV + ", modelV=" + modelV + ", lenghtV=" + lenghtV
				+ ", weightV=" + weightV + ", emissionV=" + emissionV + ", fuelV=" + fuelV + ", EURO=" + EURO
				+ ", description=" + description + "]";
	}

}
