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
import it.vige.greenarea.ln.model.LNSite;

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
