package it.vige.greenarea.gtg.db.demoData.jsf;

import static it.vige.greenarea.cl.library.entities.FreightItemState.NOTAVAILABLE;
import static it.vige.greenarea.cl.library.entities.FreightItemState.valueOf;
import static it.vige.greenarea.cl.library.entities.FreightItemState.values;
import static it.vige.greenarea.gtg.db.demoData.jsf.util.JsfUtil.getSelectItems;
import static java.util.Arrays.asList;
import it.vige.greenarea.cl.library.entities.FreightItemState;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.inject.Named;

@Named("freightStateController")
@SessionScoped
public class FreightStateController implements Serializable {

	private static final long serialVersionUID = -6076424677053174191L;
	private FreightItemState current;

	public FreightStateController() {
	}

	public FreightItemState getSelected() {
		if (current == null) {
			current = NOTAVAILABLE;
		}
		return current;
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return getSelectItems(asList(values()), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return getSelectItems(asList(values()), true);
	}

	@FacesConverter(forClass = FreightItemState.class)
	public static class FreightStateControllerConverter implements Converter {

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
			if (object instanceof FreightItemState) {
				FreightItemState o = (FreightItemState) object;
				return o.name();
			} else {
				throw new IllegalArgumentException("object " + object
						+ " is of type " + object.getClass().getName()
						+ "; expected type: "
						+ FreightItemState.class.getName());
			}
		}
	}
}
