package it.vige.greenarea.dto;

import java.io.Serializable;
import java.util.Date;

public class DettaglioMissione implements Serializable,
		Comparable<DettaglioMissione> {

	private static final long serialVersionUID = 1461687331653317142L;
	private final static String TOTALE = "missioni_pa_sintesi_table_fields_totale";
	private String chiave;
	private int missioni;
	private int accessiInGATotale;
	private double accessiInGAMedia;
	private double kmPercorsiInGA;
	private long tempoTrascorsoInGA;
	private int numeroStop;
	private int numeroConsegneperStop;
	private double emissioniTotali;
	private double creditidiMobilita;
	private double bonus;
	private Date dal;
	private Date al;

	public DettaglioMissione() {

	}

	public DettaglioMissione(String chiave) {
		this();
		this.chiave = chiave;
	}

	public String getChiave() {
		return chiave;
	}

	public void setChiave(String chiave) {
		this.chiave = chiave;
	}

	public int getMissioni() {
		return missioni;
	}

	public void setMissioni(int missioni) {
		this.missioni = missioni;
	}

	public int getAccessiInGATotale() {
		return accessiInGATotale;
	}

	public void setAccessiInGATotale(int accessiInGATotale) {
		this.accessiInGATotale = accessiInGATotale;
	}

	public double getAccessiInGAMedia() {
		return accessiInGAMedia;
	}

	public void setAccessiInGAMedia(double accessiInGAMedia) {
		this.accessiInGAMedia = accessiInGAMedia;
	}

	public double getKmPercorsiInGA() {
		return kmPercorsiInGA;
	}

	public void setKmPercorsiInGA(double kmPercorsiInGA) {
		this.kmPercorsiInGA = kmPercorsiInGA;
	}

	public long getTempoTrascorsoInGA() {
		return tempoTrascorsoInGA;
	}

	public void setTempoTrascorsoInGA(long tempoTrascorsoInGA) {
		this.tempoTrascorsoInGA = tempoTrascorsoInGA;
	}

	public int getNumeroStop() {
		return numeroStop;
	}

	public void setNumeroStop(int numeroStop) {
		this.numeroStop = numeroStop;
	}

	public int getNumeroConsegneperStop() {
		return numeroConsegneperStop;
	}

	public void setNumeroConsegneperStop(int numeroConsegneperStop) {
		this.numeroConsegneperStop = numeroConsegneperStop;
	}

	public double getEmissioniTotali() {
		return emissioniTotali;
	}

	public void setEmissioniTotali(double emissioniTotali) {
		this.emissioniTotali = emissioniTotali;
	}

	public double getCreditidiMobilita() {
		return creditidiMobilita;
	}

	public void setCreditidiMobilita(double creditidiMobilita) {
		this.creditidiMobilita = creditidiMobilita;
	}

	public double getBonus() {
		return bonus;
	}

	public void setBonus(double bonus) {
		this.bonus = bonus;
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

	@Override
	public int compareTo(DettaglioMissione o) {
		if (chiave != null && chiave.equals(TOTALE))
			return 1;
		if (o != null && o.getChiave().equals(TOTALE))
			return -1;
		if (this.equals(o))
			return 0;
		else
			return -1;
	}

}
