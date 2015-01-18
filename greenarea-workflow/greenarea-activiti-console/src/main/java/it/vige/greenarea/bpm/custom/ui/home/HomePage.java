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
package it.vige.greenarea.bpm.custom.ui.home;

import static com.vaadin.ui.Table.COLUMN_HEADER_MODE_HIDDEN;
import static com.vaadin.ui.themes.Reindeer.SPLITPANEL_SMALL;
import static it.vige.greenarea.Constants.ANONYMOUS;
import static it.vige.greenarea.Constants.OPERATORE_LOGISTICO;
import static it.vige.greenarea.Constants.PA;
import static it.vige.greenarea.Constants.SOCIETA_DI_TRASPORTO;
import static it.vige.greenarea.Constants.TRASPORTATORE_AUTONOMO;
import static it.vige.greenarea.bpm.custom.ui.GreenareaCellStyleGenerator.getLevel;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_MENU_LEFT;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_SCROLLABLE;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_TASK_LIST;
import it.vige.greenarea.bpm.UserConverter;
import it.vige.greenarea.bpm.custom.ui.LoginPanel;
import it.vige.greenarea.bpm.custom.ui.GreenareaCellStyleGenerator;
import it.vige.greenarea.bpm.custom.ui.admin.AdminDetailPanel;
import it.vige.greenarea.bpm.custom.ui.data.PageListItem;
import it.vige.greenarea.bpm.custom.ui.operatorelogistico.OperatoreLogisticoDetailPanel;
import it.vige.greenarea.bpm.custom.ui.operatorelogistico.data.OperatoreLogisticoTable;
import it.vige.greenarea.bpm.custom.ui.pa.PADetailPanel;
import it.vige.greenarea.bpm.custom.ui.pa.data.PATable;
import it.vige.greenarea.bpm.custom.ui.societaditrasporto.SocietaDiTrasportoDetailPanel;
import it.vige.greenarea.bpm.custom.ui.trasportatoreautonomo.TrasportatoreAutonomoDetailPanel;

import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.explorer.data.LazyLoadingContainer;
import org.activiti.explorer.data.LazyLoadingQuery;
import org.activiti.explorer.navigation.UriFragment;
import org.activiti.explorer.ui.AbstractTablePage;
import org.activiti.explorer.ui.custom.ToolBar;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Table;

/**
 * Abstract super class for all task pages (inbox, queued, archived, etc.),
 * Builds up the default UI: task list on the left, central panel and events on
 * the right.
 * 
 * @author Joram Barrez
 */
public abstract class HomePage extends AbstractTablePage {

	private static final long serialVersionUID = 1L;

	protected transient TaskService taskService;

	protected String id;
	protected Table menuTable;
	protected LazyLoadingContainer itemListContainer;
	protected LazyLoadingQuery lazyLoadingQuery;
	protected LoginPanel loginPanel;
	protected boolean loginPageEnabled;

	public HomePage() {
		taskService = getDefaultProcessEngine().getTaskService();
	}

	public HomePage(String id) {
		this();
		this.id = id;
	}

	@Override
	protected void initUi() {
		loginPageEnabled = getLoginComponent() != null;
		addMainLayout();
		setSizeFull();
		if (!loginPageEnabled) {
			addSelectComponent();
			if (id == null) {
				setDetailComponent(createDetailComponent(null));
			} else {
				int index = itemListContainer.getIndexForObjectId(id);
				selectElement(index);
				PageListItem item = (PageListItem) menuTable.getItem(index);
				item.openItem(lazyLoadingQuery.loadItems(0, 0));
				grid.removeComponent(0, 2);
				menuTable.refreshRowCache();
				grid.addComponent(menuTable, 0, 2);
			}

			if (itemListContainer.size() == 0) {
				get().setCurrentUriFragment(getUriFragment(null));
			}
		} else {
			addIntroComponent();
			addNewsComponent();
			addLoginComponent();
		}
	}

	protected void addIntroComponent() {
		grid.addComponent(getIntroComponent(), 0, 0, 0, 2);
	}

	protected Component getIntroComponent() {
		return new CustomLayout("intro");
	}

	protected void addNewsComponent() {
		grid.addComponent(getNewsComponent(), 1, 0, 1, 2);
	}

	protected Component getNewsComponent() {
		return new CustomLayout("news");
	}

	protected void addLoginComponent() {
		grid.addComponent(getLoginComponent(), 2, 0, 2, 2);
	}

	@Override
	protected void addMainLayout() {
		if (loginPageEnabled) {
			grid = new GridLayout(3, 3);
			grid.setColumnExpandRatio(0, .25f);
			grid.setColumnExpandRatio(1, .52f);
			grid.setColumnExpandRatio(2, .23f);
		} else {
			grid = new GridLayout(2, 3);
			grid.setColumnExpandRatio(0, .18f);
			grid.setColumnExpandRatio(1, .82f);
		}

		grid.addStyleName(SPLITPANEL_SMALL);
		grid.setSizeFull();

		// Height division
		grid.setRowExpandRatio(2, 1.0f);

		setCompositionRoot(grid);
	}

