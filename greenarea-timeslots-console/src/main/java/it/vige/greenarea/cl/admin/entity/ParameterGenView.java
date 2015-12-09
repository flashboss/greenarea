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

import java.io.Serializable;

import it.vige.greenarea.dto.TipologiaParametro;

public class ParameterGenView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2053106630788529433L;
	private Integer idPG;
	private String namePG;
	// typePG: beneficio (0) costo (1) contatore (2) booleano (3)
	private TipologiaParametro typePG;
	private String measureUnit;
	// useType: disabilitato (0) abilitato (1)
	public boolean useType;
	private String description;

	public String settaggio;

	public ParameterGenView() {
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

	public boolean getUseType() {
		return useType;
	}

	public void setUseType(boolean useType) {
		this.useType = useType;
	}

	public String useTypeButtonActionName() {
		return (this.useType ? "ON" : "OFF");
	}

	public String tipologiaParGen() {
		return typePG.toString();
	}

}
