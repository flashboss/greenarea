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
package it.vige.greenarea.bpm.custom.ui.dettaglio;

import static de.micromata.opengis.kml.v_2_2_0.Kml.unmarshal;
import static org.vaadin.vol.VectorLayer.SelectionMode.SIMPLE;

import java.util.List;

import org.vaadin.vol.Area;
import org.vaadin.vol.Bounds;
import org.vaadin.vol.Marker;
import org.vaadin.vol.MarkerLayer;
import org.vaadin.vol.OpenLayersMap;
import org.vaadin.vol.OpenStreetMapLayer;
import org.vaadin.vol.Point;
import org.vaadin.vol.Style;
import org.vaadin.vol.StyleMap;
import org.vaadin.vol.VectorLayer;
import org.vaadin.vol.VectorLayer.VectorSelectedEvent;
import org.vaadin.vol.VectorLayer.VectorSelectedListener;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.Window;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.LineStyle;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.PolyStyle;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.StyleSelector;
import it.vige.greenarea.bpm.custom.ui.pa.DefinizioneAreaGeografica;

public class KmlDocumentViewer extends OpenLayersMap {

	private static final long serialVersionUID = 6508833282493124799L;
	private Document doc;
	private OpenStreetMapLayer osm = new OpenStreetMapLayer();
	private VectorLayer vectorLayer = new VectorLayer();
	private UriFragmentUtility ufu = new UriFragmentUtility();

	public KmlDocumentViewer(String focusedFeature, Coordinate coordinate) {
		super();
		setImmediate(true);
		loadDocument();
		setWidth(100, UNITS_PERCENTAGE);
		addComponent(ufu);
		addLayer(osm);
		addLayer(vectorLayer);

		extractStyles(doc);

		displayFeatures(focusedFeature);

		vectorLayer.setSelectionMode(SIMPLE);
		vectorLayer.setImmediate(true);
		vectorLayer.addListener(new VectorSelectedListener() {
			public void vectorSelected(VectorSelectedEvent event) {
				final Area component = (Area) event.getVector();
				final String data = (String) component.getData();

				final Window window = new Window("Details");
				window.getContent().setSizeFull();
				window.setHeight("50%");
				window.setWidth("50%");

				Button button = new Button("Focus this feature");
				button.addListener(new ClickListener() {
					private static final long serialVersionUID = 3286851301965195290L;

					@Override
					public void buttonClick(ClickEvent event) {
						ufu.setFragment(data);
						displayFeatures(data);
						window.getParent().removeWindow(window);
					}
				});
				window.addComponent(button);
				window.setClosable(true);
				window.center();
				getWindow().addWindow(window);
				vectorLayer.setSelectedVector(null);
			}
		});

		if (coordinate != null) {
			// Definig a Marker Layer
			MarkerLayer markerLayer = new MarkerLayer();

			// Defining a new Marker

			final Marker marker = new Marker(coordinate.getLongitude(), coordinate.getLatitude());
			// URL of marker Icon
			marker.setIcon(new ThemeResource("img/marker.png"), 60, 60);
			markerLayer.addComponent(marker);
			addLayer(markerLayer);
			setCenter(coordinate.getLongitude(), coordinate.getLatitude());
		}

	}

	private void displayFeatures(String focusedFeature) {
		vectorLayer.removeAllComponents();
		boolean focusFeature = focusedFeature != null && !focusedFeature.isEmpty();
		Folder folder = (Folder) doc.getFeature().get(0);
		Bounds bounds = null;
		for (Feature childFeature : folder.getFeature()) {
			Folder childFolder = (Folder) childFeature;
			for (Feature feature2 : childFolder.getFeature()) {
				Placemark area = (Placemark) feature2;
				String name = area.getName();
				if (focusFeature && !name.equals(focusedFeature)) {
					continue;
				}
				List<Coordinate> coords = null;
				Geometry geometry = area.getGeometry();
				if (geometry instanceof Polygon) {
					coords = ((Polygon) geometry).getOuterBoundaryIs().getLinearRing().getCoordinates();
				} else if (geometry instanceof LineString) {
					coords = ((LineString) geometry).getCoordinates();
				}
				if (coords.size() == 0) {
					continue;
				}
				Point[] points = new Point[coords.size()];
				for (int i = 0; i < points.length; i++) {
					Coordinate coordinate = coords.get(i);
					points[i] = new Point(coordinate.getLongitude(), coordinate.getLatitude());
				}
				if (bounds == null) {
					bounds = new Bounds();
					bounds.setTop(points[0].getLat());
					bounds.setBottom(points[0].getLat());
					bounds.setLeft(points[0].getLon());
					bounds.setRight(points[0].getLon());
				}
				bounds.extend(points);

				Area area2 = new Area();
				area2.setRenderIntent(area.getStyleUrl().substring(1));
				area2.setPoints(points);
				vectorLayer.addComponent(area2);
				area2.setData(name);
			}
		}
		zoomToExtent(bounds);
	}

	private void extractStyles(Document doc) {
		List<StyleSelector> styleSelector = doc.getStyleSelector();
		StyleMap stylemap = new StyleMap();
		for (StyleSelector s : styleSelector) {
			if (s instanceof de.micromata.opengis.kml.v_2_2_0.Style) {
				de.micromata.opengis.kml.v_2_2_0.Style s2 = (de.micromata.opengis.kml.v_2_2_0.Style) s;
				String id = s.getId();
				Style style = new Style(id);
				LineStyle lineStyle = s2.getLineStyle();
				style.setStrokeColor(kmlGRBtoRGB(lineStyle.getColor()));
				PolyStyle polyStyle = s2.getPolyStyle();
				if (polyStyle != null)
					style.setFillColor(kmlGRBtoRGB(polyStyle.getColor()));
				stylemap.setStyle(id, style);
			} else {
				stylemap = new StyleMap();
				stylemap.setExtendDefault(true);
				vectorLayer.setStyleMap(stylemap);
			}
		}

	}

	private String kmlGRBtoRGB(String color) {
		return "#" + color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2);
	}

	private void loadDocument() {
		Kml unmarshal = unmarshal(
				DefinizioneAreaGeografica.class.getClassLoader().getResourceAsStream("ztl-guidonia.kml"));
		doc = (Document) unmarshal.getFeature();
	}

}
