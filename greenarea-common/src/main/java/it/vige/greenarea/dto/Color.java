package it.vige.greenarea.dto;

import static it.vige.greenarea.Utilities.uppercaseFirstLetters;

public enum Color {
	GIALLO, ROSSO, VERDE;

	@Override
	public String toString() {
		return uppercaseFirstLetters(name());
	}
}
