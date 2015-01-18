/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import static javax.persistence.GenerationType.AUTO;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;
import it.vige.greenarea.dto.AperturaRichieste;
import it.vige.greenarea.dto.ChiusuraRichieste;
import it.vige.greenarea.dto.Ripetizione;
import it.vige.greenarea.dto.TipologiaClassifica;
import it.vige.greenarea.dto.Tolleranza;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(FIELD)
public class TimeSlot implements Serializable {

	private static final long serialVersionUID = -7329252948825804964L;

	@Id
	@GeneratedValue(strategy = AUTO)
	@XmlElement
	private Integer idTS;
	@XmlElement
	private AperturaRichieste timeToAcceptRequest;
	@XmlElement
	private ChiusuraRichieste timeToStopRequest;
	@XmlElement
	private ChiusuraRichieste timeToRun;
	@XmlElement
	private Tolleranza tollerance;
	@XmlElement
	private Ripetizione wmy; // Stringa per giorni/settimana/mesi
	@XmlElement
	private String startTS;
	@XmlElement
	private String finishTS;
	@XmlElement
	private String dayStart;
	@XmlElement
	private String dayFinish;
	@XmlElement
	private TipologiaClassifica vikInd;
	@XmlElement
	private String pa;
	@XmlElement
	private String roundCode;

	public TimeSlot() {

	}

	public TimeSlot(int idTS) {
		this.idTS = idTS;
	}

	public TipologiaClassifica getVikInd() {
		return vikInd;
	}

	public void setVikInd(TipologiaClassifica vikInd) {
		this.vikInd = vikInd;
	}

	public String getDayFinish() {
		return dayFinish;
	}

	public void setDayFinish(String dayFinish) {
		this.dayFinish = dayFinish;
	}

	public String getDayStart() {
		return dayStart;
	}

	public void setDayStart(String dayStart) {
		this.dayStart = dayStart;
	}

	public String getFinishTS() {
		return finishTS;
	}

	public void setFinishTS(String finishTS) {
		this.finishTS = finishTS;
	}

	public Integer getIdTS() {
		return idTS;
	}

	public String getStartTS() {
		return startTS;
	}

	public void setStartTS(String startTS) {
		this.startTS = startTS;
	}

	public AperturaRichieste getTimeToAcceptRequest() {
		return timeToAcceptRequest;
	}

	public void setTimeToAcceptRequest(AperturaRichieste timeToAcceptRequest) {
		this.timeToAcceptRequest = timeToAcceptRequest;
	}

	public ChiusuraRichieste getTimeToRun() {
		return timeToRun;
	}

	public void setTimeToRun(ChiusuraRichieste timeToRun) {
		this.timeToRun = timeToRun;
	}

	public ChiusuraRichieste getTimeToStopRequest() {
		return timeToStopRequest;
	}

	public void setTimeToStopRequest(ChiusuraRichieste timeToStopRequest) {
		this.timeToStopRequest = timeToStopRequest;
	}

	public Tolleranza getTollerance() {
		return tollerance;
	}

	public void setTollerance(Tolleranza tollerance) {
		this.tollerance = tollerance;
	}

	public Ripetizione getWmy() {
		return wmy;
	}

	public void setWmy(Ripetizione wmy) {
		this.wmy = wmy;
	}

	public String getPa() {
		return pa;
	}

	public void setPa(String pa) {
		this.pa = pa;
	}

	public String getRoundCode() {
		return roundCode;
	}

	public void setRoundCode(String roundCode) {
		this.roundCode = roundCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idTS == null) ? 0 : idTS.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeSlot other = (TimeSlot) obj;
		if (idTS == null) {
			if (other.idTS != null)
				return false;
		} else if (!idTS.equals(other.idTS))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimeSlot [idTS=" + idTS + ", timeToAcceptRequest="
				+ timeToAcceptRequest + ", timeToStopRequest="
				+ timeToStopRequest + ", timeToRun=" + timeToRun
				+ ", tollerance=" + tollerance + ", wmy=" + wmy + ", startTS="
				+ startTS + ", finishTS=" + finishTS + ", dayStart=" + dayStart
				+ ", dayFinish=" + dayFinish + ", vikInd=" + vikInd + ", pa="
				+ pa + "]";
	}
}
