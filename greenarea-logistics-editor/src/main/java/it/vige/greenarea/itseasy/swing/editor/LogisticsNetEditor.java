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

import java.awt.Color;
import java.awt.Point;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.w3c.dom.Document;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import it.vige.greenarea.I18N.I18N;
import it.vige.greenarea.geo.GisService;
import it.vige.greenarea.geo.GoogleGis;
import it.vige.greenarea.itseasy.ln.swing.LNCellEditor;
import it.vige.greenarea.itseasy.sgrl.wswrapper.LogisticNetworkManagementService;
import it.vige.greenarea.itseasy.sgrl.wswrapper.LogisticNetworkRoutingService;
import it.vige.greenarea.ln.model.LNArea;
import it.vige.greenarea.ln.model.LNCellCodec;
import it.vige.greenarea.ln.model.LNSimpleCarrier;
import it.vige.greenarea.ln.model.LNSite;
import it.vige.greenarea.ln.model.LNSitesSet;
import it.vige.greenarea.ln.model.LogisticNetwork;
import it.vige.greenarea.utilities.Application;
import it.vige.greenarea.utilities.LNutilities;
import it.vige.greenarea.utilities.Proxy;

public class LogisticsNetEditor extends BasicGraphEditor {
	private static LogisticsNetEditor editor;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4601740824088314699L;

	private static Logger logger = getLogger(LogisticsNetEditor.class);

	/**
	 * Holds the shared number formatter.
	 * 
	 * @see NumberFormat#getInstance()
	 */
	public static final NumberFormat numberFormat = NumberFormat.getInstance();

	/**
	 * Impostazione della lingua
	 * 
	 */
	static {
		Locale.setDefault(
				new Locale(Application.getProperty("Locale.language"), Application.getProperty("Locale.country")));
	}

	/**
	 * Servizio GIS
	 * 
	 */
	public static GisService gisService = new GoogleGis(I18N.getLocale());

	public static GisService getGisService() {
		return gisService;
	}

	/**
	 * Holds the URL for the icon to be used as a handle for creating new
	 * connections. This is currently unused.
	 */
	public static URL url = null;
	private static CustomGraphComponent graphComponent = new CustomGraphComponent(LNutilities.getLogisticNetwork());

	// public static mxGraph getGraph(){
	// return graphComponent.getGraph();
	// }
	// GraphEditor.class.getResource("/it/vige/itseasy/swing/images/connector.gif");

	public LogisticsNetEditor() {
		this(I18N.getString("MainTitle"), graphComponent);

	}

