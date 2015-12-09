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

import java.util.Collection;

import com.mxgraph.analysis.mxICostFunction;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

import it.vige.greenarea.dto.GeoLocationInterface;
import it.vige.greenarea.ln.model.LNCell;
import it.vige.greenarea.utilities.LNutilities;

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
				result += ((LNCell) o).getCostFunction().getCost(getCellState(c));
		}
		return result;
	}

	private static mxCellState getCellState(mxCell cell) {
		return logisticNetwork.getView().getState(cell);
	}
}
