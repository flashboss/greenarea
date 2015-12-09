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

import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;

import org.slf4j.Logger;
import org.w3c.dom.Document;

import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxXmlUtils;

import it.vige.greenarea.I18N.I18N;
import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.itseasy.routing.swing.PathFinderDialog;
import it.vige.greenarea.itseasy.routing.swing.RootUrlConfigurationDialog;
import it.vige.greenarea.itseasy.routing.swing.RouteFinderDialog;
import it.vige.greenarea.itseasy.sgrl.wswrapper.LogisticNetworkManagementService;
import it.vige.greenarea.ln.model.LogisticNetwork;
import it.vige.greenarea.sgrl.webservices.LogisticNetworkManagement;
import it.vige.greenarea.sgrl.webservices.UnsupportedEncodingException_Exception;
import it.vige.greenarea.utilities.LNutilities;

/**
 * 
 * @author 00917308
 */
public class LogisticsActionsMenu extends JMenu {

	private static final long serialVersionUID = 6558328516659685579L;

	private static Logger logger = getLogger(LogisticsActionsMenu.class);

	private static PathFinderDialog pathFinderDialog;
	private static RouteFinderDialog routeFinderDialog;
	private static RootUrlConfigurationDialog rootUrlConfigurationDialog;

	private static class PathFinderAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1619546388451309620L;

		@Override
		public void actionPerformed(ActionEvent e) {

			pathFinderDialog.display(e);
		}

	}

	private static class RouteFinderAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8502725096920320541L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Container container = LogisticsNetEditor.getEditor().getRootPane().getParent();
			routeFinderDialog.setLocation(
					container.getX() + container.getWidth() / 2 - routeFinderDialog.getWidth() / 2,
					container.getY() + container.getHeight() / 2 - routeFinderDialog.getHeight() / 2);
			routeFinderDialog.setGeoTo(new GeoLocation(0., 0.));
			routeFinderDialog.setGeoFrom(new GeoLocation(0., 0.));
			routeFinderDialog.display(e);
		}
	}

	private static class ServerSetUpAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6657884979026504109L;

		@Override
		public void actionPerformed(ActionEvent e) {

			rootUrlConfigurationDialog.setVisible(true);
		}
	}

	private static class UploadAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2981423160367199406L;

		@Override
		public void actionPerformed(ActionEvent e) {
			LogisticNetwork logisticNetwork = LNutilities.getLogisticNetwork();
			mxCodec codec = new mxCodec();
			try {
				String xml = URLEncoder.encode(mxXmlUtils.getXml(codec.encode(logisticNetwork.getModel())), "UTF-8");
				upload("LogisticNetwork", xml);
			} catch (UnsupportedEncodingException ex) {
				logger.error("logistic editor", ex);
			}

			/*
			 * FileUploader fu = new FileUploader(
			 * "http://10.229.16.57:8080/SGRL/Commonsfileuploadservlet" ); try {
			 * fu.doUpload(
			 * "C:\\Users\\00917308\\Desktop\\GRAPH_Editing\\NetbeansProject\\LogisticEditor\\LogisticNetwork.mxe"
			 * ); } catch (IOException ex) {
			 * Logger.getLogger(LogisticsActionsMenu
			 * .class.getName()).log(Level.SEVERE, null, ex); }
			 */
		}
	}

	private static class DownloadAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4590805136350900465L;

		@Override
		public void actionPerformed(ActionEvent e) {

			String xmlGraph = downLoad("LogisticNetwork");

			Document document;
			try {
				document = mxXmlUtils.parseXml(URLDecoder.decode(xmlGraph, "UTF-8"));
				mxCodec codec = new mxCodec(document);
				codec.decode(document.getDocumentElement(), LNutilities.getLogisticNetwork().getModel());
			} catch (UnsupportedEncodingException ex) {
				logger.error("logistic editor", ex);
			}
			editor.setCurrentFile(null);
			resetEditor(editor);
		}
	}

	private static BasicGraphEditor editor;

	public LogisticsActionsMenu(final BasicGraphEditor editor) {
		super(I18N.getString("LogisticsActionsMenu"));
		LogisticsActionsMenu.editor = editor;
		super.add(editor.bind(I18N.getString("ServerSetUp"), new ServerSetUpAction()));
		super.add(editor.bind(I18N.getString("PathFinder"), new PathFinderAction()));
		super.add(editor.bind(I18N.getString("RouteFinder"), new RouteFinderAction()));
		super.add(editor.bind(I18N.getString("LogisticNetworkUpload"), new UploadAction()));
		super.add(editor.bind(I18N.getString("LogisticNetworkDownload"), new DownloadAction()));
		pathFinderDialog = new PathFinderDialog((JFrame) (editor.getParent()), true);
		pathFinderDialog.setVisible(false);
		routeFinderDialog = new RouteFinderDialog((JFrame) (editor.getParent()), true);
		routeFinderDialog.setVisible(false);
		rootUrlConfigurationDialog = new RootUrlConfigurationDialog((JFrame) (editor.getParent()), true);
		rootUrlConfigurationDialog.setVisible(false);
	}

	private static String upload(java.lang.String name, java.lang.String xmlGraph) {
		LogisticNetworkManagementService service = new LogisticNetworkManagementService();
		LogisticNetworkManagement port = service.getLogisticNetworkManagementPort();
		String result = null;
		try {
			result = port.upload(name, xmlGraph);
		} catch (UnsupportedEncodingException_Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String downLoad(java.lang.String name) {
		LogisticNetworkManagementService service = new LogisticNetworkManagementService();
		LogisticNetworkManagement port = service.getLogisticNetworkManagementPort();
		return port.download(name);
	}

	protected static void resetEditor(BasicGraphEditor editor) {
		editor.setModified(false);
		editor.getUndoManager().clear();
		editor.getGraphComponent().zoomAndCenter();
	}
}
