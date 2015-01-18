/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.net;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author 00917308
 */
public abstract class Net implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5780618623312743941L;
	protected HashSet<Node> nodes = new HashSet<Node>();
	protected HashSet<Edge> edges = new HashSet<Edge>();

	public boolean addLeg(Node source, Edge link, Node target) {
		if (source == null || link == null || target == null
				|| link.getSource() != null || link.getTarget() != null)
			return false;
		nodes.add(source.addEdge(link));
		nodes.add(target.addEdge(link));
		edges.add(link.setSource(source).setTarget(target));
		return true;
	}

	public boolean isConnected() {

		int n = nodes.size();
		if (n == 0)
			return true;
		else {
			Node root = (Node) nodes.toArray()[0];
			if (findReachableNodes(root).size() == n)
				return true;
		}
		return false;
	}

	private HashSet<Node> findReachableNodes(Node node) {
		HashSet<Node> reached = new HashSet<Node>();
		ArrayList<Node> toBeExplored = new ArrayList<Node>();
		reached.add(node);
		toBeExplored.add(node);
		while (!toBeExplored.isEmpty()) {
			Node n = toBeExplored.remove(0);
			for (Edge e : n.getEdges()) {
				Node opposite = (e.getSource() == n ? e.getTarget() : e
						.getSource());
				if (reached.add(opposite))
					toBeExplored.add(opposite);
			}
		}
		return reached;
	}
}