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

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxCellState;

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.ln.model.LNNode;

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
			return getCost(((LNNode) source.getValue()).locate(), ((LNNode) target.getValue()).locate(), state);
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
