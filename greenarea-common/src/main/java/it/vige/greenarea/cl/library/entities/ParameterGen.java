/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import static javax.persistence.GenerationType.AUTO;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;
import it.vige.greenarea.dto.TipologiaParametro;

import java.io.Serializable;

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
public class ParameterGen implements Serializable {

	private static final long serialVersionUID = -4333054188736156280L;

	@Id
	@GeneratedValue(strategy = AUTO)
	@XmlElement
	private Integer idPG;

	@XmlElement
	private String namePG;
	@XmlElement
	private TipologiaParametro typePG; // definisce come uso il parameterGEn
	// costo...beneficio etc etc
	@XmlElement
	private String measureUnit;
	@XmlElement
	private boolean useType;
	@XmlElement
	private String description;

	public ParameterGen() {

	}

	public ParameterGen(int idPG) {
		this.idPG = idPG;
	}

	public Integer getId() {
		return idPG;
	}

	public void setId(Integer id) {
		this.idPG = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idPG != null ? idPG.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof ParameterGen)) {
			return false;
		}
		ParameterGen other = (ParameterGen) object;
		if ((this.idPG == null && other.idPG != null)
				|| (this.idPG != null && !this.idPG.equals(other.idPG))) {
			return false;
		}
		return true;
	}

	public String getNamePG() {
		return namePG;
	}

	public void setNamePG(String namePG) {
		this.namePG = namePG;
	}

	public boolean isUseType() {
		return useType;
	}

	public void setUseType(boolean useType) {
		this.useType = useType;
	}

	@Override
	public String toString() {
		return "org.entities.ParameterGen[ id=" + idPG + " ]";
	}

	/**
	 * @return the idTypePG
	 */
	public TipologiaParametro getTypePG() {
		return typePG;
	}

	/**
	 * @param idTypePG
	 *            the idTypePG to set
	 */
	public void setTypePG(TipologiaParametro typePG) {
		this.typePG = typePG;
	}

	/**
	 * @return the measureUnit
	 */
	public String getMeasureUnit() {
		return measureUnit;
	}

	/**
	 * @param measureUnit
	 *            the measureUnit to set
	 */
	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
