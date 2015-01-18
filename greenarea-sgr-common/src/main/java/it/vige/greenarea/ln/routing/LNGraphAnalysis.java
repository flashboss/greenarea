/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.ln.routing;

import it.vige.greenarea.costmodels.LNCost;
import it.vige.greenarea.dto.GeoLocationInterface;
import it.vige.greenarea.ln.model.LNNode;

import java.util.ArrayList;
import java.util.Collections;

import com.mxgraph.analysis.mxGraphAnalysis;
import com.mxgraph.analysis.mxICostFunction;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

/**
 * 
 * @author 00917308
 */
public class LNGraphAnalysis extends mxGraphAnalysis {
	private static final boolean directed = false;
	private static final int steps = 100;

	public ArrayList<LNPath> getLogisticPaths(mxGraph graph,
			GeoLocationInterface geoFrom, GeoLocationInterface geoTo) {
		// LG contengono gli insiemi di LNNode della Rete Logistica che che
		// "contengono" geoFrom e GeoTo
		// la ricerca del cammino ottimo si esegue sulla matrice toSet X FromSet
		ArrayList<mxCell> toSet = getMatchingCells(graph, geoTo);
		ArrayList<mxCell> fromSet = getMatchingCells(graph, geoFrom);

		// LG Contiene l'insieme dei percorsi logistici risultanti alla fine
		// dell'elaborazione
		// ordinati per costo crescente
		ArrayList<LNPath> resultPaths = new ArrayList<LNPath>();
		mxCell resultPath[];
		// nella prima e nell'ultima leg, la funzione di costo viene applicata
		// anche (rispettivamente) a geoFrom e a geoTo
		mxICostFunction cf = new LNCost(geoFrom, geoTo);
		Object[] resultPathO;

		for (Object from : fromSet) {
			for (Object to : toSet) {
				resultPathO = new mxCell[0];
				// caso del loop sullo stesso nodo
				if (from.equals(to)) {
					Object[] e = graph.getEdgesBetween(from, to, false);
					/*
					 * getEdgesBetween da effettivamente gli edge in mezzo anche
					 * se source e destination coincidono. La GetOpposites non
					 * contempla la possibilit?????? che source e destination siano
					 * coincidenti
					 */
					for (int i = 0; i < e.length; i++) {
						// N.B. tutte le edge in e[] sono loop
						resultPath = new mxCell[3];
						resultPath[0] = (mxCell) from;
						resultPath[1] = (mxCell) e[i];
						resultPath[2] = (mxCell) to;
						resultPaths.add(new LNPath(geoFrom, geoTo, resultPath));
					}
				} else {
					resultPathO = super.getShortestPath(graph, from, to, cf,
							steps, directed);
					resultPath = new mxCell[resultPathO.length];
					for (int i = 0; i < resultPathO.length; i++) {
						resultPath[i] = (mxCell) resultPathO[i];
					}
					// elimino il caso dei path di lunghezza 0
					if (resultPath.length > 0)
						resultPaths.add(new LNPath(geoFrom, geoTo, resultPath));
				}
			}
		}
		/*
		 * Ordino i path in base al costo (dal minore al maggiore)
		 */
		Collections.sort(resultPaths);
		return resultPaths;
	}

	@Override
	public Object[] getShortestPath(mxGraph graph, Object from, Object to,
			mxICostFunction cf, int steps, boolean directed) {
		if (cf == null)
			cf = new LNCost(null, null);
		return super.getShortestPath(graph, from, to, cf, steps, directed);

	}

	private ArrayList<mxCell> getMatchingCells(mxGraph graph,
			GeoLocationInterface geoSite) {
		Object node = null;
		ArrayList<mxCell> nodes = new ArrayList<mxCell>();
		Object root = graph.getDefaultParent();
		Object[] cells = graph.getChildCells(root);
		for (int i = 0; i < cells.length; i++) {
			node = ((mxCell) cells[i]).getValue();
			if (node instanceof LNNode && ((LNNode) node).includes(geoSite)) {
				nodes.add((mxCell) cells[i]);
			}
		}
		return nodes;
	}

}
