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

import java.awt.Color;
import java.util.EventObject;
import java.util.regex.Matcher;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxICellEditor;

import it.vige.greenarea.I18N.I18N;
import it.vige.greenarea.ln.model.LNEdge;
import it.vige.greenarea.ln.model.LNEdge.Cathegory;
import it.vige.greenarea.ln.model.LNEdge.Domain;
import it.vige.greenarea.ln.model.LNEdge.Status;

public class LNEdgeEditor extends java.awt.Dialog implements mxICellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1877290791138606783L;
	protected mxGraphComponent graphComponent;
	protected mxCell editingCell;
	protected LNEdge edge;

	public mxCell getEditingCell() {
		return editingCell;
	}

	public mxGraphComponent getGc() {
		return graphComponent;
	}

	public LNEdge getNode() {
		return edge;
	}

	public JTabbedPane getEditorTabPane() {
		return editorTabPane;
	}

	/** Creates new form LNSiteEditor2 */
	public LNEdgeEditor(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	public LNEdgeEditor(mxGraphComponent gc, LNEdge edge) {
		this((JFrame) SwingUtilities.windowForComponent(gc), false);
		this.graphComponent = gc;
		this.edge = edge;
	}

	@Override
	public void startEditing(Object cell, EventObject trigger) {
		editingCell = (mxCell) cell;
		// edge.loadElement((Element) editingCell.getValue());
		edge = (LNEdge) editingCell.getValue();
		displayGenerics(edge);
		disableButton(saveGenericsChangeButton);
		disableButton(resetGenericsValuesButton);
		nameErrorField.setText("");
		graphComponent.setEnabled(false);
		this.setVisible(true);
	}

	@Override
	public void stopEditing(boolean cancel) {
		if (editingCell != null) {
			if (!cancel)
				graphComponent.labelChanged(editingCell, edge, null);
			editingCell = null;
			graphComponent.setEnabled(true);
			this.setVisible(false);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		saveConfirmDialog = new javax.swing.JDialog();
		dialogMessage = new javax.swing.JLabel();
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		editorTabPane = new javax.swing.JTabbedPane();
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		nameField = new javax.swing.JTextField();
		statusCombo = new javax.swing.JComboBox();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		domainCombo = new javax.swing.JComboBox();
		jLabel5 = new javax.swing.JLabel();
		cathegoryCombo = new javax.swing.JComboBox();
		jLabel6 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		descriptionTextArea = new javax.swing.JTextArea();
		saveGenericsChangeButton = new javax.swing.JButton();
		resetGenericsValuesButton = new javax.swing.JButton();
		nameErrorField = new javax.swing.JTextField();

		saveConfirmDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		saveConfirmDialog.setAlwaysOnTop(true);
		saveConfirmDialog.setResizable(false);

		dialogMessage.setText(I18N.getString("ConfirmSaveMessage"));

		okButton.setText(I18N.getString("OK"));
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});

		cancelButton.setText(I18N.getString("Cancel"));
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout saveConfirmDialogLayout = new javax.swing.GroupLayout(
				saveConfirmDialog.getContentPane());
		saveConfirmDialog.getContentPane().setLayout(saveConfirmDialogLayout);
		saveConfirmDialogLayout.setHorizontalGroup(saveConfirmDialogLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(saveConfirmDialogLayout.createSequentialGroup().addContainerGap()
						.addGroup(saveConfirmDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(dialogMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										saveConfirmDialogLayout.createSequentialGroup().addComponent(cancelButton)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(okButton)))
						.addContainerGap()));
		saveConfirmDialogLayout.setVerticalGroup(saveConfirmDialogLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(saveConfirmDialogLayout.createSequentialGroup().addContainerGap().addComponent(dialogMessage)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 233, Short.MAX_VALUE)
						.addGroup(
								saveConfirmDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(okButton).addComponent(cancelButton))
						.addContainerGap()));

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				closeDialog(evt);
			}
		});

		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel1.setText(I18N.getString("Status"));

		nameField.setToolTipText(I18N.getString("NodeNameFormat"));
		nameField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
		nameField.setInputVerifier(new NameFieldVerifier());
		nameField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt) {
				nameFieldKeyTyped(evt);
			}
		});

		statusCombo.setModel(new javax.swing.DefaultComboBoxModel(I18N.getStrings(LNEdge.Status.values())));
		statusCombo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				statusComboActionPerformed(evt);
			}
		});

		jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel3.setText(I18N.getString("CarrierName"));

		jLabel4.setText(I18N.getString("Domain"));

		domainCombo.setModel(new javax.swing.DefaultComboBoxModel(I18N.getStrings(LNEdge.Domain.values())));
		domainCombo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				domainComboActionPerformed(evt);
			}
		});

		jLabel5.setText(I18N.getString("Cathegory"));

		cathegoryCombo.setModel(new javax.swing.DefaultComboBoxModel(I18N.getStrings(LNEdge.Cathegory.values())));
		cathegoryCombo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cathegoryComboActionPerformed(evt);
			}
		});

		jLabel6.setText(I18N.getString("Description"));

		descriptionTextArea.setColumns(20);
		descriptionTextArea.setRows(5);
		descriptionTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt) {
				descriptionTextAreaKeyTyped(evt);
			}
		});
		jScrollPane1.setViewportView(descriptionTextArea);

		saveGenericsChangeButton.setText(I18N.getString("Save"));
		saveGenericsChangeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveGenericsChangeButtonActionPerformed(evt);
			}
		});

		resetGenericsValuesButton.setText(I18N.getString("RESET"));
		resetGenericsValuesButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetGenericsValuesButtonActionPerformed(evt);
			}
		});

		nameErrorField.setEditable(false);
		nameErrorField.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
		nameErrorField.setForeground(new java.awt.Color(255, 0, 0));
		nameErrorField.setText("jTextField1");
		nameErrorField.setAutoscrolls(false);
		nameErrorField.setBorder(null);
		nameErrorField.setFocusable(false);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel1Layout.createSequentialGroup()
								.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(jLabel5).addComponent(jLabel4)
										.addGroup(jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(jLabel1)))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(jPanel1Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(cathegoryCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(domainCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(nameField).addComponent(statusCombo, 0,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(nameErrorField))
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
								.addComponent(saveGenericsChangeButton, javax.swing.GroupLayout.Alignment.LEADING,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(resetGenericsValuesButton, javax.swing.GroupLayout.Alignment.LEADING,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel1Layout.createSequentialGroup().addComponent(jLabel6)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 361,
												Short.MAX_VALUE)))
						.addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel3).addComponent(nameErrorField,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(14, 14, 14)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel1).addComponent(statusCombo, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel4).addComponent(domainCombo, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel5).addComponent(cathegoryCombo,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel6))
						.addGap(34, 34, 34).addComponent(resetGenericsValuesButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(saveGenericsChangeButton).addGap(86, 86, 86)));

		editorTabPane.addTab(I18N.getString("GeneralMenu"), jPanel1);

		add(editorTabPane, java.awt.BorderLayout.CENTER);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_closeDialog
		setVisible(false);
		graphComponent.setEnabled(true);
		dispose();
	}// GEN-LAST:event_closeDialog

	private void nameFieldKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_nameFieldKeyTyped
		enableButton(saveGenericsChangeButton);
		enableButton(resetGenericsValuesButton);
	}// GEN-LAST:event_nameFieldKeyTyped

	private void statusComboActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_statusComboActionPerformed
		enableButton(saveGenericsChangeButton);
		enableButton(resetGenericsValuesButton);
	}// GEN-LAST:event_statusComboActionPerformed

	private void domainComboActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_domainComboActionPerformed
		enableButton(saveGenericsChangeButton);
		enableButton(resetGenericsValuesButton);
	}// GEN-LAST:event_domainComboActionPerformed

	private void cathegoryComboActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cathegoryComboActionPerformed
		enableButton(saveGenericsChangeButton);
		enableButton(resetGenericsValuesButton);
	}// GEN-LAST:event_cathegoryComboActionPerformed

	private void descriptionTextAreaKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_descriptionTextAreaKeyTyped
		enableButton(saveGenericsChangeButton);
		enableButton(resetGenericsValuesButton);
	}// GEN-LAST:event_descriptionTextAreaKeyTyped

	private void saveGenericsChangeButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveGenericsChangeButtonActionPerformed
		doConfirmDialog();
	}// GEN-LAST:event_saveGenericsChangeButtonActionPerformed

	private void doConfirmDialog() {
		saveConfirmDialog.setLocation(this.getLocation());
		saveConfirmDialog.setSize(347, 101);
		saveConfirmDialog.setVisible(true);
		this.setEnabled(false);
	}

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okButtonActionPerformed
		disableButton(saveGenericsChangeButton);
		disableButton(resetGenericsValuesButton);
		// edge.loadElement((Element) editingCell.getValue());
		edge = (LNEdge) editingCell.getValue();
		saveGenericsData(edge);
		graphComponent.getGraph().cellLabelChanged(editingCell, edge, false);
		saveConfirmDialog.setVisible(false);
		this.setEnabled(true);
	}// GEN-LAST:event_okButtonActionPerformed

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
		saveConfirmDialog.setVisible(false);
		this.setEnabled(true);
	}// GEN-LAST:event_cancelButtonActionPerformed

	private void resetGenericsValuesButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_resetGenericsValuesButtonActionPerformed
		displayGenerics(edge);
		disableButton(saveGenericsChangeButton);
		disableButton(resetGenericsValuesButton);
	}// GEN-LAST:event_resetGenericsValuesButtonActionPerformed

	private void disableButton(JButton b) {
		b.setEnabled(false);
		b.setForeground(Color.gray);
	}

	private void enableButton(JButton b) {
		b.setEnabled(true);
		b.setForeground(Color.black);
	}

	private void displayGenerics(LNEdge edge) {
		cathegoryCombo.setSelectedIndex(edge.getCathegory().ordinal());
		descriptionTextArea.setText(edge.getDescription());
		domainCombo.setSelectedIndex(edge.getDomain().ordinal());
		nameField.setText(edge.getName());
		doVerifySiteName(edge.getName());
		statusCombo.setSelectedIndex(edge.getStatus().ordinal());
	}

	private void saveGenericsData(LNEdge edge) {
		edge.setCathegory(Cathegory.values()[cathegoryCombo.getSelectedIndex()]);
		edge.setDescription(descriptionTextArea.getText());
		edge.setDomain(Domain.values()[domainCombo.getSelectedIndex()]);
		edge.setName(nameField.getText());
		edge.setStatus(Status.values()[statusCombo.getSelectedIndex()]);
	}

	private class NameFieldVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			String name = ((JTextField) input).getText();
			return doVerifySiteName(name);
		}
	}

	private static String errorMessage = I18N.getString("InvalidNameError");

	private boolean doVerifySiteName(String name) {
		Matcher m = LNEdge.EdgeNamePattern.matcher(name);
		if (!m.matches()) {
			nameErrorField.setText(errorMessage);
			nameField.setForeground(Color.red);
			disableButton(saveGenericsChangeButton);
		} else {
			nameErrorField.setText("");
			nameField.setForeground(Color.black);
			enableButton(saveGenericsChangeButton);
		}
		return m.matches();
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton cancelButton;
	private javax.swing.JComboBox cathegoryCombo;
	private javax.swing.JTextArea descriptionTextArea;
	private javax.swing.JLabel dialogMessage;
	private javax.swing.JComboBox domainCombo;
	private javax.swing.JTabbedPane editorTabPane;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextField nameErrorField;
	private javax.swing.JTextField nameField;
	private javax.swing.JButton okButton;
	private javax.swing.JButton resetGenericsValuesButton;
	private javax.swing.JDialog saveConfirmDialog;
	private javax.swing.JButton saveGenericsChangeButton;
	private javax.swing.JComboBox statusCombo;
	// End of variables declaration//GEN-END:variables

}
