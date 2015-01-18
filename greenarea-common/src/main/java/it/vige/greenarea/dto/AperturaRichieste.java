package it.vige.greenarea.dto;

import static it.vige.greenarea.Utilities.uppercaseFirstLetters;

public enum AperturaRichieste {
	_2_GIORNI_PRIMA(48), _3_GIORNI_PRIMA(72), _6_GIORNI_PRIMA(144), _12_GIORNI_PRIMA(
			288);

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
