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

import it.vige.greenarea.vo.Selectable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.Format;
import java.util.List;

import org.apache.commons.lang.time.FastDateFormat;

public class Missione implements Serializable, Selectable {

	private static final long serialVersionUID = 8084590465033994424L;
	private Format dateFormat = FastDateFormat.getInstance("d-MM-yyyy");
	private String nome;
	private String compagnia;
	private String codiceFiliale;

	private StatoMissione stato;
	private List<Richiesta> richieste;
	private Veicolo veicolo;

	private FasciaOraria fasciaOraria;
	private String lunghezza;
	private Timestamp dataInizio;
	private String carico;
	private String tappe;
	private String euro;
	private String peso;

	private Color ranking;
	private double creditoMobilita;
	private double bonus;

	public Missione() {
	}

	public Missione(String nome, StatoMissione stato) {
		super();
		this.nome = nome;
		this.stato = stato;
	}

	public Missione(String nome, StatoMissione stato, Veicolo veicolo) {
		this(nome, stato);
		this.veicolo = veicolo;
	}

	public Missione(String nome, StatoMissione stato, Veicolo veicolo,
			List<Richiesta> richieste) {
		this(nome, stato, veicolo);
		this.richieste = richieste;
	}

	public Missione(String nome, String compagnia, String lunghezza,
			String carico, String tappe, String euro, String peso,
			StatoMissione stato, List<Richiesta> richieste, Veicolo veicolo,
			Timestamp dataInizio, FasciaOraria fasciaOraria) {
		this(nome, stato, veicolo, richieste);
		this.compagnia = compagnia;
		this.lunghezza = lunghezza;
		this.carico = carico;
		this.tappe = tappe;
		this.euro = euro;
		this.peso = peso;
		this.dataInizio = dataInizio;
		this.fasciaOraria = fasciaOraria;
	}

	public Timestamp getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Timestamp dataInizio) {
		this.dataInizio = dataInizio;
	}

	public List<Richiesta> getRichieste() {
		return richieste;
	}

	public void setRichieste(List<Richiesta> richieste) {
		this.richieste = richieste;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public StatoMissione getStato() {
		return stato;
	}

	public void setStato(StatoMissione stato) {
		this.stato = stato;
	}

	public Veicolo getVeicolo() {
		return veicolo;
	}

	public void setVeicolo(Veicolo veicolo) {
		this.veicolo = veicolo;
	}

	public String getCompagnia() {
		return compagnia;
	}

	public void setCompagnia(String compagnia) {
		this.compagnia = compagnia;
	}

	public String getLunghezza() {
		return lunghezza;
	}

	public void setLunghezza(String lunghezza) {
		this.lunghezza = lunghezza;
	}

	public String getCarico() {
		return carico;
	}

	public void setCarico(String carico) {
		this.carico = carico;
	}

	public String getTappe() {
		return tappe;
	}

	public void setTappe(String tappe) {
		this.tappe = tappe;
	}

	public String getEuro() {
		return euro;
	}

	public void setEuro(String euro) {
		this.euro = euro;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public FasciaOraria getFasciaOraria() {
		return fasciaOraria;
	}

	public void setFasciaOraria(FasciaOraria fasciaOraria) {
		this.fasciaOraria = fasciaOraria;
	}

	public String getCodiceFiliale() {
		if (richieste != null && richieste.size() > 0)
			return richieste.get(0).getCodiceFiliale();
		return codiceFiliale;
	}

	public void setCodiceFiliale(String codiceFiliale) {
		this.codiceFiliale = codiceFiliale;
	}

	public Color getRanking() {
		return ranking;
	}

	public void setRanking(Color ranking) {
		this.ranking = ranking;
	}

	public double getCreditoMobilita() {
		return creditoMobilita;
	}

	public void setCreditoMobilita(double creditoMobilita) {
		this.creditoMobilita = creditoMobilita;
	}

	public double getBonus() {
		return bonus;
	}

	public void setBonus(double bonus) {
		this.bonus = bonus;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		if (dataInizio != null)
			return dateFormat.format(dataInizio);
		else
			return null;
	}
	
	public void setValue(String value) {
		
	}

}
