package it.vige.greenarea.gtg.db.demoData.jsf;

import static it.vige.greenarea.gtg.db.demoData.jsf.util.JsfUtil.getSelectItems;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.sgapl.sgot.facade.ShippingOrderFacade;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;

@Named("shippingOrderController")
@SessionScoped
public class ShippingOrderController implements Serializable {

	private static final long serialVersionUID = 7838902646740863474L;
	private ShippingOrder current;
	@EJB
	private ShippingOrderFacade ejbFacade;

	public ShippingOrderController() {
	}

	public ShippingOrder getSelected() {
		if (current == null) {
			current = new ShippingOrder("");
		}
		return current;
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return getSelectItems(ejbFacade.findAll(), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return getSelectItems(ejbFacade.findAll(), true);
	}

	@FacesConverter(forClass = ShippingOrder.class)
	public static class ShippingOrderControllerConverter implements Converter {

		private static Logger logger = getLogger(ShippingOrderControllerConverter.class);

		public Object getAsObject(FacesContext facesContext,
				UIComponent component, String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			ShippingOrderFacade ejbFacade = null;
			try {
				ejbFacade = (ShippingOrderFacade) new InitialContext()
						.lookup("java:global/greenarea-service/ShippingOrderFacade");
			} catch (NamingException e) {
				logger.error("getAsObject", e);
			}
			return ejbFacade.find(value);
		}

		public String getAsString(FacesContext facesContext,
				UIComponent component, Object object) {
			if (object == null) {
				return null;
			}
			if (object instanceof ShippingOrder) {
				ShippingOrder o = (ShippingOrder) object;
				return o.getId();
			} else {
				throw new IllegalArgumentException("object " + object
						+ " is of type " + object.getClass().getName()
						+ "; expected type: " + ShippingOrder.class.getName());
			}
		}
	}
}
