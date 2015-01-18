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
