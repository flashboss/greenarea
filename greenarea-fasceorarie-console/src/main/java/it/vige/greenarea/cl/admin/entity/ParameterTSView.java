/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.admin.entity;

import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.dto.Peso;
import it.vige.greenarea.dto.TipoParametro;

import java.io.Serializable;

public class ParameterTSView implements Serializable {

	private static final long serialVersionUID = 4514513713844712202L;

	private Integer idPTS;

	private TimeSlot ts;

	private ParameterGen parGen;

	private double maxVal;

	private double minVal;

	private Peso weight;

	private TipoParametro typePar;

	public ParameterTSView() {
	}

	public Integer getIdPTS() {
		return idPTS;
	}

	public void setIdPTS(Integer idPTS) {
		this.idPTS = idPTS;
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

	public double getMinVal() {
		return minVal;
	}

	public void setMinVal(double minVal) {
		this.minVal = minVal;
	}

	public TimeSlot getTs() {
		return ts;
	}

	public void setTs(TimeSlot ts) {
		this.ts = ts;
	}

	public ParameterGen getParGen() {
		return parGen;
	}

	public void setParGen(ParameterGen parGen) {
		this.parGen = parGen;
	}

	public String pesoParameterTS() {
		return weight.toString();
	}

}
