/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import static javax.persistence.FetchType.EAGER;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;
import it.vige.greenarea.dto.StatoVeicolo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(FIELD)
public class Vehicle implements Serializable {
	private static final long serialVersionUID = -5519100393242836375L;
	@Id
	@Column(name = "PLATENUMBER", length = 10, nullable = false, unique = true)
	@XmlElement
	private String plateNumber;
	@Column(name = "TRUCKSTATE")
	private StatoVeicolo state;
	@ElementCollection(fetch = EAGER)
	private List<Attachment> attachments;
	@ManyToOne
	private TruckServiceClass serviceClass;
	@XmlElement
	private String autista;
	@XmlElement
	private String societaDiTrasporto;
	@XmlElement
	private String operatoreLogistico;
	@XmlElement
	private String codiceFiliale;
	@XmlElement
	private String roundCode;
	@XmlElement
	private String vin;

	public Vehicle() {
	}

	public Vehicle(String plateNumber) {
		this();
		this.plateNumber = plateNumber;
		this.state = StatoVeicolo.IDLE;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public StatoVeicolo getState() {
		return state;
	}

	public void setState(StatoVeicolo state) {
		this.state = state;
	}

	public TruckServiceClass getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(TruckServiceClass serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getAutista() {
		return autista;
	}

	public void setAutista(String autista) {
		this.autista = autista;
	}

	public String getSocietaDiTrasporto() {
		return societaDiTrasporto;
	}

	public void setSocietaDiTrasporto(String societaDiTrasporto) {
		this.societaDiTrasporto = societaDiTrasporto;
	}

	public String getOperatoreLogistico() {
		return operatoreLogistico;
	}

	public void setOperatoreLogistico(String operatoreLogistico) {
		this.operatoreLogistico = operatoreLogistico;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getCodiceFiliale() {
		return codiceFiliale;
	}

	public void setCodiceFiliale(String codiceFiliale) {
		this.codiceFiliale = codiceFiliale;
	}

	public String getRoundCode() {
		return roundCode;
	}

	public void setRoundCode(String roundCode) {
		this.roundCode = roundCode;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((autista == null) ? 0 : autista.hashCode());
		result = prime
				* result
				+ ((operatoreLogistico == null) ? 0 : operatoreLogistico
						.hashCode());
		result = prime * result
				+ ((plateNumber == null) ? 0 : plateNumber.hashCode());
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
		Vehicle other = (Vehicle) obj;
		if (autista == null) {
			if (other.autista != null)
				return false;
		} else if (!autista.equals(other.autista))
			return false;
		if (operatoreLogistico == null) {
			if (other.operatoreLogistico != null)
				return false;
		} else if (!operatoreLogistico.equals(other.operatoreLogistico))
			return false;
		if (plateNumber == null) {
			if (other.plateNumber != null)
				return false;
		} else if (!plateNumber.equals(other.plateNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Vehicle [plateNumber=" + plateNumber + ", state=" + state
				+ ", attachments=" + attachments + ", serviceClass="
				+ serviceClass + ", autista=" + autista
				+ ", operatoreLogistico=" + operatoreLogistico + "]";
	}
}
