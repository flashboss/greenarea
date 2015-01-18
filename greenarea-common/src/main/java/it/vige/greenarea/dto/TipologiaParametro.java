package it.vige.greenarea.dto;

import static it.vige.greenarea.Utilities.uppercaseFirstLetters;

public enum TipologiaParametro {
	BENEFICIO, COSTO, CONTATORE, BOOLEANO;

	@Override
	public String toString() {
		return uppercaseFirstLetters(name());
	}
}
