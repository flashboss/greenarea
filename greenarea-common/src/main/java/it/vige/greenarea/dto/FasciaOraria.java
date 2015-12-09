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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FasciaOraria implements Serializable {

	private static final long serialVersionUID = 5101169084273862013L;
	private DateFormat data = new SimpleDateFormat("d-MM-yyyy");
	private DateFormat orario = new SimpleDateFormat("HH:mm");
	private int id;
	private String nome;
	private String validita;
	private String ga;
	private Date dataInizio;
	private Date dataFine;
	private Date orarioInizio;
	private Date orarioFine;
	private String ripetitivitaPolicy;
	private String aperturaRichieste;
	private String chiusuraRichieste;
	private String tolleranza;
	private String tipologiaClassifica;
	private List<Parametro> parametri;
	private List<Prezzo> prezzi;

	private GreenareaUser pa;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getValidita() {
		return validita;
	}

	public FasciaOraria() {

	}

	public FasciaOraria(String ga, Date dataInizio, Date dataFine, Date orarioInizio, Date orarioFine,
			String aperturaRichieste, String chiusuraRichieste, String ripetitivitaPolicy, String tolleranza,
			String tipologiaClassifica, GreenareaUser pa, List<Parametro> parametri, List<Prezzo> prezzi) {
		super();
		this.ga = ga;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.orarioInizio = orarioInizio;
		this.orarioFine = orarioFine;
		this.aperturaRichieste = aperturaRichieste;
		this.chiusuraRichieste = chiusuraRichieste;
		this.ripetitivitaPolicy = ripetitivitaPolicy;
		this.tolleranza = tolleranza;
		this.tipologiaClassifica = tipologiaClassifica;
		this.pa = pa;
		this.parametri = parametri;
		this.prezzi = prezzi;
		this.nome = orario.format(orarioInizio) + " - " + orario.format(orarioFine);
		this.validita = "dal " + data.format(dataInizio) + " al " + data.format(dataFine);
	}

	public FasciaOraria(int id, List<Parametro> parametri) {
		super();
		this.id = id;
		this.parametri = parametri;
	}

	public GreenareaUser getPa() {
		return pa;
	}

	public void setPa(GreenareaUser pa) {
		this.pa = pa;
	}

	public String getGa() {
		return ga;
	}

	public void setGa(String ga) {
		this.ga = ga;
	}

	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
		String newDataInizio = "";
		String newDataFine = "";
		if (dataInizio != null)
			newDataInizio = data.format(dataInizio);
		if (dataFine != null)
			newDataFine = data.format(dataFine);
		this.validita = "dal " + newDataInizio + " al " + newDataFine;
	}

	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
		String newDataInizio = "";
		String newDataFine = "";
		if (dataInizio != null)
			newDataInizio = data.format(dataInizio);
		if (dataFine != null)
			newDataFine = data.format(dataFine);
		this.validita = "dal " + newDataInizio + " al " + newDataFine;
	}

	public List<Parametro> getParametri() {
		return parametri;
	}

	public void setParametri(List<Parametro> parametri) {
		this.parametri = parametri;
	}

	public List<Prezzo> getPrezzi() {
		return prezzi;
	}

	public void setPrezzi(List<Prezzo> prezzi) {
		this.prezzi = prezzi;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRipetitivitaPolicy() {
		return ripetitivitaPolicy;
	}

	public void setRipetitivitaPolicy(String ripetitivitaPolicy) {
		this.ripetitivitaPolicy = ripetitivitaPolicy;
	}

	public String getAperturaRichieste() {
		return aperturaRichieste;
	}

	public void setAperturaRichieste(String aperturaRichieste) {
		this.aperturaRichieste = aperturaRichieste;
	}

	public String getChiusuraRichieste() {
		return chiusuraRichieste;
	}

	public void setChiusuraRichieste(String chiusuraRichieste) {
		this.chiusuraRichieste = chiusuraRichieste;
	}

	public String getTolleranza() {
		return tolleranza;
	}

	public void setTolleranza(String tolleranza) {
		this.tolleranza = tolleranza;
	}

	public String getTipologiaClassifica() {
		return tipologiaClassifica;
	}

	public void setTipologiaClassifica(String tipologiaClassifica) {
		this.tipologiaClassifica = tipologiaClassifica;
	}

	public Date getOrarioInizio() {
		return orarioInizio;
	}

	public void setOrarioInizio(Date orarioInizio) {
		this.orarioInizio = orarioInizio;
		String newOrarioInizio = "";
		String newOrarioFine = "";
		if (orarioInizio != null)
			newOrarioInizio = orario.format(orarioInizio);
		if (orarioFine != null)
			newOrarioFine = orario.format(orarioFine);
		this.nome = newOrarioInizio + " - " + newOrarioFine;
	}

	public Date getOrarioFine() {
		return orarioFine;
	}

	public void setOrarioFine(Date orarioFine) {
		this.orarioFine = orarioFine;
		String newOrarioInizio = "";
		String newOrarioFine = "";
		if (orarioInizio != null)
			newOrarioInizio = orario.format(orarioInizio);
		if (orarioFine != null)
			newOrarioFine = orario.format(orarioFine);
		this.nome = newOrarioInizio + " - " + newOrarioFine;
	}

	@Override
	public String toString() {
		if (data == null || dataInizio == null || orario == null || orarioInizio == null || dataFine == null
				|| orarioFine == null)
			return super.toString();
		else
			return data.format(dataInizio) + " " + data.format(dataFine) + " | " + orario.format(orarioInizio) + " "
					+ orario.format(orarioFine);
	}
}
