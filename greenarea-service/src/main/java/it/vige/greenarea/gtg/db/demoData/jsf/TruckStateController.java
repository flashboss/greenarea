package it.vige.greenarea.gtg.db.demoData.jsf;

import static it.vige.greenarea.dto.StatoVeicolo.IDLE;
import static it.vige.greenarea.dto.StatoVeicolo.valueOf;
import static it.vige.greenarea.dto.StatoVeicolo.values;
import static it.vige.greenarea.gtg.db.demoData.jsf.util.JsfUtil.getSelectItems;
import static java.util.Arrays.asList;
import it.vige.greenarea.dto.StatoVeicolo;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.inject.Named;

@Named("truckStateController")
@SessionScoped
public class TruckStateController implements Serializable {

	private static final long serialVersionUID = -6076424677053174191L;
	private StatoVeicolo current;

	public TruckStateController() {
	}

	public StatoVeicolo getSelected() {
		if (current == null) {
			current = IDLE;
		}
		return current;
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return getSelectItems(asList(values()), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return getSelectItems(asList(values()), true);
	}

	@FacesConverter(forClass = StatoVeicolo.class)
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
			if (object instanceof StatoVeicolo) {
				StatoVeicolo o = (StatoVeicolo) object;
				return o.name();
			} else {
				throw new IllegalArgumentException("object " + object
						+ " is of type " + object.getClass().getName()
						+ "; expected type: " + StatoVeicolo.class.getName());
			}
		}
	}
}
