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

public class Veicolo implements Serializable {

	private static final long serialVersionUID = 660117360464881760L;
	private String stato;
	private String targa;
	private GreenareaUser autista;
	private GreenareaUser societaDiTrasporto;
	private OperatoreLogistico operatoreLogistico;
	private ValoriVeicolo valori;
	private String roundCode;
	private String codiceFiliale;
	private String vin;

	public Veicolo() {
	}

	public Veicolo(String stato, String targa) {
		this.stato = stato;
		this.targa = targa;
	}

	public Veicolo(String stato, String targa, GreenareaUser autista,
			GreenareaUser societaDiTrasporto) {
		this(stato, targa);
		this.autista = autista;
		this.societaDiTrasporto = societaDiTrasporto;
	}

	public Veicolo(String stato, String targa, GreenareaUser autista,
			GreenareaUser societaDiTrasporto,
			OperatoreLogistico operatoreLogistico) {
		this(stato, targa, autista, societaDiTrasporto);
		this.operatoreLogistico = operatoreLogistico;
	}

	public Veicolo(String stato, String targa, GreenareaUser autista,
			GreenareaUser societaDiTrasporto,
			OperatoreLogistico operatoreLogistico, ValoriVeicolo valori) {
		this(stato, targa, autista, societaDiTrasporto, operatoreLogistico);
		this.valori = valori;
	}

	public ValoriVeicolo getValori() {
		return valori;
	}

	public void setValori(ValoriVeicolo valori) {
		this.valori = valori;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getTarga() {
		return targa;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}

	public GreenareaUser getAutista() {
		return autista;
	}

	public void setAutista(GreenareaUser autista) {
		this.autista = autista;
	}

	public GreenareaUser getSocietaDiTrasporto() {
		return societaDiTrasporto;
	}

	public void setSocietaDiTrasporto(GreenareaUser societaDiTrasporto) {
		this.societaDiTrasporto = societaDiTrasporto;
	}

	public OperatoreLogistico getOperatoreLogistico() {
		return operatoreLogistico;
	}

	public void setOperatoreLogistico(OperatoreLogistico operatoreLogistico) {
		this.operatoreLogistico = operatoreLogistico;
	}

	public String getRoundCode() {
		return roundCode;
	}

	public void setRoundCode(String roundCode) {
		this.roundCode = roundCode;
	}

	public String getCodiceFiliale() {
		return codiceFiliale;
	}

	public void setCodiceFiliale(String codiceFiliale) {
		this.codiceFiliale = codiceFiliale;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	@Override
	public String toString() {
		return targa + " | " + valori;
	}
}
