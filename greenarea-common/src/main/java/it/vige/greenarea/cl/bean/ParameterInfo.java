/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.bean;

import it.vige.greenarea.dto.Peso;
import it.vige.greenarea.dto.TipoParametro;
import it.vige.greenarea.dto.TipologiaParametro;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ParameterInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5374696234559247868L;

	@XmlElement
	private int idPg;
	@XmlElement
	private String namePG;
	@XmlElement
	private TipologiaParametro typePG;
	@XmlElement
	private String measureUnit;
	@XmlElement
	private boolean useType;
	@XmlElement
	private String description;
	@XmlElement
	private double maxVal;
	@XmlElement
	private double minVal;
	@XmlElement
	private Peso weight;
	@XmlElement
	private TipoParametro typePar;

	public int getIdPg() {
		return idPg;
	}

	public void setIdPg(int idPg) {
		this.idPg = idPg;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TipologiaParametro getTypePG() {
		return typePG;
	}

	public void setTypePG(TipologiaParametro typePG) {
		this.typePG = typePG;
	}

	public TipoParametro getTypePar() {
		return typePar;
	}

	public void setTypePar(TipoParametro typePar) {
		this.typePar = typePar;
	}

	public Peso getWeight() {
		return weight;
	}

	public void setWeight(Peso weight) {
		this.weight = weight;
	}

	public double getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(double maxVal) {
		this.maxVal = maxVal;
	}

	public String getMeasureUnit() {
		return measureUnit;
	}

	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

	public double getMinVal() {
		return minVal;
	}

	public void setMinVal(double minVal) {
		this.minVal = minVal;
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

}
