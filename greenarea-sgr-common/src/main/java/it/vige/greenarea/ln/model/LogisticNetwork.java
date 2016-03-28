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
package it.vige.greenarea.ln.model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

/**
 * A graph that creates new edges from a given template edge.
 */
public class LogisticNetwork extends mxGraph implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = -5854808850809197202L;
	private String name;
	private long uid;
	/**
	 * Holds the shared number formatter.
	 * 
	 * @see NumberFormat#getInstance()
	 */
	public static final NumberFormat numberFormat = NumberFormat.getInstance();
	/**
	 * Holds the edge to be used as a template for inserting new edges.
	 */
	protected Object edgeTemplate;

	/**
	 * Custom graph that defines the alternate edge style to be used when the
	 * middle control point of edges is double clicked (flipped).
	 */
	public LogisticNetwork() {
		// setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
		setAllowLoops(true);
		uid = GregorianCalendar.getInstance().getTimeInMillis();
	}

	public long getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the edge template to be used to inserting edges.
	 */
	public void setEdgeTemplate(Object template) {
		edgeTemplate = template;
	}

	/**
	 * Prints out some useful information about the cell in the tooltip.
	 */
	public String getToolTipForCell(Object cell) {
		String tip = "<html>";
		mxGeometry geo = getModel().getGeometry(cell);
		mxCellState state = getView().getState(cell);

		if (getModel().isEdge(cell)) {
			tip += "points={";

			if (geo != null) {
				List<mxPoint> points = geo.getPoints();

				if (points != null) {
					Iterator<mxPoint> it = points.iterator();

					while (it.hasNext()) {
						mxPoint point = it.next();
						tip += "[x=" + numberFormat.format(point.getX()) + ",y=" + numberFormat.format(point.getY())
								+ "],";
					}

					tip = tip.substring(0, tip.length() - 1);
				}
			}

			tip += "}<br>";
			tip += "absPoints={";

			if (state != null) {

				for (int i = 0; i < state.getAbsolutePointCount(); i++) {
					mxPoint point = state.getAbsolutePoint(i);
					tip += "[x=" + numberFormat.format(point.getX()) + ",y=" + numberFormat.format(point.getY()) + "],";
				}

				tip = tip.substring(0, tip.length() - 1);
			}

			tip += "}";
		} else {
			tip += "geo=[";

			if (geo != null) {
				tip += "x=" + numberFormat.format(geo.getX()) + ",y=" + numberFormat.format(geo.getY()) + ",width="
						+ numberFormat.format(geo.getWidth()) + ",height=" + numberFormat.format(geo.getHeight());
			}

			tip += "]<br>";
			tip += "state=[";

			if (state != null) {
				tip += "x=" + numberFormat.format(state.getX()) + ",y=" + numberFormat.format(state.getY()) + ",width="
						+ numberFormat.format(state.getWidth()) + ",height=" + numberFormat.format(state.getHeight());
			}

			tip += "]";
		}

		mxPoint trans = getView().getTranslate();

		tip += "<br>scale=" + numberFormat.format(getView().getScale()) + ", translate=[x="
				+ numberFormat.format(trans.getX()) + ",y=" + numberFormat.format(trans.getY()) + "]";
		tip += "</html>";

		return tip;
	}

	/*
	 * Overrides method to disallow edge label editing public boolean
	 * isCellEditable(Object cell) { return !getModel().isEdge(cell); }
	 */
	/**
	 * Overrides the method to use the currently selected edge template for new
	 * edges.
	 * 
	 * @param graph
	 * @param parent
	 * @param id
	 * @param value
	 * @param source
	 * @param target
	 * @param style
	 * @return
	 */
	public Object createEdge(Object parent, String id, Object value, Object source, Object target, String style) {
		if (edgeTemplate != null) {
			mxCell edge = (mxCell) cloneCells(new Object[] { edgeTemplate })[0];
			edge.setId(id);

			return edge;
		}

		return super.createEdge(parent, id, value, source, target, style);
	}

	// Overrides method to provide a cell label in the display
	@Override
	public String convertValueToString(Object cell) {
		if (cell instanceof mxCell) {
			if (((mxCell) cell).getAttribute(LNCell.LNCELLTYPE) != null) {
				String name = ((mxCell) cell).getAttribute(LNCell.LNCELLNAME);
				return name;
			}
		}

		return super.convertValueToString(cell);
	}

	// Overrides method to store a cell label in the model
	/*
	 * public void cellLabelChanged(Object cell, Object newValue, boolean
	 * autoSize) { if (cell instanceof mxCell && newValue != null) { Object
	 * value = ((mxCell) cell).getValue();
	 * 
	 * if (value instanceof Node) { String label = newValue.toString(); Element
	 * elt = (Element) value;
	 * 
	 * if (elt.getTagName().equalsIgnoreCase("person")) { int pos =
	 * label.indexOf(' ');
	 * 
	 * String firstName = (pos > 0) ? label.substring(0, pos).trim() : label;
	 * String lastName = (pos > 0) ? label.substring( pos + 1,
	 * label.length()).trim() : "";
	 * 
	 * // Clones the value for correct undo/redo elt = (Element)
	 * elt.cloneNode(true);
	 * 
	 * elt.setAttribute("firstName", firstName); elt.setAttribute("lastName",
	 * lastName);
	 * 
	 * newValue = elt; } } }
	 * 
	 * super.cellLabelChanged(cell, newValue, autoSize); }
	 */

}
