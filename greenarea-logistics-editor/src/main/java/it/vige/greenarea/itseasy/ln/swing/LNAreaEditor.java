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

import com.mxgraph.swing.mxGraphComponent;

import it.vige.greenarea.I18N.*;
import it.vige.greenarea.ln.model.LNArea;

import java.util.EventObject;

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
