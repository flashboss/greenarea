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

public class ImpattoAmbientale implements Serializable {

	private static final long serialVersionUID = -2836583043040487452L;

	private String chiave;
	private int numeroMissioni;
	private double percentualeMissioni;
	private double numeroKmPercorsiInGA;
	private double percentualeKmPercorsiInGA;
	private double numeroEmissioni;
	private double percentualeEmissioni;
	private Date dal;
	private Date al;

	public ImpattoAmbientale() {

	}

	public ImpattoAmbientale(String chiave) {
		this();
		this.chiave = chiave;
	}

	public String getChiave() {
		return chiave;
	}

	public void setChiave(String chiave) {
		this.chiave = chiave;
	}

	public int getNumeroMissioni() {
		return numeroMissioni;
	}

	public void setNumeroMissioni(int numeroMissioni) {
		this.numeroMissioni = numeroMissioni;
	}

	public double getPercentualeMissioni() {
		return percentualeMissioni;
	}

	public void setPercentualeMissioni(double percentualeMissioni) {
		this.percentualeMissioni = percentualeMissioni;
	}

	public double getNumeroKmPercorsiInGA() {
		return numeroKmPercorsiInGA;
	}

	public void setNumeroKmPercorsiInGA(double numeroKmPercorsiInGA) {
		this.numeroKmPercorsiInGA = numeroKmPercorsiInGA;
	}

	public double getPercentualeKmPercorsiInGA() {
		return percentualeKmPercorsiInGA;
	}

	public void setPercentualeKmPercorsiInGA(double percentualeKmPercorsiInGA) {
		this.percentualeKmPercorsiInGA = percentualeKmPercorsiInGA;
	}

	public double getNumeroEmissioni() {
		return numeroEmissioni;
	}

	public void setNumeroEmissioni(double numeroEmissioni) {
		this.numeroEmissioni = numeroEmissioni;
	}

	public double getPercentualeEmissioni() {
		return percentualeEmissioni;
	}

	public void setPercentualeEmissioni(double percentualeEmissioni) {
		this.percentualeEmissioni = percentualeEmissioni;
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

}
