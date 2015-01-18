/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.costmodels;

import it.vige.greenarea.dto.GeoLocation;

import java.io.Serializable;

import org.w3c.dom.Element;

import com.mxgraph.analysis.mxICostFunction;
import com.mxgraph.view.mxCellState;

/**
 *
 * @author 00917308
 */
public interface LNICostFunction extends mxICostFunction, Serializable {
    public double getCost( GeoLocation from, GeoLocation to, mxCellState state );  
    public  void toElement( Element elt );
    public  void loadElement( Element elt );
}
