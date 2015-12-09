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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(FIELD)
public class ValueMission implements Serializable {

	private static final long serialVersionUID = -3731124278035648822L;

	@Id
	@GeneratedValue(strategy = AUTO)
	@XmlElement
	private Integer idVM;
	@XmlElement
	private int idParameter;
	@XmlElement
	private double valuePar;
	@XmlElement
	@ManyToOne
	private Mission mission;

	public Integer getIdVM() {
		return idVM;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idVM != null ? idVM.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof ValueMission)) {
			return false;
		}
		ValueMission other = (ValueMission) object;
		if ((this.idVM == null && other.idVM != null) || (this.idVM != null && !this.idVM.equals(other.idVM))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return idParameter + ":" + valuePar;
	}

	/**
	 * @return the idParameter
	 */
	public int getIdParameter() {
		return idParameter;
	}

	/**
	 * @param idParameter
	 *            the idParameter to set
	 */
	public void setIdParameter(int idParameter) {
		this.idParameter = idParameter;
	}

	/**
	 * @return the valuePar
	 */
	public double getValuePar() {
		return valuePar;
	}

	/**
	 * @param valuePar
	 *            the valuePar to set
	 */
	public void setValuePar(double valuePar) {
		this.valuePar = valuePar;
	}

	/**
	 * @return the idMission
	 */
	public Mission getMission() {
		return mission;
	}

	/**
	 * @param idMission
	 *            the idMission to set
	 */
	public void setMission(Mission mission) {
		this.mission = mission;
	}

}
