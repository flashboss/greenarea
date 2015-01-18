/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.ln.swing;

import com.mxgraph.swing.mxGraphComponent;

import it.vige.greenarea.I18N.*;
import it.vige.greenarea.ln.model.LNArea;

import java.util.EventObject;

/**
 * 
 * @author 00917308
 */
public class LNAreaEditor extends LNNodeEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4152958077479823104L;
	AreaPanel areaPanel;

	public LNAreaEditor(mxGraphComponent gc) {
		super(gc, new LNArea());
		/* mio codice */
		areaPanel = new AreaPanel(this);
		getEditorTabPane().addTab(I18N.getString("Location"), areaPanel);
	}

	@Override
	public void startEditing(Object cell, EventObject trigger) {
		super.startEditing(cell, trigger);
		areaPanel.startEditing(cell);
	}

	@Override
	public void stopEditing(boolean cancel) {
		super.stopEditing(cancel);
	}

}
