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

import static it.vige.greenarea.gtg.db.demoData.jsf.util.JsfUtil.addErrorMessage;
import static it.vige.greenarea.gtg.db.demoData.jsf.util.JsfUtil.addSuccessMessage;
import static it.vige.greenarea.gtg.db.demoData.jsf.util.JsfUtil.getSelectItems;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.TruckServiceClass;
import it.vige.greenarea.gtg.db.demoData.jsf.util.PaginationHelper;
import it.vige.greenarea.gtg.db.facades.TruckServiceClassFacade;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;

@Named("truckServiceClassController")
@SessionScoped
public class TruckServiceClassController implements Serializable {

	private static final long serialVersionUID = -6076424677053174191L;
	private TruckServiceClass current;
	private DataModel items = null;
	@EJB
	private TruckServiceClassFacade ejbFacade;
	private PaginationHelper pagination;
	private int selectedItemIndex;

	public TruckServiceClassController() {
	}

	public TruckServiceClass getSelected() {
		if (current == null) {
			current = new TruckServiceClass();
			selectedItemIndex = -1;
		}
		return current;
	}

	private TruckServiceClassFacade getFacade() {
		return ejbFacade;
	}

	public PaginationHelper getPagination() {
		if (pagination == null) {
			pagination = new PaginationHelper(10) {
				@Override
				public int getItemsCount() {
					return getFacade().count();
				}

				@Override
				public DataModel createPageDataModel() {
					return new ListDataModel(getFacade().findRange(
							new int[] { getPageFirstItem(),
									getPageFirstItem() + getPageSize() }));
				}
			};
		}
		return pagination;
	}

	public String prepareList() {
		recreateModel();
		return "/truckServiceClass/List";
	}

	public String prepareView() {
		current = (TruckServiceClass) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem()
				+ getItems().getRowIndex();
		return "View";
	}

	public String prepareCreate() {
		current = new TruckServiceClass();
		selectedItemIndex = -1;
		return "Create";
	}

	public String create() {
		try {
			getFacade().create(current);
			addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString(
					"TruckServiceClassCreated"));
			return prepareCreate();
		} catch (Exception e) {
			addErrorMessage(
					e,
					ResourceBundle.getBundle("/Bundle").getString(
							"PersistenceErrorOccured"));
			return null;
		}
	}

	public String prepareEdit() {
		current = (TruckServiceClass) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem()
				+ getItems().getRowIndex();
		return "Edit";
	}

	public String update() {
		try {
			getFacade().edit(current);
			addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString(
					"TruckServiceClassUpdated"));
			return "View";
		} catch (Exception e) {
			addErrorMessage(
					e,
					ResourceBundle.getBundle("/Bundle").getString(
							"PersistenceErrorOccured"));
			return null;
		}
	}

	public String destroy() {
		current = (TruckServiceClass) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem()
				+ getItems().getRowIndex();
		performDestroy();
		recreatePagination();
		recreateModel();
		return "List";
	}

	public String destroyAndView() {
		performDestroy();
		recreateModel();
		updateCurrentItem();
		if (selectedItemIndex >= 0) {
			return "View";
		} else {
			// all items were removed - go back to list
			recreateModel();
			return "List";
		}
	}

	private void performDestroy() {
		try {
			getFacade().remove(current);
			addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString(
					"TruckServiceClassDeleted"));
		} catch (Exception e) {
			addErrorMessage(
					e,
					ResourceBundle.getBundle("/Bundle").getString(
							"PersistenceErrorOccured"));
		}
	}

	private void updateCurrentItem() {
		int count = getFacade().count();
		if (selectedItemIndex >= count) {
			// selected index cannot be bigger than number of items:
			selectedItemIndex = count - 1;
			// go to previous page if last page disappeared:
			if (pagination.getPageFirstItem() >= count) {
				pagination.previousPage();
			}
		}
		if (selectedItemIndex >= 0) {
			current = getFacade().findRange(
					new int[] { selectedItemIndex, selectedItemIndex + 1 })
					.get(0);
		}
	}

	public DataModel getItems() {
		if (items == null)
			items = getPagination().createPageDataModel();
		return items;
	}

	private void recreateModel() {
		items = null;
	}

	private void recreatePagination() {
		pagination = null;
	}

	public String next() {
		getPagination().nextPage();
		recreateModel();
		return "List";
	}

	public String previous() {
		getPagination().previousPage();
		recreateModel();
		return "List";
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return getSelectItems(ejbFacade.findAll(), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return getSelectItems(ejbFacade.findAll(), true);
	}

	@FacesConverter(forClass = TruckServiceClass.class)
	public static class TruckServiceClassControllerConverter implements
			Converter {

		private static Logger logger = getLogger(TruckServiceClassControllerConverter.class);

		public Object getAsObject(FacesContext facesContext,
				UIComponent component, String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			TruckServiceClassFacade ejbFacade = null;
			try {
				ejbFacade = (TruckServiceClassFacade) new InitialContext()
						.lookup("java:global/greenarea-service/TruckServiceClassFacade");
			} catch (NamingException e) {
				logger.error("getAsObject", e);
			}
			return ejbFacade.find(getKey(value));
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
			if (object instanceof TruckServiceClass) {
				TruckServiceClass o = (TruckServiceClass) object;
				return getStringKey(o.getId());
			} else {
				throw new IllegalArgumentException("object " + object
						+ " is of type " + object.getClass().getName()
						+ "; expected type: "
						+ TruckServiceClass.class.getName());
			}
		}
	}
}
