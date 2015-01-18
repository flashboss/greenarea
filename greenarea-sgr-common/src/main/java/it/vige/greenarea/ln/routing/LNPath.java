/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.ln.routing;

import it.vige.greenarea.costmodels.LNCost;
import it.vige.greenarea.dto.GeoLocationInterface;
import it.vige.greenarea.ln.model.LNCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import com.mxgraph.model.mxCell;

/**
 * 
 * @author 00917308
 */
public class LNPath implements Comparable<Object> {
	private GeoLocationInterface geoFrom;
	private GeoLocationInterface geoTo;
	private double cost = 0.;
	private Date timeStamp;
	private ArrayList<mxCell> path;

	public LNPath(GeoLocationInterface geoFrom, GeoLocationInterface geoTo,
			mxCell[] cells) {
		// fatto come getInstance per consentire la verifica della coerenza dei
		// parametri
		ArrayList<mxCell> path = new ArrayList<mxCell>();
		path.addAll(Arrays.asList(cells));
		LNCost cf = new LNCost(geoFrom, geoTo);
		this.cost = cf.getCost(path);
		this.geoFrom = geoFrom;
		this.geoTo = geoTo;
		this.timeStamp = GregorianCalendar.getInstance().getTime();
		this.path = path;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public double getCost() {
		return cost;
	}

	public GeoLocationInterface getGeoFrom() {
		return geoFrom;
	}

	public GeoLocationInterface getGeoTo() {
		return geoTo;
	}

	public mxCell[] getPath() {
		mxCell[] result = new mxCell[path.size()];
		return path.toArray(result);
	}

	public int size() {
		return path.size();
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof LNPath) {
			if (this.cost - ((LNPath) o).getCost() > 0)
				return 1;
			else if (this.cost - ((LNPath) o).getCost() < 0)
				return -1;
		}
		return 0;
	}

	@Override
	public String toString() {
		ArrayList<mxCell> cells = path;

		StringBuilder sb = new StringBuilder();
		int legCount = (cells.size() - 1) / 2;
		sb.append(" cost=").append(Double.toString(cost))
				.append("   leg count=").append(Integer.toString(legCount))
				.append("\n  path: ");
		if (cells.size() > 0) {
			int i;
			for (i = 0; i < cells.size() - 1;) {
				mxCell source = cells.get(i++);
				mxCell carrier = cells.get(i++);
				cells.get(i);
				sb.append("[").append(((LNCell) source.getValue()).toString())
						.append("]");
				sb.append("--")
						.append(((LNCell) carrier.getValue()).toString())
						.append("->");
			}
			sb.append("[")
					.append(((LNCell) cells.get(i).getValue()).toString())
					.append("]");
		}
		return sb.toString();
	}
}