	/**
	 * 
	 */
	public LogisticsNetEditor(String appTitle, mxGraphComponent component) {
		super(appTitle, component);
		final mxGraph graph = graphComponent.getGraph();
		// Creates the shapes palette
		EditorPalette shapesPalette = insertPalette(I18N.getString("Carriers"));
		EditorPalette imagesPalette = insertPalette(I18N.getString("Nodes"));
		// EditorPalette symbolsPalette =
		// insertPalette(mxResources.get("symbols"));

		// Sets the edge template to be used for creating new edges if an edge
		// is clicked in the shape palette
		shapesPalette.addListener(mxEvent.SELECT, new mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable) {
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell)) {
						((LogisticNetwork) graph).setEdgeTemplate(cell);
					}
				}
			}

		});

		// Adds some template cells for dropping into the graph
		/*
		 * shapesPalette .addTemplate( "Container", new ImageIcon(
		 * GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/swimlane.png")),
		 * "swimlane", 280, 280, "Container"); shapesPalette .addTemplate(
		 * "Icon", new ImageIcon( GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/rounded.png")),
		 * "icon;image=/it/vige/itseasy/swing/images/wrench.png", 70, 70,
		 * "Icon"); shapesPalette .addTemplate( "Label", new ImageIcon(
		 * GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/rounded.png")),
		 * "label;image=/it/vige/itseasy/swing/images/gear.png", 130, 50,
		 * "Label"); shapesPalette .addTemplate( "Rectangle", new ImageIcon(
		 * GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/rectangle.png" )), null,
		 * 160, 120, ""); shapesPalette .addTemplate( "Rounded Rectangle", new
		 * ImageIcon( GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/rounded.png")),
		 * "rounded=1", 160, 120, ""); shapesPalette .addTemplate( "Ellipse",
		 * new ImageIcon( GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/ellipse.png")),
		 * "ellipse", 160, 160, ""); shapesPalette .addTemplate(
		 * "Double Ellipse", new ImageIcon( GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/doubleellipse.png" )),
		 * "ellipse;shape=doubleEllipse", 160, 160, ""); shapesPalette
		 * .addTemplate( "Triangle", new ImageIcon( GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/triangle.png")),
		 * "triangle", 120, 160, ""); shapesPalette .addTemplate( "Rhombus", new
		 * ImageIcon( GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/rhombus.png")),
		 * "rhombus", 160, 160, "");
		 */
		shapesPalette.addTemplate("Horizontal Line",
				new ImageIcon(LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/hline.png")), "line",
				160, 10, "");
		/*
		 * shapesPalette .addTemplate( "Hexagon", new ImageIcon(
		 * GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/hexagon.png")),
		 * "shape=hexagon", 160, 120, ""); shapesPalette .addTemplate(
		 * "Cylinder", new ImageIcon( GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/cylinder.png")),
		 * "shape=cylinder", 120, 160, ""); shapesPalette .addTemplate( "Actor",
		 * new ImageIcon( GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/actor.png")),
		 * "shape=actor", 120, 160, ""); shapesPalette .addTemplate( "Cloud",
		 * new ImageIcon( GraphEditor.class
		 * .getResource("/it/vige/itseasy/swing/images/cloud.png")),
		 * "ellipse;shape=cloud", 160, 120, "");
		 */

		shapesPalette.addEdgeTemplate("Straight",
				new ImageIcon(LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/straight.png")),
				"straight", 120, 120, "");

		shapesPalette.addEdgeTemplate(I18N.getString("GenericVector"),
				new ImageIcon(LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/connect.png")), null,
				100, 100, (new LNSimpleCarrier(I18N.getString("UNDEFINED"))));
		shapesPalette.addEdgeTemplate("Vertical Connector",
				new ImageIcon(LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/vertical.png")),
				"vertical", 100, 100, "");

		imagesPalette.addTemplate("Area",
				new ImageIcon(LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/area.png")),
				"roundImage;image=/it/vige/itseasy/swing/images/area.png", 50, 50, (new LNArea("Area")));
		imagesPalette.addTemplate("Aeroporto",
				new ImageIcon(LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/aeroporto.png")),
				"roundImage;image=/it/vige/itseasy/swing/images/aeroporto.png", 50, 50, (new LNSite("Aeroporto")));
		imagesPalette.addTemplate("Stazione",
				new ImageIcon(
						LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/icon-train-station.png")),
				"roundImage;image=/it/vige/itseasy/swing/images/icon-train-station.png", 50, 50,
				(new LNSite("Stazione")));
		imagesPalette.addTemplate("Citta'",
				new ImageIcon(LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/city.png")),
				"roundImage;image=/it/vige/itseasy/swing/images/city.png", 50, 50,
				(new LNArea("Citta'", LNArea.AreaLevel.city)));
		imagesPalette.addTemplate("InterModale",
				new ImageIcon(
						LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/IntermodalExchange.png")),
				"roundImage;image=/it/vige/itseasy/swing/images/IntermodalExchange.png", 50, 50,
				(new LNSite("InterModale")));
		imagesPalette.addTemplate("Sede Centrale",
				new ImageIcon(LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/hq.png")),
				"roundImage;image=/it/vige/itseasy/swing/images/hq.png", 50, 50, (new LNSite("Sede Centrale")));
		imagesPalette.addTemplate("Produzione",
				new ImageIcon(LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/factory.png")),
				"roundImage;image=/it/vige/itseasy/swing/images/factory.png", 50, 50, (new LNSite("Produzione")));
		imagesPalette.addTemplate("Pool",
				new ImageIcon(LogisticsNetEditor.class.getResource("/it/vige/itseasy/swing/images/sitesSet.png")),
				"roundImage;image=/it/vige/itseasy/swing/images/sitesSet.png", 50, 50, (new LNSitesSet("Pool")));
		editor = this;
	}

	/**
	* 
	*/
	public static class CustomGraphComponent extends mxGraphComponent {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6833603133512882012L;

		/**
		 * 
		 * @param graph
		 */
		public CustomGraphComponent(mxGraph graph) {
			super(graph);

			// Sets switches typically used in an editor
			setPageVisible(true);
			setGridVisible(true);
			setToolTips(true);
			getConnectionHandler().setCreateTarget(true);

			// Loads the defalt stylesheet from an external file
			mxCodec codec = new mxCodec();
			Document doc = mxUtils.loadDocument(LogisticsNetEditor.class
					.getResource("/it/vige/itseasy/swing/resources/default-style.xml").toString());
			codec.decode(doc.getDocumentElement(), graph.getStylesheet());

			// Sets the background to white
			getViewport().setOpaque(true);
			getViewport().setBackground(Color.WHITE);
			super.setCellEditor(new LNCellEditor(this));
		}

		/**
		 * Overrides drop behaviour to set the cell style if the target is not a
		 * valid drop target and the cells are of the same type (eg. both
		 * vertices or both edges).
		 */
		public Object[] importCells(Object[] cells, double dx, double dy, Object target, Point location) {
			if (target == null && cells.length == 1 && location != null) {
				target = getCellAt(location.x, location.y);

				if (target instanceof mxICell && cells[0] instanceof mxICell) {
					mxICell targetCell = (mxICell) target;
					mxICell dropCell = (mxICell) cells[0];

					if (targetCell.isVertex() == dropCell.isVertex() || targetCell.isEdge() == dropCell.isEdge()) {
						mxIGraphModel model = graph.getModel();
						model.setStyle(target, model.getStyle(cells[0]));
						graph.setSelectionCell(target);

						return null;
					}
				}
			}

			return super.importCells(cells, dx, dy, target, location);
		}

	}

	/**
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			logger.error("logistic editor", e1);
		}
		Proxy.initialize();
		mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";
		LogisticsNetEditor editor = new LogisticsNetEditor();
		EditorMenuBar editorMenuBar = new EditorMenuBar(editor);
		editorMenuBar.add(new LogisticsActionsMenu(editor), editorMenuBar.getMenuCount() - 1);
		editor.createFrame(editorMenuBar).setVisible(true);
		mxCodecRegistry.register(new LNCellCodec());
		String serverRootUrl = Application.getProperty("serverRootUrl");
		LogisticNetworkManagementService.setRootUrl(serverRootUrl);
		LogisticNetworkRoutingService.setRootUrl(serverRootUrl);

		logger.debug(System.getProperties().toString());

	}

	public static LogisticsNetEditor getEditor() {
		return editor;
	}

}
