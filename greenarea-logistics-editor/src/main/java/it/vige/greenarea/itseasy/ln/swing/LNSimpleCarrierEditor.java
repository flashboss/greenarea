/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.ln.swing;

import com.mxgraph.swing.mxGraphComponent;

import it.vige.greenarea.I18N.*;
import it.vige.greenarea.ln.model.LNSimpleCarrier;

import java.util.EventObject;

/**
 * 
 * @author 00917308
 */
public class LNSimpleCarrierEditor extends LNEdgeEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3775043703832581112L;
	SimpleCarrierPanel simpleCarrierPanel;

	public LNSimpleCarrierEditor(mxGraphComponent gc) {
		super(gc, new LNSimpleCarrier());
		/* mio codice */
		simpleCarrierPanel = new SimpleCarrierPanel(gc, this);
		getEditorTabPane().addTab(I18N.getString("Route"), simpleCarrierPanel);
	}

	@Override
	public void startEditing(Object cell, EventObject trigger) {
		super.startEditing(cell, trigger);
		simpleCarrierPanel.startEditing(cell);
	}

	@Override
	public void stopEditing(boolean cancel) {
		super.stopEditing(cancel);
	}

}
