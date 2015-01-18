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
import java.util.Date;

public class PerformanceVeicoli implements Serializable {

	private static final long serialVersionUID = -2836583043040487452L;

	private int numeroMissioni;
	private double numeroKmPercorsiInGA;
	private Fuel tipoAlimentazione;
	private Date dal;
	private Date al;
	private String targa;
	private int classeEcologica;
	private double consumoTotale;
	private int numeroMedioConsegneAMissione;
	private double percentualeConsegneABuonFine;
	private int numeroMedioDiRitiriAMissione;

	public PerformanceVeicoli() {

	}

	public PerformanceVeicoli(String targa) {
		this();
		this.targa = targa;
	}

	public int getNumeroMissioni() {
		return numeroMissioni;
	}

	public void setNumeroMissioni(int numeroMissioni) {
		this.numeroMissioni = numeroMissioni;
	}

	public double getNumeroKmPercorsiInGA() {
		return numeroKmPercorsiInGA;
	}

	public void setNumeroKmPercorsiInGA(double numeroKmPercorsiInGA) {
		this.numeroKmPercorsiInGA = numeroKmPercorsiInGA;
	}

	public Date getDal() {
		return dal;
	}

	public void setDal(Date dal) {
		this.dal = dal;
	}

	public Date getAl() {
		return al;
	}

	public void setAl(Date al) {
		this.al = al;
	}

	public String getTarga() {
		return targa;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}

	public int getClasseEcologica() {
		return classeEcologica;
	}

	public void setClasseEcologica(int classeEcologica) {
		this.classeEcologica = classeEcologica;
	}

	public double getConsumoTotale() {
		return consumoTotale;
	}

	public void setConsumoTotale(double consumoTotale) {
		this.consumoTotale = consumoTotale;
	}

	public int getNumeroMedioConsegneAMissione() {
		return numeroMedioConsegneAMissione;
	}

	public void setNumeroMedioConsegneAMissione(
			int numeroMedioConsegneAMissione) {
		this.numeroMedioConsegneAMissione = numeroMedioConsegneAMissione;
	}

	public double getPercentualeConsegneABuonFine() {
		return percentualeConsegneABuonFine;
	}

	public void setPercentualeConsegneABuonFine(
			double percentualeConsegneABuonFine) {
		this.percentualeConsegneABuonFine = percentualeConsegneABuonFine;
	}

	public int getNumeroMedioDiRitiriAMissione() {
		return numeroMedioDiRitiriAMissione;
	}

	public void setNumeroMedioDiRitiriAMissione(
			int numeroMedioDiRitiriAMissione) {
		this.numeroMedioDiRitiriAMissione = numeroMedioDiRitiriAMissione;
	}

	public Fuel getTipoAlimentazione() {
		return tipoAlimentazione;
	}

	public void setTipoAlimentazione(Fuel tipoAlimentazione) {
		this.tipoAlimentazione = tipoAlimentazione;
	}

}
