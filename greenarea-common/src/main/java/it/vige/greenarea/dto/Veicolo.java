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
