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

import static org.slf4j.LoggerFactory.getLogger;

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

import it.vige.greenarea.gtg.db.demoData.jsf.util.JsfUtil;
import it.vige.greenarea.gtg.db.demoData.jsf.util.PaginationHelper;
import it.vige.greenarea.gtg.db.entities.SystemVar;
import it.vige.greenarea.gtg.db.facades.SystemVarFacade;

@Named("systemVarController")
@SessionScoped
public class SystemVarController implements Serializable {

	private static final long serialVersionUID = 468704927810187750L;
	private SystemVar current;
	private DataModel items = null;
	@EJB
	private SystemVarFacade ejbFacade;
	private PaginationHelper pagination;
	private int selectedItemIndex;

	public SystemVarController() {
	}

	public SystemVar getSelected() {
		if (current == null) {
			current = new SystemVar();
			selectedItemIndex = -1;
		}
		return current;
	}

	private SystemVarFacade getFacade() {
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
					return new ListDataModel(getFacade()
							.findRange(new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }));
				}
			};
		}
		return pagination;
	}

	public String prepareList() {
		recreateModel();
		return "List";
	}

	public String prepareView() {
		current = (SystemVar) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		return "View";
	}

	public String prepareCreate() {
		current = new SystemVar();
		selectedItemIndex = -1;
		return "Create";
	}

	public String create() {
		try {
			getFacade().create(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SystemVarCreated"));
			return prepareCreate();
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String prepareEdit() {
		current = (SystemVar) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		return "Edit";
	}

	public String update() {
		try {
			getFacade().edit(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SystemVarUpdated"));
			return "View";
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String destroy() {
		current = (SystemVar) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
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
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SystemVarDeleted"));
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
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
			current = getFacade().findRange(new int[] { selectedItemIndex, selectedItemIndex + 1 }).get(0);
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
		return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
	}

	@FacesConverter(forClass = SystemVar.class)
	public static class SystemVarControllerConverter implements Converter {

		private static Logger logger = getLogger(SystemVarControllerConverter.class);

		public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			SystemVarFacade ejbFacade = null;
			try {
				ejbFacade = (SystemVarFacade) new InitialContext()
						.lookup("java:global/greenarea-service/SystemVarFacade");
			} catch (NamingException e) {
				logger.error("getAsObject", e);
			}
			return ejbFacade.find(getKey(value));
		}

		java.lang.String getKey(String value) {
			java.lang.String key;
			key = value;
			return key;
		}

		String getStringKey(java.lang.String value) {
			StringBuffer sb = new StringBuffer();
			sb.append(value);
			return sb.toString();
		}

		public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
			if (object == null) {
				return null;
			}
			if (object instanceof SystemVar) {
				SystemVar o = (SystemVar) object;
				return getStringKey(o.getVarName());
			} else {
				throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName()
						+ "; expected type: " + SystemVar.class.getName());
			}
		}
	}
}
