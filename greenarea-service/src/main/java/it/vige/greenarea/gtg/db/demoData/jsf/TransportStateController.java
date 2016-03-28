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
package it.vige.greenarea.gtg.db.demoData.jsf;

import static it.vige.greenarea.cl.library.entities.Transport.TransportState.unknown;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.valueOf;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.values;
import static it.vige.greenarea.gtg.db.demoData.jsf.util.JsfUtil.getSelectItems;
import static java.util.Arrays.asList;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import it.vige.greenarea.cl.library.entities.Transport.TransportState;

@Named("transportStateController")
@SessionScoped
public class TransportStateController implements Serializable {

	private static final long serialVersionUID = -6076424677053174191L;
	private TransportState current;

	public TransportStateController() {
	}

	public TransportState getSelected() {
		if (current == null) {
			current = unknown;
		}
		return current;
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return getSelectItems(asList(values()), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return getSelectItems(asList(values()), true);
	}

	@FacesConverter(forClass = TransportState.class)
	public static class TransportStateControllerConverter implements Converter {

		public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			return valueOf(value);
		}

		java.lang.Long getKey(String value) {
			java.lang.Long key;
			key = Long.valueOf(value);
			return key;
		}

		String getStringKey(java.lang.Long value) {
			StringBuffer sb = new StringBuffer();
			sb.append(value);
			return sb.toString();
		}

		public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
			if (object == null) {
				return null;
			}
			if (object instanceof TransportState) {
				TransportState o = (TransportState) object;
				return o.name();
			} else {
				throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName()
						+ "; expected type: " + TransportState.class.getName());
			}
		}
	}
}