	@Override
	protected ToolBar createMenuBar() {
		return null;
	}

	@Override
	protected Table createList() {
		List<Group> groups = get().getLoggedInUser().getGroups();
		UserConverter userConverter = new UserConverter();
		if (userConverter.isUserInGroup(groups, PA)) {
			menuTable = new PATable();
		} else if (userConverter.isUserInGroup(groups, OPERATORE_LOGISTICO)) {
			menuTable = new OperatoreLogisticoTable();
		} else
			menuTable = new Table();
		menuTable.addStyleName(STYLE_TASK_LIST);
		menuTable.addStyleName(STYLE_SCROLLABLE);
		menuTable.addStyleName(STYLE_MENU_LEFT);

		// Listener to change right panel when clicked on a task
		menuTable.addListener(getListSelectionListener());
		menuTable.addListener(getItemClickListener());

		lazyLoadingQuery = createLazyLoadingQuery();
		menuTable.setCellStyleGenerator(new GreenareaCellStyleGenerator(
				lazyLoadingQuery));
		itemListContainer = new LazyLoadingContainer(lazyLoadingQuery, 30);
		menuTable.setContainerDataSource(itemListContainer);

		menuTable.addContainerProperty("name", String.class, null);
		menuTable.setColumnHeaderMode(COLUMN_HEADER_MODE_HIDDEN);

		return menuTable;
	}

	protected void select(PageListItem item) {
		if (item != null) {
			if (item.isFather() || getLevel(item) == 0) {
				item.splitChildren(lazyLoadingQuery.loadItems(0, 0));
				grid.removeComponent(0, 2);
				menuTable.refreshRowCache();
				grid.addComponent(menuTable, 0, 2);
			} else {
				String id = (String) item.getItemProperty("id").getValue();
				setDetailComponent(createDetailComponent(item));

				UriFragment itemFragment = getUriFragment(id);
				get().setCurrentUriFragment(itemFragment);
			}
		} else {
			// Nothing is selected
			setDetailComponent(null);
			get().setCurrentUriFragment(getUriFragment(null));
		}

	}

	protected ItemClickListener getItemClickListener() {
		return new ItemClickListener() {
			private static final long serialVersionUID = 1L;

			public void itemClick(ItemClickEvent event) {
				PageListItem item = (PageListItem) menuTable.getItem(event
						.getItemId());
				if (item.isFather() && getLevel(item) != 0)
					select(item);
				updateCounter();
			}
		};
	}

	protected ValueChangeListener getListSelectionListener() {
		return new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				PageListItem item = (PageListItem) menuTable.getItem(event
						.getProperty().getValue());
				if (!item.isFather() || getLevel(item) == 0)
					select(item);
				updateCounter();
			}
		};
	}

	protected Component getLoginComponent() {
		if (get().getLoggedInUser().getId().equals(ANONYMOUS))
			return getLoginPanel();
		else
			return null;
	}

	protected Component createDetailComponent(Item item) {
		if (get().getLoggedInUser().isAdmin())
			return new AdminDetailPanel(item, this);
		List<Group> groups = get().getLoggedInUser().getGroups();
		UserConverter userConverter = new UserConverter();
		if (userConverter.isUserInGroup(groups, PA))
			return new PADetailPanel(item, this);
		else if (userConverter.isUserInGroup(groups, OPERATORE_LOGISTICO))
			return new OperatoreLogisticoDetailPanel(item, this);
		else if (userConverter.isUserInGroup(groups, SOCIETA_DI_TRASPORTO))
			return new SocietaDiTrasportoDetailPanel(item, this);
		else if (userConverter.isUserInGroup(groups, TRASPORTATORE_AUTONOMO))
			return new TrasportatoreAutonomoDetailPanel(item, this);
		else
			return new PADetailPanel(item, this);
	}

	public LoginPanel getLoginPanel() {
		if (loginPanel == null) {
			loginPanel = new LoginPanel();
		}
		return loginPanel;
	}

	@Override
	public void refreshSelectNext() {

		// Selects new element in the table
		super.refreshSelectNext();

		// Update the counts in the header
		addMenuBar();
	}

	protected abstract LazyLoadingQuery createLazyLoadingQuery();

	protected abstract UriFragment getUriFragment(String id);

	public void updateCounter() {
		if (menuTable instanceof PATable) {
			PATable paTable = (PATable) menuTable;
			paTable.setColumnFooter("name", paTable.counter());
		} else if (menuTable instanceof OperatoreLogisticoTable) {
			OperatoreLogisticoTable operatoreLogisticoTable = (OperatoreLogisticoTable) menuTable;
			operatoreLogisticoTable.setColumnFooter("name",
					operatoreLogisticoTable.counter());
		}
	}

}
