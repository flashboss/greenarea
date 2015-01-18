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
package it.vige.greenarea.bpm.custom.ui.dettaglio;

import static com.vaadin.ui.Table.COLUMN_HEADER_MODE_HIDDEN;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.navigation.TaskNavigator.CATEGORY_INBOX;
import static org.activiti.explorer.navigation.TaskNavigator.PARAMETER_CATEGORY;
import static org.activiti.explorer.navigation.TaskNavigator.TASK_URI_PART;
import static org.activiti.explorer.ui.Images.TASK_22;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_SCROLLABLE;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_TASK_LIST;
import it.vige.greenarea.bpm.custom.GreenareaViewManager;
import it.vige.greenarea.bpm.custom.ui.home.HomePage;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.explorer.data.LazyLoadingContainer;
import org.activiti.explorer.data.LazyLoadingQuery;
import org.activiti.explorer.navigation.UriFragment;
import org.activiti.explorer.ui.custom.ToolBar;
import org.activiti.explorer.ui.util.ThemeImageColumnGenerator;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * The page displaying all tasks currently in ones inbox.
 */
public class DettaglioPage extends HomePage {

	private static final long serialVersionUID = 1L;
	private Label mainTitle;

	protected transient TaskService taskService = getDefaultProcessEngine()
			.getTaskService();

	private String taskId;
	protected String processInstanceId;
	protected Table taskTable;
	protected LazyLoadingContainer taskListContainer;
	protected LazyLoadingQuery lazyLoadingQuery;

	/**
	 * Constructor called when page is accessed straight through the url, eg.
	 * /task/id=123
	 */
	public DettaglioPage(String processInstanceId, Label mainTitle) {
		super(processInstanceId);
		this.mainTitle = mainTitle;
	}

	@Override
	protected LazyLoadingQuery createLazyLoadingQuery() {
		return new DettaglioListQuery(id);
	}

	@Override
	protected UriFragment getUriFragment(String taskId) {
		UriFragment taskFragment = new UriFragment(TASK_URI_PART);

		if (taskId != null) {
			taskFragment.addUriPart(taskId);
		}

		taskFragment.addParameter(PARAMETER_CATEGORY, CATEGORY_INBOX);
		return taskFragment;
	}

	@Override
	protected void initUi() {
		addMainLayout();
		setSizeFull();
		addMenuBar();
		addSelectComponent();
		if (taskId == null) {
			selectElement(0);
		} else {
			int index = taskListContainer.getIndexForObjectId(taskId);
			selectElement(index);
		}

		if (taskListContainer.size() == 0) {
			get().setCurrentUriFragment(getUriFragment(null));
		}
	}

	@Override
	protected ToolBar createMenuBar() {
		return null;
	}

	@Override
	protected Table createList() {
		taskTable = new Table();
		taskTable.addStyleName(STYLE_TASK_LIST);
		taskTable.addStyleName(STYLE_SCROLLABLE);

		// Listener to change right panel when clicked on a task
		taskTable.addListener(getListSelectionListener());

		lazyLoadingQuery = createLazyLoadingQuery();
		taskListContainer = new LazyLoadingContainer(lazyLoadingQuery, 30);
		taskTable.setContainerDataSource(taskListContainer);

		// Create column header
		taskTable.addGeneratedColumn("icon", new ThemeImageColumnGenerator(
				TASK_22));
		taskTable.setColumnWidth("icon", 22);

		taskTable.addContainerProperty("name", String.class, null);
		taskTable.setColumnHeaderMode(COLUMN_HEADER_MODE_HIDDEN);

		return taskTable;
	}

	@Override
	protected ValueChangeListener getListSelectionListener() {
		return new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				Item item = taskTable.getItem(event.getProperty().getValue());

				if (item != null) {
					String id = (String) item.getItemProperty("id").getValue();
					setDetailComponent(createDetailComponent(id));

					UriFragment taskFragment = getUriFragment(id);
					get().setCurrentUriFragment(taskFragment);
				} else {
					// Nothing is selected
					setDetailComponent(null);
					get().setCurrentUriFragment(getUriFragment(null));
				}
			}
		};
	}

	protected Component createDetailComponent(String id) {
		Task task = taskService.createTaskQuery().taskId(id).singleResult();
		Component detailComponent = new DettaglioPanel(task, this);
		return detailComponent;
	}

	@Override
	public void refreshSelectNext() {
		Integer pageIndex = (Integer) table.getCurrentPageFirstItemId();
		Integer selectedIndex = (Integer) table.getValue();
		table.removeAllItems();

		// Remove all items
		table.getContainerDataSource().removeAllItems();

		// Try to select the next one in the list
		Integer max = table.getContainerDataSource().size();
		if (max != 0) {
			if (pageIndex > max) {
				pageIndex = max - 1;
			}
			if (selectedIndex > max) {
				selectedIndex = max - 1;
			}
			table.setCurrentPageFirstItemIndex(pageIndex);
			selectElement(selectedIndex);
		} else {
			table.setCurrentPageFirstItemIndex(0);
			((GreenareaViewManager) get().getViewManager()).showHomePage();
		}

		// Update the counts in the header
		addMenuBar();
	}

	public Label getMainTitle() {
		return mainTitle;
	}

	public void setMainTitle(Label mainTitle) {
		this.mainTitle = mainTitle;
	}

}
