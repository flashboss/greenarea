/*
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
