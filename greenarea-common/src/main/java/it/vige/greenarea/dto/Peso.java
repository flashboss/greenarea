package it.vige.greenarea.dto;

import static it.vige.greenarea.Utilities.uppercaseFirstLetters;

public enum Peso {
	NESSUNO, BASSO, MEDIO, CRITICO;

	@Override
	public String toString() {
		return uppercaseFirstLetters(name());
	}
}
