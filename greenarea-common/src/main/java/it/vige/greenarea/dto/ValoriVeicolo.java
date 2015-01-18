package it.vige.greenarea.dto;

import it.vige.greenarea.cl.library.entities.TruckServiceClass;

import java.io.Serializable;

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
