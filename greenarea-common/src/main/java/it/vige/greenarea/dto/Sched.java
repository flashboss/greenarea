/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * 
 */
@XmlRootElement
@XmlAccessorType(FIELD)
public class Sched implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	private Integer idSc;
	@XmlElement
	private int idTimeSlot;
	@XmlElement
	private String timeSlot;
	@XmlElement
	private Date timeAccept;
	@XmlElement
	private Date timeClosing;
	@XmlElement
	private Date timeRank;
	@XmlElement
	private String request;
	@XmlElement
	private Date dateMiss;
	@XmlElement
	private String roundCode;

	public Date getDateMiss() {
		return dateMiss;
	}

	public void setDateMiss(Date dateMiss) {
		this.dateMiss = dateMiss;
	}

	public Date getTimeAccept() {
		return timeAccept;
	}

	public void setTimeAccept(Date timeAccept) {
		this.timeAccept = timeAccept;
	}

	public int getIdTimeSlot() {
		return idTimeSlot;
	}

	public void setIdTimeSlot(int idTimeSlot) {
		this.idTimeSlot = idTimeSlot;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public Date getTimeClosing() {
		return timeClosing;
	}

	public void setTimeClosing(Date timeClosing) {
		this.timeClosing = timeClosing;
	}

	public Date getTimeRank() {
		return timeRank;
	}

	public void setTimeRank(Date timeRank) {
		this.timeRank = timeRank;
	}

	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

	public Integer getIdSc() {
		return idSc;
	}

	public String getRoundCode() {
		return roundCode;
	}

	public void setRoundCode(String roundCode) {
		this.roundCode = roundCode;
	}

}
