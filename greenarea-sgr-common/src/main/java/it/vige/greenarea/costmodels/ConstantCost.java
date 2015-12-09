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
package it.vige.greenarea.costmodels;

import org.w3c.dom.Element;

import com.mxgraph.view.mxCellState;

import it.vige.greenarea.dto.GeoLocation;

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
