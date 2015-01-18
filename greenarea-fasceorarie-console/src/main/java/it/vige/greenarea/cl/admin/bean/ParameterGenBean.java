/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.admin.bean;

import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.dto.TipologiaParametro;
import it.vige.greenarea.cl.admin.rest.TimeSlotRestClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.Dependent;

@ManagedBean
@Dependent
public class ParameterGenBean implements Serializable {
	private static final long serialVersionUID = 1L;

	// variabile di ParameterGen
	// id
	private Integer idPG;
	// nome
	private String namePG;
	// booleano, costo o beneficio
	private TipologiaParametro typePG;
	// unit?? di misura
	private String measureUnit;
	// abilitato/disabilitato
	public boolean useType;

	private boolean[] useTypeNew;

	private List<ParameterGen> listaParametriPA;

	// descrizione
	private String description;

	TimeSlotRestClient rc = new TimeSlotRestClient();

	private int allParameter;

	private List<ParameterGen> parametriAggiornati = new ArrayList<ParameterGen>();

	private List<Boolean> listaAllBoolUse = new ArrayList<Boolean>();

	ParameterGen parGenUp;

	public ParameterGenBean() {
	}

	// Tabella lista dei Parametri scelti dalla PA
	public List<ParameterGen> allParameterGen() throws Exception {
		if (listaParametriPA == null) {
			listaParametriPA = rc.findAllParameterGen();
			allParameter = listaParametriPA.size();
		}
		return listaParametriPA;
	}

	// Tabella lista Booleani
	public List<Boolean> allBoolUse() throws Exception {
		for (int i = 0; i < allParameter - 1; i++) {
			listaAllBoolUse.add(i, this.allParameterGen().get(i).isUseType());
		}
		return listaAllBoolUse;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getIdPG() {
		return idPG;
	}

	public void setIdPG(Integer idPG) {
		this.idPG = idPG;
	}

	public TipologiaParametro getTypePG() {
		return typePG;
	}

	public void setTypePG(TipologiaParametro typePG) {
		this.typePG = typePG;
	}

	public String getMeasureUnit() {
		return measureUnit;
	}

	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

	public String getNamePG() {
		return namePG;
	}

	public void setNamePG(String namePG) {
		this.namePG = namePG;
	}

	public TimeSlotRestClient getRc() {
		return rc;
	}

	public void setRc(TimeSlotRestClient rc) {
		this.rc = rc;
	}

	public boolean isUseType() {
		return useType;
	}

	public void setUseType(boolean useType) {
		this.useType = useType;
	}

	public int getAllParameter() {
		return allParameter;
	}

	public void setAllParameter(int allParameter) {
		this.allParameter = allParameter;
	}

	public List<ParameterGen> getParametriAggiornati() {
		return parametriAggiornati;
	}

	public void setParametriAggiornati(List<ParameterGen> parametriAggiornati) {
		this.parametriAggiornati = parametriAggiornati;
	}

	public boolean[] getUseTypeNew() {
		return useTypeNew;
	}

	public void setUseTypeNew(boolean[] useTypeNew) {
		this.useTypeNew = useTypeNew;
	}

}
