package it.vige.greenarea.dto;

import static it.vige.greenarea.Utilities.uppercaseFirstLetters;

public enum ChiusuraRichieste {
	_4_ORE_PRIMA(4), _12_ORE_PRIMA(12), _1_GIORNO_PRIMA(24), _2_GIORNI_PRIMA(48), _3_GIORNI_PRIMA(
			72), _6_GIORNI_PRIMA(144);

	private int value;

	ChiusuraRichieste(int value) {
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
