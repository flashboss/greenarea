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
package it.vige.greenarea.itseasy.swing.editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraphView;

import it.vige.greenarea.itseasy.swing.editor.EditorActions.HistoryAction;
import it.vige.greenarea.itseasy.swing.editor.EditorActions.NewAction;
import it.vige.greenarea.itseasy.swing.editor.EditorActions.OpenAction;
import it.vige.greenarea.itseasy.swing.editor.EditorActions.PrintAction;
import it.vige.greenarea.itseasy.swing.editor.EditorActions.SaveAction;

public class SchemaEditorToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3979320704834605323L;

	/**
	 * 
	 * @param frame
	 * @param orientation
	 */
	private boolean ignoreZoomChange = false;

	/**
	 * 
	 */
	public SchemaEditorToolBar(final BasicGraphEditor editor, int orientation) {
		super(orientation);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), getBorder()));
		setFloatable(false);

		add(editor.bind("New", new NewAction(), "/it/vige.greenarea/itseasy/swing/images/new.gif"));
		add(editor.bind("Open", new OpenAction(), "/it/vige.greenarea/itseasy/swing/images/open.gif"));
		add(editor.bind("Save", new SaveAction(false), "/it/vige.greenarea/itseasy/swing/images/save.gif"));

		addSeparator();

		add(editor.bind("Print", new PrintAction(), "/it/vige.greenarea/itseasy/swing/images/print.gif"));

		addSeparator();

		add(editor.bind("Cut", TransferHandler.getCutAction(), "/it/vige.greenarea/itseasy/swing/images/cut.gif"));
		add(editor.bind("Copy", TransferHandler.getCopyAction(), "/it/vige.greenarea/itseasy/swing/images/copy.gif"));
		add(editor.bind("Paste", TransferHandler.getPasteAction(),
				"/it/vige.greenarea/itseasy/swing/images/paste.gif"));

		addSeparator();

		add(editor.bind("Delete", mxGraphActions.getDeleteAction(),
				"/it/vige.greenarea/itseasy/swing/images/delete.gif"));

		addSeparator();

		add(editor.bind("Undo", new HistoryAction(true), "/it/vige.greenarea/itseasy/swing/images/undo.gif"));
		add(editor.bind("Redo", new HistoryAction(false), "/it/vige.greenarea/itseasy/swing/images/redo.gif"));

		addSeparator();

		final mxGraphView view = editor.getGraphComponent().getGraph().getView();
		final JComboBox zoomCombo = new JComboBox(new Object[] { "400%", "200%", "150%", "100%", "75%", "50%",
				mxResources.get("page"), mxResources.get("width"), mxResources.get("actualSize") });
		zoomCombo.setEditable(true);
		zoomCombo.setMinimumSize(new Dimension(75, 0));
		zoomCombo.setPreferredSize(new Dimension(75, 0));
		zoomCombo.setMaximumSize(new Dimension(75, 100));
		zoomCombo.setMaximumRowCount(9);
		add(zoomCombo);

		// Sets the zoom in the zoom combo the current value
		mxIEventListener scaleTracker = new mxIEventListener() {
			/**
			 * 
			 */
			public void invoke(Object sender, mxEventObject evt) {
				ignoreZoomChange = true;

				try {
					zoomCombo.setSelectedItem((int) Math.round(100 * view.getScale()) + "%");
				} finally {
					ignoreZoomChange = false;
				}
			}
		};

		// Installs the scale tracker to update the value in the combo box
		// if the zoom is changed from outside the combo box
		view.getGraph().getView().addListener(mxEvent.SCALE, scaleTracker);
		view.getGraph().getView().addListener(mxEvent.SCALE_AND_TRANSLATE, scaleTracker);

		// Invokes once to sync with the actual zoom value
		scaleTracker.invoke(null, null);

		zoomCombo.addActionListener(new ActionListener() {
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e) {
				mxGraphComponent graphComponent = editor.getGraphComponent();

				// Zoomcombo is changed when the scale is changed in the diagram
				// but the change is ignored here
				if (!ignoreZoomChange) {
					String zoom = zoomCombo.getSelectedItem().toString();

					if (zoom.equals(mxResources.get("page"))) {
						graphComponent.setPageVisible(true);
						graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_PAGE);
					} else if (zoom.equals(mxResources.get("width"))) {
						graphComponent.setPageVisible(true);
						graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);
					} else if (zoom.equals(mxResources.get("actualSize"))) {
						graphComponent.zoomActual();
					} else {
						try {
							zoom = zoom.replace("%", "");
							double scale = Math.min(16, Math.max(0.01, Double.parseDouble(zoom) / 100));
							graphComponent.zoomTo(scale, graphComponent.isCenterZoom());
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(editor, ex.getMessage());
						}
					}
				}
			}
		});
	}
}
