package it.vige.greenarea.gtg.db.demoData.jsf;

import static it.vige.greenarea.dto.Fuel.DIESEL;
import static it.vige.greenarea.dto.Fuel.valueOf;
import static it.vige.greenarea.dto.Fuel.values;
import static it.vige.greenarea.gtg.db.demoData.jsf.util.JsfUtil.getSelectItems;
import static java.util.Arrays.asList;
import it.vige.greenarea.dto.Fuel;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.inject.Named;

@Named("fuelController")
@SessionScoped
public class FuelController implements Serializable {

	private static final long serialVersionUID = 1586086578122036251L;
	private Fuel current;

	public FuelController() {
	}

	public Fuel getSelected() {
		if (current == null) {
			current = DIESEL;
		}
		return current;
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return getSelectItems(asList(values()), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return getSelectItems(asList(values()), true);
	}

	@FacesConverter(forClass = Fuel.class)
	public static class TruckStateControllerConverter implements Converter {

		public Object getAsObject(FacesContext facesContext,
				UIComponent component, String value) {
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

		public String getAsString(FacesContext facesContext,
				UIComponent component, Object object) {
			if (object == null) {
				return null;
			}
			if (object instanceof Fuel) {
				Fuel o = (Fuel) object;
				return o.name();
			} else {
				throw new IllegalArgumentException("object " + object
						+ " is of type " + object.getClass().getName()
						+ "; expected type: " + Fuel.class.getName());
			}
		}
	}
}
