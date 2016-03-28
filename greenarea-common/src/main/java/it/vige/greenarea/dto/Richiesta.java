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

import static it.vige.greenarea.Conversioni.convertiGeoLocationToIndirizzo;
import static it.vige.greenarea.GTGsystem.olivetti;
import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static it.vige.greenarea.dto.TipoRichiesta.RITIRO;
import static java.lang.Math.floor;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

import java.io.Serializable;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.FastDateFormat;

import it.vige.greenarea.vo.RichiestaXML;
import it.vige.greenarea.vo.Selectable;

public class Richiesta implements Serializable, Selectable {

	private static final long serialVersionUID = 8567033547077476886L;
	private Format dateFormat = FastDateFormat.getInstance("d-MM-yyyy");
	private String shipmentId;
	private long idStop;
	private String roundCode;
	private String codiceFiliale;
	private String tipo;
	private String stato;
	private String motivazione;
	private GreenareaUser autista;
	private OperatoreLogistico operatoreLogistico;
	private Pacco[] pacchi;
	private String fromName;
	private String toName;
	private Indirizzo fromAddress;
	private Indirizzo toAddress;
	private Date orarioInizio;
	private Date orarioFine;
	private Date dataMissione;
	private FasciaOraria fasciaOraria;
	private Map<String, String> terminiDiConsegna; // es catena del
													// freddo:si/no,
													// deperibile:si/no,
													// priorita'....
	private String note;

	public Richiesta() {
	}

	public Richiesta(Pacco[] pacchi, Map<String, String> terminiDiConsegna, String note) {
		this.pacchi = pacchi;
		this.terminiDiConsegna = terminiDiConsegna;
		this.note = note;
	}

	public Richiesta(String tipo) {
		super();
		this.tipo = tipo;
	}

	public Richiesta(String tipo, GreenareaUser autista) {
		this(tipo);
		this.autista = autista;
	}

	public Richiesta(String tipo, GreenareaUser autista, String stato) {
		this(tipo, autista);
		this.stato = stato;
	}

	public Richiesta(RichiestaXML xml) {
		super();
		setRoundCode(xml.getRoundCode());
		setShipmentId(xml.getShipmentId());
		setCodiceFiliale(xml.getDepot());
		setNote("prova");
		Map<String, String> terminiDiConsegna = new HashMap<String, String>();
		setTerminiDiConsegna(terminiDiConsegna);
		String tntType = xml.getTntType();
		setTipo(tntType.equals("D") ? CONSEGNA.name() : RITIRO.name());
		List<Pacco> pacchi = new ArrayList<Pacco>();
		int numeroPacchi = xml.getPieces();
		for (int i = 0; i < numeroPacchi; i++) {
			Pacco pacco = new Pacco();
			pacco.setDescrizione("pacco" + i);
			pacco.setItemID(xml.getShipmentId() + "-" + i);
			Map<String, String> attributi = new HashMap<String, String>();
			attributi.put("Type", xml.getPackageType() + "");
			attributi.put("Volume", xml.getVolume() / numeroPacchi + "");
			attributi.put("Height", "0");
			attributi.put("Length", "0");
			attributi.put("Width", "0");
			attributi.put("Weight", xml.getWeight() / numeroPacchi + "");
			attributi.put("Stackable", "false");
			attributi.put("KeepUpStanding", "true");
			pacco.setAttributi(attributi);
			pacchi.add(pacco);
		}

		this.pacchi = pacchi.toArray(new Pacco[0]);

		String tipo = xml.getTntType();
		Calendar cal = Calendar.getInstance();
		if (tipo.equals("D")) {
			cal.setTime(xml.getDataEarlestDelivery());
			cal.add(HOUR_OF_DAY, (int) xml.getTimeFromDelivery());
			cal.add(MINUTE, new Integer(new String(floor(xml.getTimeFromDelivery()) + "").split("\\.")[1]));
			orarioInizio = cal.getTime();
			cal.setTime(xml.getDataLatestDelivery());
			cal.add(HOUR_OF_DAY, (int) xml.getTimeToDelivery());
			cal.add(MINUTE, new Integer(new String(floor(xml.getTimeToDelivery()) + "").split("\\.")[1]));
			orarioFine = cal.getTime();
			fromName = "operatore";
			fromAddress = convertiGeoLocationToIndirizzo(olivetti);
			Indirizzo toAddress = new Indirizzo();
			toAddress.setCity(xml.getCity());
			toAddress.setCountry(xml.getCountry());
			toAddress.setNumber(xml.getRoundCode());
			toAddress.setProvince(xml.getProvince());
			toAddress.setStreet(xml.getAddress());
			toAddress.setZipCode(xml.getZipCode() + "");
			toAddress.setLatitude(xml.getLatitude());
			toAddress.setLongitude(xml.getLongitude());
			toName = xml.getCustomer();
			this.toAddress = toAddress;
			this.tipo = CONSEGNA.name();
		} else {
			cal.setTime(xml.getDataEarlestPu());
			cal.add(HOUR_OF_DAY, (int) xml.getTimeFromPu());
			cal.add(MINUTE, new Integer(new String(floor(xml.getTimeFromPu()) + "").split("\\.")[1]));
			orarioInizio = cal.getTime();
			cal.setTime(xml.getDataLatestPu());
			cal.add(HOUR_OF_DAY, (int) xml.getTimeToPu());
			cal.add(MINUTE, new Integer(new String(floor(xml.getTimeToPu()) + "").split("\\.")[1]));
			orarioFine = cal.getTime();
			toName = "operatore";
			toAddress = convertiGeoLocationToIndirizzo(olivetti);
			Indirizzo fromAddress = new Indirizzo();
			fromAddress.setCity(xml.getCity());
			fromAddress.setCountry(xml.getCountry());
			fromAddress.setNumber(xml.getRoundCode());
			fromAddress.setProvince(xml.getProvince());
			fromAddress.setStreet(xml.getAddress());
			fromAddress.setZipCode(xml.getZipCode() + "");
			fromAddress.setLatitude(xml.getLatitude());
			fromAddress.setLongitude(xml.getLongitude());
			fromName = xml.getCustomer();
			this.fromAddress = fromAddress;
			this.tipo = RITIRO.name();
		}
	}

