package it.vige.greenarea.geofencing;

public class Poligono {

	private Punto[] points;

	public Poligono() {
	}

	public Poligono(Punto[] points) {
		this.points = points;
	}

	public Punto[] getPoints() {
		return points;
	}
}
