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
package it.vige.greenarea.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RichiestaAccesso implements Serializable {

	private static final long serialVersionUID = -45137880937658994L;
	private List<String> gas;
	private Date dataInizio;
	private Date dataFine;
	private List<String> operatoriLogistici;

	public List<String> getGas() {
		return gas;
	}

	public void setGas(List<String> gas) {
		this.gas = gas;
	}

	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}

	public List<String> getOperatoriLogistici() {
		return operatoriLogistici;
	}

	public void setOperatoriLogistici(List<String> operatoriLogistici) {
		this.operatoriLogistici = operatoriLogistici;
	}
}