	public Date getDataMissione() {
		return dataMissione;
	}

	public void setDataMissione(Date dataMissione) {
		this.dataMissione = dataMissione;
	}

	public String getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(String shipmentId) {
		this.shipmentId = shipmentId;
	}

	public Indirizzo getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(Indirizzo fromAddress) {
		this.fromAddress = fromAddress;
	}

	public Indirizzo getToAddress() {
		return toAddress;
	}

	public void setToAddress(Indirizzo toAddress) {
		this.toAddress = toAddress;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public GreenareaUser getAutista() {
		return autista;
	}

	public void setAutista(GreenareaUser autista) {
		this.autista = autista;
	}

	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Pacco[] getPacchi() {
		return pacchi;
	}

	public void setPacchi(Pacco[] pacchi) {
		this.pacchi = pacchi;
	}

	public Map<String, String> getTerminiDiConsegna() {
		return terminiDiConsegna;
	}

	public void setTerminiDiConsegna(Map<String, String> terminiDiConsegna) {
		this.terminiDiConsegna = terminiDiConsegna;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public Date getOrarioInizio() {
		return orarioInizio;
	}

	public void setOrarioInizio(Date orarioInizio) {
		this.orarioInizio = orarioInizio;
	}

	public Date getOrarioFine() {
		return orarioFine;
	}

	public void setOrarioFine(Date orarioFine) {
		this.orarioFine = orarioFine;
	}

	public String getRoundCode() {
		return roundCode;
	}

	public void setRoundCode(String roundCode) {
		this.roundCode = roundCode;
	}

	public OperatoreLogistico getOperatoreLogistico() {
		return operatoreLogistico;
	}

	public void setOperatoreLogistico(OperatoreLogistico operatoreLogistico) {
		this.operatoreLogistico = operatoreLogistico;
	}

	public String getCodiceFiliale() {
		return codiceFiliale;
	}

	public void setCodiceFiliale(String codiceFiliale) {
		this.codiceFiliale = codiceFiliale;
	}

	public long getIdStop() {
		return idStop;
	}

	public void setIdStop(long idStop) {
		this.idStop = idStop;
	}

	public FasciaOraria getFasciaOraria() {
		return fasciaOraria;
	}

	public void setFasciaOraria(FasciaOraria fasciaOraria) {
		this.fasciaOraria = fasciaOraria;
	}

	@Override
	public String toString() {
		return (shipmentId != null ? shipmentId + " | " : " | ") + (roundCode != null ? roundCode + " | " : " | ")
				+ (tipo != null ? tipo + " | " : "");
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		if (dataMissione != null)
			return dateFormat.format(dataMissione);
		else
			return null;
	}

	public void setValue(String value) {

	}
}
