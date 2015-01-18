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
package it.vige.greenarea.cl.library.entities;

import static javax.persistence.GenerationType.AUTO;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * Fascia Oraria - Numero di Richieste Ricevute - Accessi consentiti - Accessi
 * Negati - Accessi a Pagamento
 */
@Entity
@XmlRootElement
@XmlAccessorType(FIELD)
public class TsStat implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3900666775633239423L;
	@Id
	@GeneratedValue(strategy = AUTO)
	@XmlElement
	private Integer idTsStat;
	@XmlElement
	private Date dateMission;
	@XmlElement
	private int inOk;
	@XmlElement
	private int inKo;
	@XmlElement
	private int inWithPayment;
	@XmlElement
	private int totalReq;
	@XmlElement
	private int idTimeSlot;

	public Date getDateMission() {
		return dateMission;
	}

	public void setDateMission(Date dateMission) {
		this.dateMission = dateMission;
	}

	public int getIdTimeSlot() {
		return idTimeSlot;
	}

	public void setIdTimeSlot(int idTimeSlot) {
		this.idTimeSlot = idTimeSlot;
	}

	public Integer getIdTsStat() {
		return idTsStat;
	}

	public int getInKo() {
		return inKo;
	}

	public void setInKo(int inKo) {
		this.inKo = inKo;
	}

	public int getInOk() {
		return inOk;
	}

	public void setInOk(int inOk) {
		this.inOk = inOk;
	}

	public int getInWithPayment() {
		return inWithPayment;
	}

	public void setInWithPayment(int inWithPayment) {
		this.inWithPayment = inWithPayment;
	}

	public int getTotalReq() {
		return totalReq;
	}

	public void setTotalReq(int totalReq) {
		this.totalReq = totalReq;
	}

}
