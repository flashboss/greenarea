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
package it.vige.greenarea.itseasy.routing.swing;

import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;

import it.vige.greenarea.I18N.I18N;
import it.vige.greenarea.itseasy.sgrl.wswrapper.LogisticNetworkManagementService;
import it.vige.greenarea.itseasy.sgrl.wswrapper.LogisticNetworkRoutingService;
import it.vige.greenarea.utilities.Application;
import it.vige.greenarea.utilities.NetworkResourceTest;
import it.vige.greenarea.utilities.NetworkResourceTest.NetworkTestType;

public class RootUrlConfigurationDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = -8461241931705795256L;

	private static Logger logger = getLogger(RootUrlConfigurationDialog.class);

	private static boolean hasChanged;

	/**
	 * Creates new form RootUrlConfigurationDialog
	 */
	public RootUrlConfigurationDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		urlTextField = new javax.swing.JTextField();
		saveButton = new javax.swing.JButton();
		testConnectionButton = new javax.swing.JButton();
		testResultMessage = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentShown(java.awt.event.ComponentEvent evt) {
				formComponentShown(evt);
			}
		});

		jLabel1.setText(I18N.getString("ChangeRootUrlMessage"));

		urlTextField.setText("jTextField1");
		urlTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt) {
				urlTextFieldKeyTyped(evt);
			}
		});

		saveButton.setText(I18N.getString("Save"));
		saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveButtonActionPerformed(evt);
			}
		});

		testConnectionButton.setText(I18N.getString("testConnectionButton"));
		testConnectionButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				testConnectionButtonActionPerformed(evt);
			}
		});

		testResultMessage.setForeground(new java.awt.Color(255, 0, 0));
		testResultMessage.setText("jLabel2");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(39, 39, 39)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jLabel1)
								.addComponent(urlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 437,
										javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
										.addComponent(saveButton, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
								.addComponent(testConnectionButton, javax.swing.GroupLayout.Alignment.LEADING,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(testResultMessage, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(35, 35, 35).addComponent(jLabel1)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(urlTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(saveButton)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(testConnectionButton).addComponent(testResultMessage))
				.addContainerGap(38, Short.MAX_VALUE)));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void formComponentShown(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_formComponentShown
		hasChanged = false;
		urlTextField.setText(Application.getProperty("serverRootUrl"));
		urlTextField.setEnabled(true);
		saveButton.setEnabled(false);
		testConnectionButton.setEnabled(true);
		testResultMessage.setText("");
	}// GEN-LAST:event_formComponentShown

	private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveButtonActionPerformed
		if (hasChanged) {
			String newUrl = urlTextField.getText();
			Application.setProperty("serverRootUrl", newUrl);
			Application.saveProperties();
			LogisticNetworkManagementService.setRootUrl(newUrl);
			LogisticNetworkRoutingService.setRootUrl(newUrl);
			urlTextField.setEnabled(false);
			saveButton.setEnabled(false);
		}
	}// GEN-LAST:event_saveButtonActionPerformed

	private void urlTextFieldKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_urlTextFieldKeyTyped
		hasChanged = true;
		saveButton.setEnabled(true);
		testConnectionButton.setEnabled(true);
		testResultMessage.setText("");
	}// GEN-LAST:event_urlTextFieldKeyTyped

	private void testConnectionButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_testConnectionButtonActionPerformed
		try {
			new URL(urlTextField.getText());
			if (!NetworkResourceTest.test(NetworkTestType.HTTPTEST, urlTextField.getText())) {
				testResultMessage.setForeground(Color.RED);
				testResultMessage.setText(I18N.getString("testFailed"));
			} else {
				testResultMessage.setForeground(Color.GREEN);
				testResultMessage.setText(I18N.getString("testSucceded"));
			}
		} catch (MalformedURLException e) {
			testResultMessage.setForeground(Color.RED);
			testResultMessage.setText(I18N.getString("malformedURL"));
		}
		testConnectionButton.setEnabled(false);
	}// GEN-LAST:event_testConnectionButtonActionPerformed

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/*
		 * Set the Nimbus look and feel
		 */
		// <editor-fold defaultstate="collapsed"
		// desc=" Look and feel setting code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase
		 * /tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			logger.error("logistic editor", ex);
		} catch (InstantiationException ex) {
			logger.error("logistic editor", ex);
		} catch (IllegalAccessException ex) {
			logger.error("logistic editor", ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			logger.error("logistic editor", ex);
		}
		// </editor-fold>

		/*
		 * Create and display the dialog
		 */
		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				RootUrlConfigurationDialog dialog = new RootUrlConfigurationDialog(new java.awt.Frame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {

					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel jLabel1;
	private javax.swing.JButton saveButton;
	private javax.swing.JButton testConnectionButton;
	private javax.swing.JLabel testResultMessage;
	private javax.swing.JTextField urlTextField;
	// End of variables declaration//GEN-END:variables
}
