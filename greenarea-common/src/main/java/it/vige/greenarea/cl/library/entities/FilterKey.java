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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FilterKey implements Serializable {

	private static final long serialVersionUID = 7639962823474130836L;
	@Column
	private String roundCode;
	@Column
	private String operatoreLogistico;

	public FilterKey() {

	}

	public FilterKey(String roundCode, String operatoreLogistico) {
		this.roundCode = roundCode;
		this.operatoreLogistico = operatoreLogistico;
	}

	public String getRoundCode() {
		return roundCode;
	}

	public void setRoundCode(String roundCode) {
		this.roundCode = roundCode;
	}

	public String getOperatoreLogistico() {
		return operatoreLogistico;
	}

	public void setOperatoreLogistico(String operatoreLogistico) {
		this.operatoreLogistico = operatoreLogistico;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operatoreLogistico == null) ? 0 : operatoreLogistico.hashCode());
		result = prime * result + ((roundCode == null) ? 0 : roundCode.hashCode());
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
		FilterKey other = (FilterKey) obj;
		if (operatoreLogistico == null) {
			if (other.operatoreLogistico != null)
				return false;
		} else if (!operatoreLogistico.equals(other.operatoreLogistico))
			return false;
		if (roundCode == null) {
			if (other.roundCode != null)
				return false;
		} else if (!roundCode.equals(other.roundCode))
			return false;
		return true;
	}
}