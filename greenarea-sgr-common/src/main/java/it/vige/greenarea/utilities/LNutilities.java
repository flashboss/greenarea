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
package it.vige.greenarea.utilities;

import it.vige.greenarea.ln.model.LNNode;
import it.vige.greenarea.ln.model.LNSite;
import it.vige.greenarea.ln.model.LNSitesSet;
import it.vige.greenarea.ln.model.LogisticNetwork;

import java.util.HashMap;

import com.mxgraph.model.mxCell;

public class LNutilities {
	private static LogisticNetwork logisticNetwork;

	public static synchronized LogisticNetwork getLogisticNetwork() {
		if (logisticNetwork == null)
			logisticNetwork = new LogisticNetwork();
		return logisticNetwork;
	}

	public static void setLogisticNetwork(LogisticNetwork ln) {
		logisticNetwork = ln;
	}

	public static HashMap<String, LNNode> getLNNodesMap() {
		HashMap<String, LNNode> nodesMap = new HashMap<String, LNNode>();

		Object root = getLogisticNetwork().getDefaultParent();
		Object[] cells = getLogisticNetwork().getChildCells(root);
		for (Object o : cells) {
			if (o instanceof mxCell) {
				mxCell cell = (mxCell) o;
				if ((cell.getValue() instanceof LNNode)) {
					nodesMap.put(getLogisticNetwork()
							.convertValueToString(cell), (LNNode) cell
							.getValue());
				}
			}
		}
		return nodesMap;
	}

	public static HashMap<String, LNSite> getLNSitesMap() {
		HashMap<String, LNSite> nodesMap = new HashMap<String, LNSite>();
		Object root = getLogisticNetwork().getDefaultParent();
		Object[] cells = getLogisticNetwork().getChildCells(root);
		for (Object o : cells) {
			if (o instanceof mxCell) {
				mxCell cell = (mxCell) o;
				if (cell.getValue() instanceof LNSitesSet)
					for (LNSite lns : ((LNSitesSet) cell.getValue()).getSites())
						nodesMap.put(lns.getName(), lns);
				if (cell.getValue() instanceof LNSite) {
					nodesMap.put(getLogisticNetwork()
							.convertValueToString(cell), (LNSite) cell
							.getValue());
				}
			}
		}
		return nodesMap;
	}
}
