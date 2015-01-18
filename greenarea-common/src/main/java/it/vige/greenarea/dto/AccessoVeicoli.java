package it.vige.greenarea.dto;

import static it.vige.greenarea.Utilities.uppercaseFirstLetters;

public enum AccessoVeicoli {
	GRATUITO, PREZZO_FISSO, PREZZO_VARIABILE, NEGATO;

	@Override
	public String toString() {
		return uppercaseFirstLetters(name().replaceAll("_", " ").trim());
	}
}
