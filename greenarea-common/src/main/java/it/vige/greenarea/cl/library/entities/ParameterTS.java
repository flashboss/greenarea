/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import static javax.persistence.GenerationType.AUTO;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;
import it.vige.greenarea.dto.Peso;
import it.vige.greenarea.dto.TipoParametro;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
public class ParameterTS implements Serializable {

	private static final long serialVersionUID = -2699940526493789020L;
	@Id
	@GeneratedValue(strategy = AUTO)
	@XmlElement
	private Integer id;
	@XmlElement
	@ManyToOne
	private TimeSlot ts;
	@XmlElement
	@ManyToOne
	private ParameterGen parGen;
	@XmlElement
	private double maxVal;
	@XmlElement
	private double minVal;
	@XmlElement
	private Peso weight;
	@XmlElement
	private TipoParametro typePar; // definisce come uso il parametro ts costo
									// beneficio

	public ParameterTS() {
	}

	public ParameterTS(Integer id) {
		this();
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the idTS
	 */
	public TimeSlot getTs() {
		return ts;
	}

	/**
	 * @param idTS
	 *            the idTS to set
	 */
	public void setTs(TimeSlot ts) {
		this.ts = ts;
	}

	/**
	 * @return the maxValue
	 */
	public double getMaxValue() {
		return maxVal;
	}

	/**
	 * @param maxValue
	 *            the maxValue to set
	 */
	public void setMaxValue(double maxValue) {
		this.maxVal = maxValue;
	}

	/**
	 * @return the minValue
	 */
	public double getMinValue() {
		return minVal;
	}

	/**
	 * @param minValue
	 *            the minValue to set
	 */
	public void setMinValue(double minValue) {
		this.minVal = minValue;
	}

	/**
	 * @return the weight
	 */
	public Peso getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(Peso weight) {
		this.weight = weight;
	}

	/**
	 * @return the typePar
	 */
	public TipoParametro getTypePar() {
		return typePar;
	}

	/**
	 * @param typePar
	 *            the typePar to set
	 */
	public void setTypePar(TipoParametro typePar) {
		this.typePar = typePar;
	}

	/**
	 * @return the idParGen
	 */
	public ParameterGen getParGen() {
		return parGen;
	}

	/**
	 * @param idParGen
	 *            the idParGen to set
	 */
	public void setParGen(ParameterGen parGen) {
		this.parGen = parGen;
	}

}
