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

import it.vige.greenarea.cl.library.entities.TruckServiceClass;

public class ValoriVeicolo implements Serializable {

	private static final long serialVersionUID = -502382573412821791L;
	private long id;
	private String description;
	private String fuel;
	private double emission;
	private String euro;
	private double lenght;
	private String baseName;
	private String model;
	private double weight;
	private double carico;
	private double consumiPresunti;

	private long tappe;

	public ValoriVeicolo() {

	}

	public ValoriVeicolo(TruckServiceClass truckServiceClass) {
		id = truckServiceClass.getId();
		description = truckServiceClass.getDescription();
		emission = truckServiceClass.getEmissionV();
		euro = truckServiceClass.getEURO();
		fuel = truckServiceClass.getFuelV().name();
		lenght = truckServiceClass.getLenghtV();
		baseName = truckServiceClass.getMakeV();
		model = truckServiceClass.getModelV();
		weight = truckServiceClass.getWeightV();
		consumiPresunti = truckServiceClass.getConsumption();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFuel() {
		return fuel;
	}

	public void setFuel(String fuel) {
		this.fuel = fuel;
	}

	public double getEmission() {
		return emission;
	}

	public void setEmission(double emission) {
		this.emission = emission;
	}

	public String getEuro() {
		return euro;
	}

	public void setEuro(String euro) {
		this.euro = euro;
	}

	public double getLenght() {
		return lenght;
	}

	public void setLenght(double lenght) {
		this.lenght = lenght;
	}

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getCarico() {
		return carico;
	}

	public void setCarico(double carico) {
		this.carico = carico;
	}

	public long getTappe() {
		return tappe;
	}

	public void setTappe(long tappe) {
		this.tappe = tappe;
	}

	public double getConsumiPresunti() {
		return consumiPresunti;
	}

	public void setConsumiPresunti(double consumiPresunti) {
		this.consumiPresunti = consumiPresunti;
	}

	@Override
	public String toString() {
		return baseName + " | " + model;
	}
}
