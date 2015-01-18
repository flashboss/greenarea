/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.net;

import java.io.Serializable;

/**
 * 
 * @author 00917308
 */
public abstract class Edge implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2975160431123197575L;
	protected boolean bidirectional = true;
	protected Node source = null;
	protected Node target = null;

	public boolean isBidirectional() {
		return bidirectional;
	}

	protected void setBidirectional(boolean bidirectional) {
		this.bidirectional = bidirectional;
	}

	public Node getSource() {
		return source;
	}

	protected Edge setSource(Node source) {
		this.source = source;
		return this;
	}

	public Node getTarget() {
		return target;
	}

	protected Edge setTarget(Node target) {
		this.target = target;
		return this;
	}

	public abstract double getCost();

}
