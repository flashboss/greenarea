package it.vige.greenarea.geofencing;

public class Linea {
	
	private Punto from;
	private Punto to;

	public Linea(Punto from, Punto to) {
		this.from = from;
		this.to = to;
	}

	public Punto getFrom() {
		return from;
	}

	public Punto getTo() {
		return to;
	}
}
