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

import java.io.Serializable;
import java.util.List;
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

import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.sessions.ValueMissionFacade;
import it.vige.greenarea.gtg.db.demoData.jsf.util.PaginationHelper;
import it.vige.greenarea.gtg.db.facades.MissionFacade;

@Named("missionController")
@SessionScoped
public class MissionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3401449667975127719L;
	private Mission current;
	private DataModel items = null;
	@EJB
	private MissionFacade ejbFacade;
	@EJB
	private ValueMissionFacade ejbVFacade;
	private PaginationHelper pagination;
	private int selectedItemIndex;

	public MissionController() {
	}

	public Mission getSelected() {
		if (current == null) {
			current = new Mission();
			selectedItemIndex = -1;
		}
		return current;
	}

	private MissionFacade getFacade() {
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
					List<Mission> missions = getFacade()
							.findRange(new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() });
					if (missions != null)
						for (Mission mission : missions) {
							mission.setValuesMission(ejbVFacade.findAByMission(mission));
						}
					return new ListDataModel(missions);
				}
			};
		}
		return pagination;
	}

	public String prepareList() {
		recreateModel();
		return "/mission/List";
	}

	public String prepareView() {
		current = (Mission) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		return "View";
	}

	public String prepareCreate() {
		current = new Mission();
		selectedItemIndex = -1;
		return "Create";
	}

	public String create() {
		try {
			getFacade().create(current);
			addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("MissionCreated"));
			return prepareCreate();
		} catch (Exception e) {
			addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String prepareEdit() {
		current = (Mission) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		return "Edit";
	}

	public String update() {
		try {
			getFacade().edit(current);
			addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("MissionUpdated"));
			return "View";
		} catch (Exception e) {
			addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String destroy() {
		current = (Mission) getItems().getRowData();
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
			addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("MissionDeleted"));
		} catch (Exception e) {
			addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
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
		List<Mission> missions = ejbFacade.findAll();
		if (missions != null)
			for (Mission mission : missions) {
				mission.setValuesMission(ejbVFacade.findAByMission(mission));
			}
		return getSelectItems(missions, false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		List<Mission> missions = ejbFacade.findAll();
		if (missions != null)
			for (Mission mission : missions) {
				mission.setValuesMission(ejbVFacade.findAByMission(mission));
			}
		return getSelectItems(missions, true);
	}

	@FacesConverter(forClass = Mission.class)
	public static class MissionControllerConverter implements Converter {

		private static Logger logger = getLogger(MissionControllerConverter.class);

		public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			MissionFacade ejbFacade = null;
			ValueMissionFacade ejbVFacade = null;
			try {
				ejbFacade = (MissionFacade) new InitialContext().lookup("java:global/greenarea-service/MissionFacade");
				ejbVFacade = (ValueMissionFacade) new InitialContext()
						.lookup("java:global/greenarea-service/ValueMissionFacade");
			} catch (NamingException e) {
				logger.error("getAsObject", e);
			}
			Mission mission = ejbFacade.find(getKey(value));
			mission.setValuesMission(ejbVFacade.findAByMission(mission));
			return mission;
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
			if (object instanceof Mission) {
				Mission o = (Mission) object;
				return getStringKey(o.getId());
			} else {
				throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName()
						+ "; expected type: " + Mission.class.getName());
			}
		}
	}
}
