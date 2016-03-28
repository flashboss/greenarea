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

import static it.vige.greenarea.Utilities.uppercaseFirstLetters;

public enum AperturaRichieste {
	_2_GIORNI_PRIMA(48), _3_GIORNI_PRIMA(72), _6_GIORNI_PRIMA(144), _12_GIORNI_PRIMA(288);

	private int value;

	AperturaRichieste(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return uppercaseFirstLetters(name().replaceAll("_", " ").trim());
	}
}
