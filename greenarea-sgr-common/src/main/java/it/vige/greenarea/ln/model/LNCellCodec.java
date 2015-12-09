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

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.mxgraph.io.mxCellCodec;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;

public class LNCellCodec extends mxCellCodec {

	protected LNCell lnCell;

	private Logger logger = getLogger(getClass());

	@Override
	public Object beforeEncode(mxCodec enc, Object obj, Node node) {
		lnCell = null;
		mxCell cell = null;
		if (obj instanceof mxCell) {
			cell = (mxCell) obj;

			if (cell.getValue() instanceof LNCell) {
				lnCell = (LNCell) cell.getValue();
				cell.setValue(lnCell.toElement());
			}
		}
		return super.beforeEncode(enc, obj, node);
	}

	/**
	 * Prendo il Value (che
	 * ?????????????????????????????????????????????????????? una LNCell) e gli
	 * sostituisco la LNCell codificata in element
	 * 
	 */
	@Override
	public Node afterEncode(mxCodec enc, Object obj, Node node) {
		Node result = super.afterEncode(enc, obj, node);
		if (lnCell != null)
			((mxCell) obj).setValue(lnCell);
		return result;
	}

	@Override
	public Node beforeDecode(mxCodec dec, Node node, Object obj) {
		Node tmp = super.beforeDecode(dec, node, obj);
		if (obj instanceof mxCell) {
			mxCell cell = (mxCell) obj;

			if (cell.getValue() instanceof Node) {

				/**
				 * decodifica il nodo in LNCell e lo rimette al posto del value
				 */
				Element elt = (Element) cell.getValue();

				LNCell lnCell = null;
				Class<?> c = null;

				try {
					c = Class.forName("it.vige.greenarea.ln.model.".concat(elt.getAttribute(LNCell.LNCELLTYPE)));
				} catch (ClassNotFoundException ex) {
					logger.error("errore sgr common", ex);
				}
				try {
					try {
						lnCell = (LNCell) c.getConstructor().newInstance();
					} catch (InstantiationException ex) {
						logger.error("errore sgr common", ex);
					} catch (IllegalAccessException ex) {
						logger.error("errore sgr common", ex);
					} catch (IllegalArgumentException ex) {
						logger.error("errore sgr common", ex);
					} catch (InvocationTargetException ex) {
						logger.error("errore sgr common", ex);
					}
				} catch (NoSuchMethodException ex) {
					logger.error("errore sgr common", ex);
				} catch (SecurityException ex) {
					logger.error("errore sgr common", ex);
				}
				lnCell.loadElement(elt);
				cell.setValue(lnCell);
			}
		}
		return tmp;
	}

	@Override
	public boolean isExcluded(Object obj, String attr, Object value, boolean write) {
		return super.isExcluded(obj, attr, value, write);
	}
}
