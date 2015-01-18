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
