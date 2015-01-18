package it.vige.greenarea.dto;

import java.io.Serializable;

public class RichiestaPosizioneVeicolo implements Serializable {

	private static final long serialVersionUID = 8320013811746397700L;
	private String idMissione;
	private String targa;

	public String getIdMissione() {
		return idMissione;
	}

	public void setIdMissione(String idMissione) {
		this.idMissione = idMissione;
	}

	public String getTarga() {
		return targa;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}
}
