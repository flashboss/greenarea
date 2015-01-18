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
