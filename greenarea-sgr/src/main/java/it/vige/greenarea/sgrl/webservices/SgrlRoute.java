/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgrl.webservices;

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.ln.model.LNEdge;
import it.vige.greenarea.ln.model.LNNode;
import it.vige.greenarea.ln.model.LNSite;
import it.vige.greenarea.ln.routing.LNPath;
import it.vige.greenarea.sgrl.webservices.SGRLServiceException.SGRLServiceError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.mxgraph.model.mxCell;

/**
 * 
 * @author 00917308
 */
public class SgrlRoute implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4071670570652028824L;
	private double cost = 0.;
	private Date timeStamp;
	private ArrayList<SgrlNode> route = new ArrayList<SgrlNode>();

	private SgrlRoute() {
	}

	public static SgrlRoute code(LNPath path) throws SGRLServiceException {
		// fatto come getInstance per consentire la verifica della coerenza dei
		// parametri
		SgrlRoute result = new SgrlRoute();
		SGRLExchangeStop xs;
		SgrlNode c;
		Object o;
		mxCell cell;

		if (path == null || path.size() < 3 || path.size() % 2 == 0)
			throw new SGRLServiceException(SGRLServiceError.SYSTEM_ERROR);

		cell = path.getPath()[0];
		o = cell.getValue();
		if (!(o instanceof LNNode))
			throw new SGRLServiceException(SGRLServiceError.SYSTEM_ERROR);
		xs = new SGRLExchangeStop();
		xs.setName(((LNNode) o).getName());
		xs.setLocation(new GeoLocation(path.getGeoFrom()));
		result.getRoute().add(xs);

		for (int i = 1; i < path.getPath().length; i += 2) {
			o = path.getPath()[i].getValue();
			if (!(o instanceof LNEdge))
				throw new SGRLServiceException(SGRLServiceError.SYSTEM_ERROR);
			c = new SgrlNode();
			c.setName(((LNEdge) o).getName());
			c.setNameDetails(((LNEdge) o).getDescription());
			result.getRoute().add(c);
			o = path.getPath()[i + 1].getValue();
			if (!(o instanceof LNNode) || !(o instanceof LNSite)
					&& !(path.getPath().length == i + 2))
				throw new SGRLServiceException(SGRLServiceError.SYSTEM_ERROR);
			xs = new SGRLExchangeStop();
			xs.setName(((LNNode) o).getName());
			xs.setNameDetails(((LNNode) o).getDescription());
			xs.setLocation(new GeoLocation(
					(path.getPath().length == i + 2 ? path.getGeoTo()
							: (LNSite) o)));
			result.getRoute().add(xs);
		}
		result.setCost(path.getCost());
		result.setTimeStamp(path.getTimeStamp());
		return result;
	}

	public ArrayList<SgrlNode> getRoute() {
		return route;
	}

	public void setRoute(ArrayList<SgrlNode> route) {
		this.route = route;
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
		return route.size();
	}
}
