/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.costmodels;

import it.vige.greenarea.dto.GeoLocation;

import org.w3c.dom.Element;

import com.mxgraph.view.mxCellState;

/**
 * 
 * @author 00917308
 */
public class ConstantCost implements LNICostFunction {
	
	private static final long serialVersionUID = -7931139019457314774L;
	public static final String COST_K = "COST_K";
	private double kcost = 0.;

	public double getKcost() {
		return kcost;
	}

	public void setKcost(double kcost) {
		this.kcost = kcost;
	}

	@Override
	public double getCost(mxCellState state) {
		return kcost;
	}

	@Override
	public double getCost(GeoLocation from, GeoLocation to, mxCellState state) {
		return kcost;
	}

	@Override
	public void toElement(Element elt) {
		elt.setAttribute(COST_K, Double.toString(kcost));
	}

	@Override
	public void loadElement(Element elt) {
		try {
			kcost = Double.parseDouble(elt.getAttribute(COST_K));
		} catch (Exception ex) {
			kcost = 0.;
		}
	}
}
