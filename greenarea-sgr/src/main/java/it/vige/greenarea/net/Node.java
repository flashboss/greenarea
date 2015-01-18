/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.net;

import java.io.Serializable;
import java.util.HashSet;

/**
 *
 * @author 00917308
 */
public abstract class Node implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2736860500882303908L;
	protected HashSet<Edge> edges = new HashSet<Edge>();
    
    public abstract double getCost();
    
    protected Edge[] getEdges(){
        Edge[] e = new Edge[ edges.size()];
        e = edges.toArray(e);
        return e;
    }
    
    protected Node addEdge( Edge e ){
      edges.add(e);
      return this;
    }    
    
    protected void removeEdge( Edge e ){
     edges.remove(e);
    }
}
