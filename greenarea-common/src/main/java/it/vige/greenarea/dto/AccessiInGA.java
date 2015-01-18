package it.vige.greenarea.dto;

import java.io.Serializable;

public class AccessiInGA implements Serializable {

	private static final long serialVersionUID = 1058008473856500178L;

	private int accessi;
	
	private double km;
	
	private long tempoTrascorso;

	public long getTempoTrascorso() {
		return tempoTrascorso;
	}

	public void setTempoTrascorso(long tempoTrascorso) {
		this.tempoTrascorso = tempoTrascorso;
	}

	public int getAccessi() {
		return accessi;
	}

	public void setAccessi(int accessi) {
		this.accessi = accessi;
	}

	public double getKm() {
		return km;
	}

	public void setKm(double km) {
		this.km = km;
	}
}
