/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.utilities;

import it.vige.greenarea.ln.model.LNNode;
import it.vige.greenarea.ln.model.LNSite;
import it.vige.greenarea.ln.model.LNSitesSet;
import it.vige.greenarea.ln.model.LogisticNetwork;

import java.util.HashMap;

import com.mxgraph.model.mxCell;

/**
 * 
 * @author 00917308
 */
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
