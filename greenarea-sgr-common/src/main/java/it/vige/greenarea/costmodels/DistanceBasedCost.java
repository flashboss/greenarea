/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.costmodels;

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.ln.model.LNNode;

import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxCellState;

/**
 *
 * @author 00917308
 */
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * ++++++++++++++
 * 
 * 
 */
public class DistanceBasedCost implements LNICostFunction {
	/**
		 * 
		 */
	private static final long serialVersionUID = 4383425679624248342L;
	public static final String UNIT_COST = "UNIT_COST";
	private double unitCost = 0.;

	@Override
	public double getCost(mxCellState state) {
		Object cell = state.getCell();
		if (cell instanceof mxCell) {
			mxCell source = (mxCell) ((mxCell) cell).getSource();
			mxCell target = (mxCell) ((mxCell) cell).getTarget();
			return getCost(((LNNode) source.getValue()).locate(),
					((LNNode) target.getValue()).locate(), state);
		} else
			return 0.;
	}

	@Override
	public double getCost(GeoLocation from, GeoLocation to, mxCellState state) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void toElement(Element elt) {
		elt.setAttribute(UNIT_COST, Double.toString(unitCost));
	}

	@Override
	public void loadElement(Element elt) {
		unitCost = Double.parseDouble(elt.getAttribute(UNIT_COST));
	}
}
