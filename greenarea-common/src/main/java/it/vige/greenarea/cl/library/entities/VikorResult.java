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

import it.vige.greenarea.dto.Color;
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
 * 
 */
@Entity
@XmlRootElement
@XmlAccessorType(FIELD)
public class VikorResult implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@XmlElement
	@GeneratedValue(strategy = AUTO)
	private Integer id;
	@XmlElement
	private Date dateMission;
	@XmlElement
	private int idMission;
	@XmlElement
	private double price;
	@XmlElement
	private double qH;
	@XmlElement
	private double qM;
	@XmlElement
	private double qL;
	@XmlElement
	private Color color;

	public Date getDateMission() {
		return dateMission;
	}

	public void setDateMission(Date dateMission) {
		this.dateMission = dateMission;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getIdMission() {
		return idMission;
	}

	public void setIdMission(int idMission) {
		this.idMission = idMission;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getqH() {
		return qH;
	}

	public void setqH(double qH) {
		this.qH = qH;
	}

	public double getqL() {
		return qL;
	}

	public void setqL(double qL) {
		this.qL = qL;
	}

	public double getqM() {
		return qM;
	}

	public void setqM(double qM) {
		this.qM = qM;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof VikorResult)) {
			return false;
		}
		VikorResult other = (VikorResult) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "it.vige.greenarea.cl.library.entities.vikorResult[ id=" + id
				+ " ]";
	}

}
