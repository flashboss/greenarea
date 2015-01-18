package it.vige.greenarea.dto;

import static it.vige.greenarea.Utilities.uppercaseFirstLetters;

public enum TipologiaClassifica {
	CLASSIFICA_STANDARD(1), PREMIA_RISPOSTA_GLOBALE(0), PREMIA_RISPOSTA_LOCALE(
			2);

	private int value;

	TipologiaClassifica(int value) {
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
