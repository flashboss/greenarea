/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgrl.webservices;

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.ln.model.LNCell;
import it.vige.greenarea.ln.routing.LNPath;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.mxgraph.model.mxCell;

/**
 * 
 * @author 00917308
 */
public class SgrlLogisticPath implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4323239243648836440L;
	private GeoLocation geoFrom;
	private GeoLocation geoTo;
	private double cost = 0.;
	private Date timeStamp;
	private ArrayList<String> path;

	private SgrlLogisticPath() {
	}

	public static SgrlLogisticPath code(LNPath path) {
		// fatto come getInstance per consentire la verifica della coerenza dei
		// parametri
		SgrlLogisticPath result = new SgrlLogisticPath();
		result.setGeoFrom(new GeoLocation(path.getGeoFrom()));
		result.setGeoTo(new GeoLocation(path.getGeoTo()));
		result.setPath(new ArrayList<String>());
		for (mxCell cell : path.getPath()) {
			Object o = cell.getValue();
			if (o instanceof LNCell)
				result.getPath().add(((LNCell) o).getName());
		}
		result.setCost(path.getCost());
		result.setTimeStamp(path.getTimeStamp());
		return result;
	}

	public GeoLocation getGeoFrom() {
		return geoFrom;
	}

	public void setGeoFrom(GeoLocation geoFrom) {
		this.geoFrom = geoFrom;
	}

	public GeoLocation getGeoTo() {
		return geoTo;
	}

	public void setGeoTo(GeoLocation geoTo) {
		this.geoTo = geoTo;
	}

	public ArrayList<String> getPath() {
		return path;
	}

	public void setPath(ArrayList<String> path) {
		this.path = path;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int size() {
		return path.size();
	}
}
