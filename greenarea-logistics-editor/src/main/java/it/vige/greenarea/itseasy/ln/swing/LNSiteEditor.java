/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.ln.swing;

import it.vige.greenarea.I18N.I18N;
import it.vige.greenarea.ln.model.LNSite;

import java.util.EventObject;

import com.mxgraph.swing.mxGraphComponent;

/**
 * 
 * @author 00917308
 */
public class LNSiteEditor extends LNNodeEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1678030363956144526L;
	FullAddressLocationPanel locationPanel;

	public LNSiteEditor(mxGraphComponent gc) {
		super(gc, new LNSite());
		/* mio codice */
		locationPanel = new FullAddressLocationPanel(gc, this);

		locationPanel.addListener(new LNeditorEventHandlerInterface() {
			@Override
			public void handleEvent(LNeditorEvent evt) {
				userObjectChanged = true;
			}
		});
		getEditorTabPane().addTab(I18N.getString("Location"), locationPanel);
	}

	@Override
	public void startEditing(Object cell, EventObject trigger) {
		super.startEditing(cell, trigger);
		locationPanel.startEditing(cell);
		userObjectChanged = false;
	}

	@Override
	public void stopEditing(boolean cancel) {
		super.stopEditing(cancel);
	}
}
