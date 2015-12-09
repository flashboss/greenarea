/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.greenarea.itseasy.ln.swing;

import java.util.EventObject;

import com.mxgraph.swing.mxGraphComponent;

import it.vige.greenarea.I18N.I18N;
import it.vige.greenarea.ln.model.LNSitesSet;

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
		super.getEditorTabPanel().addTab(I18N.getString("GeneralMenu"), simpleNamePanel);
		super.getEditorTabPanel().addTab(I18N.getString("Locations"), sitesSetPanel);
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
		LNSitesSet lnss = ((o == null || !(o instanceof LNSitesSet)) ? new LNSitesSet() : (LNSitesSet) o);
		lnss.setName(simpleNamePanel.getCellName());
		lnss.setDescription(simpleNamePanel.getDescription());
	}

	@Override
	public boolean isChanged() {
		return (sitesSetPanel.isChanged() || simpleNamePanel.isChanged());
	}
}
