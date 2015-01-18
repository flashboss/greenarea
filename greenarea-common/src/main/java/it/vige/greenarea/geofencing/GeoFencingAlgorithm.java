/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.greenarea.geofencing;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;

public class GeoFencingAlgorithm {

	private double x;
	private double y;
	private Poligono poligono;

	private Logger logger = getLogger(getClass());

	public GeoFencingAlgorithm(double x, double y, Poligono poligono) {
		this.x = x;
		this.y = y;
		this.poligono = poligono;
	}

	public boolean isInGA() {
		List<Linea> lines = calculateLines(poligono);
		List<Linea> intersectionLines = filterIntersectingLines(lines, y);
		List<Punto> intersectionPoints = calculateIntersectionPoints(
				intersectionLines, y);
		sortPointsByX(intersectionPoints);
		return calculateInside(intersectionPoints, x);
	}

	private List<Linea> calculateLines(Poligono polygon) {
		List<Linea> results = new LinkedList<Linea>();

		// get the polygon points
		Punto[] points = polygon.getPoints();

		// form lines by connecting the points
		Punto lastPoint = null;
		for (Punto point : points) {
			if (lastPoint != null) {
				results.add(new Linea(lastPoint, point));
			}
			lastPoint = point;
		}

		// close the polygon by connecting the last point
		// to the first point
		results.add(new Linea(lastPoint, points[0]));

		return results;
	}

	private List<Linea> filterIntersectingLines(List<Linea> lines, double y) {
		List<Linea> results = new LinkedList<Linea>();
		for (Linea line : lines) {
			if (isLineIntersectingAtY(line, y)) {
				results.add(line);
			}
		}
		return results;
	}

	private boolean isLineIntersectingAtY(Linea line, double y) {
		double minY = Math.min(line.getFrom().getY(), line.getTo().getY());
		double maxY = Math.max(line.getFrom().getY(), line.getTo().getY());
		return y > minY && y <= maxY;
	}

	private List<Punto> calculateIntersectionPoints(List<Linea> lines, double y) {
		List<Punto> results = new LinkedList<Punto>();
		for (Linea line : lines) {
			double x = calculateLineXAtY(line, y);
			results.add(new Punto(x, y));
		}
		return results;
	}

	private double calculateLineXAtY(Linea line, double y) {
		Punto from = line.getFrom();
		double slope = calculateSlope(line);
		return from.getX() + (y - from.getY()) / slope;
	}

	private double calculateSlope(Linea line) {
		Punto from = line.getFrom();
		Punto to = line.getTo();
		return (to.getY() - from.getY()) / (to.getX() - from.getX());
	}

	private void sortPointsByX(List<Punto> points) {
		Collections.sort(points, new Comparator<Punto>() {
			public int compare(Punto p1, Punto p2) {
				return Double.compare(p1.getX(), p2.getX());
			}
		});
	}

	boolean calculateInside(List<Punto> sortedPoints, double x) {
		boolean inside = false;
		for (Punto point : sortedPoints) {
			if (x < point.getX()) {
				break;
			}
			inside = !inside;
		}
		logger.debug("inside: " + x + " - " + inside);
		return inside;
	}

}
