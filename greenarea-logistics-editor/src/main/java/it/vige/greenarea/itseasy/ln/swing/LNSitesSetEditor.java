/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.ln.swing;

import com.mxgraph.swing.mxGraphComponent;

import it.vige.greenarea.I18N.*;
import it.vige.greenarea.ln.model.LNSitesSet;

import java.util.EventObject;

/**
 * 
 * @author 00917308
 */
public class LNSitesSetEditor extends LNCellEditorDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6424126425953244001L;
	private final SitesSetPanel sitesSetPanel;
	private final SimpleNamePanel simpleNamePanel;

	public LNSitesSetEditor(mxGraphComponent gc) {
		super(gc);
		/* mio codice */
		simpleNamePanel = new SimpleNamePanel();
		sitesSetPanel = new SitesSetPanel(gc, this);
		super.getEditorTabPanel().addTab(I18N.getString("GeneralMenu"),
				simpleNamePanel);
		super.getEditorTabPanel().addTab(I18N.getString("Locations"),
				sitesSetPanel);
	}

	@Override
	public void startEditing(Object cell, EventObject trigger) {
		super.startEditing(cell, trigger);
		simpleNamePanel.startEditing(cell);
		sitesSetPanel.startEditing(cell);
	}

	@Override
	public void stopEditing(boolean cancel) {
		simpleNamePanel.stopEditing(cancel);
		sitesSetPanel.stopEditing(cancel);
		super.stopEditing(cancel);
	}

	@Override
	public void onDisplay(Object o) {
		simpleNamePanel.setChanged(false);
		if (o != null && o instanceof LNSitesSet) {
			simpleNamePanel.setCellName(((LNSitesSet) o).getName());
			simpleNamePanel.setDescription(((LNSitesSet) o).getDescription());
		} else {
			simpleNamePanel.setCellName("");
			simpleNamePanel.setDescription("");
		}
	}

	@Override
	public void onClose(Object o) {
	}

	@Override
	public void saveData(Object o) {
		LNSitesSet lnss = ((o == null || !(o instanceof LNSitesSet)) ? new LNSitesSet()
				: (LNSitesSet) o);
		lnss.setName(simpleNamePanel.getCellName());
		lnss.setDescription(simpleNamePanel.getDescription());
	}

	@Override
	public boolean isChanged() {
		return (sitesSetPanel.isChanged() || simpleNamePanel.isChanged());
	}
}
