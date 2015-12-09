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

import static org.slf4j.LoggerFactory.getLogger;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxICellEditor;

import it.vige.greenarea.ln.model.LNCell;

public class LNCellEditor implements mxICellEditor {

	private Logger logger = getLogger(getClass());

	private mxCell editingCell;
	private mxICellEditor editor;
	private mxGraphComponent graphComponent;
	private Map<String, mxICellEditor> editorMap;

	public LNCellEditor(mxGraphComponent gc) {
		editorMap = new HashMap<String, mxICellEditor>();
		graphComponent = gc;
	}

	@Override
	public Object getEditingCell() {
		return null;
	}

	@Override
	public void startEditing(Object cell, EventObject trigger) {
		editingCell = (mxCell) cell;
		if (cell instanceof mxCell) {
			Object value = editingCell.getValue();
			String LNcellType = ((LNCell) value).getClass().getSimpleName();
			if (LNcellType != null) {
				editor = editorMap.get(LNcellType);
				if (editor == null) {
					try {

						// Class c =
						// LNCellEditor.class.getClassLoader().loadClass(LNcellType.concat("Editor"));
						LNcellType = "it.vige.greenarea.itseasy.ln.swing.".concat(LNcellType);
						Class c = Class.forName(LNcellType.concat("Editor"));

						editor = (mxICellEditor) c.getConstructor(new Class[] { mxGraphComponent.class })
								.newInstance(graphComponent);
						editorMap.put(LNcellType, editor);
					} catch (Exception ex) {
						logger.error("logistic editor", ex);
					}
				}

				editor.startEditing(cell, trigger);

			}
		}
	}

	@Override
	public void stopEditing(boolean cancel) {
		if (editor != null) {
			// editor.stopEditing(cancel);
			graphComponent.redraw(graphComponent.getGraph().getView().getState(editingCell));
			editingCell = null;
		}
	}

}
