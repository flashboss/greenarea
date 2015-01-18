package it.vige.greenarea.dto;

public enum Tolleranza {
	_10_PER_CENTO(10), _20_PER_CENTO(20), _30_PER_CENTO(30), _40_PER_CENTO(40), _50_PER_CENTO(
			50), _60_PER_CENTO(60);

	private int value;

	Tolleranza(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value + "%";
	}
}
