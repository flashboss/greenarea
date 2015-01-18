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

public enum Ripetizione {
	MAI("0000000-00000-000000000000"), TUTTI_I_GIORNI(
			"1111111-11111-111111111111"), FERIALI("1111100-11111-111111111111"), FESTIVI(
			"0000011-11111-111111111111");

	private String value;

	Ripetizione(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return uppercaseFirstLetters(name().replaceAll("_", " ").trim());
	}
}
