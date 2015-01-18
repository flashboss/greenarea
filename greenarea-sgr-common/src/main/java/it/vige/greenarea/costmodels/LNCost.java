/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.costmodels;

import it.vige.greenarea.dto.GeoLocationInterface;
import it.vige.greenarea.ln.model.LNCell;
import it.vige.greenarea.utilities.LNutilities;

import java.util.Collection;

import com.mxgraph.analysis.mxICostFunction;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

/**
 * 
 * @author 00917308
 */
public class LNCost implements mxICostFunction {

	private static mxGraph logisticNetwork = LNutilities.getLogisticNetwork();

	public LNCost(GeoLocationInterface geoFrom, GeoLocationInterface geoTo) {
	}

	@Override
	public double getCost(mxCellState state) {
		Object o = state.getCell();
		double result = 0.;
		if (o instanceof mxCell) {
			o = ((mxCell) o).getValue();
			if (o instanceof LNCell) {
				o = ((LNCell) o).getCostFunction();
				result = (o != null ? ((LNICostFunction) o).getCost(state) : 0.);
			}
		}
		return result;
	}

	public double getCost(Collection<mxCell> path) {
		double result = 0.;
		Object o;
		for (mxCell c : path) {
			o = c.getValue();
			if (o instanceof LNCell)
				result += ((LNCell) o).getCostFunction().getCost(
						getCellState(c));
		}
		return result;
	}

	private static mxCellState getCellState(mxCell cell) {
		return logisticNetwork.getView().getState(cell);
	}
}
