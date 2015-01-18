package it.vige.greenarea.bpm.custom.ui.home;

import static it.vige.greenarea.Constants.OPERATORE_LOGISTICO;
import static it.vige.greenarea.Constants.PA;
import static it.vige.greenarea.Constants.SOCIETA_DI_TRASPORTO;
import static it.vige.greenarea.Constants.TRASPORTATORE_AUTONOMO;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.navigation.TaskNavigator.CATEGORY_TASKS;
import static org.activiti.explorer.navigation.TaskNavigator.PARAMETER_CATEGORY;
import static org.activiti.explorer.navigation.TaskNavigator.TASK_URI_PART;
import it.vige.greenarea.bpm.UserConverter;
import it.vige.greenarea.bpm.custom.ui.admin.data.AdminHomeListQuery;
import it.vige.greenarea.bpm.custom.ui.operatorelogistico.data.OperatoreLogisticoHomeListQuery;
import it.vige.greenarea.bpm.custom.ui.pa.data.PAHomeListQuery;
import it.vige.greenarea.bpm.custom.ui.societaditrasporto.data.SocietaDiTrasportoHomeListQuery;
import it.vige.greenarea.bpm.custom.ui.trasportatoreautonomo.data.TrasportatoreAutonomoHomeListQuery;

import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.explorer.data.LazyLoadingQuery;
import org.activiti.explorer.navigation.UriFragment;

import com.vaadin.ui.AbstractSelect;

/**
 * The page displaying all cases for the logged in user. A case == task where
 * the user is the owner.
 * 
 * @author Joram Barrez
 */
public class DetailHomePage extends HomePage {

	private static final long serialVersionUID = 1L;

	public DetailHomePage() {
	}

	/**
	 * Constructor called when page is accessed straight through the url, eg.
	 * /task/id=123
	 */
	public DetailHomePage(String id) {
		super(id);
	}

	@Override
	protected LazyLoadingQuery createLazyLoadingQuery() {
		if (get().getLoggedInUser().isAdmin())
			return new AdminHomeListQuery();
		List<Group> groups = get().getLoggedInUser().getGroups();
		UserConverter userConverter = new UserConverter();
		if (userConverter.isUserInGroup(groups, PA))
			return new PAHomeListQuery();
		else if (userConverter.isUserInGroup(groups, OPERATORE_LOGISTICO))
			return new OperatoreLogisticoHomeListQuery();
		else if (userConverter.isUserInGroup(groups, SOCIETA_DI_TRASPORTO))
			return new SocietaDiTrasportoHomeListQuery();
		else if (userConverter.isUserInGroup(groups, TRASPORTATORE_AUTONOMO))
			return new TrasportatoreAutonomoHomeListQuery();
		else
			return new PAHomeListQuery();
	}

	@Override
	protected UriFragment getUriFragment(String id) {
		UriFragment itemFragment = new UriFragment(TASK_URI_PART);

		if (id != null) {
			itemFragment.addUriPart(id);
		}

		itemFragment.addParameter(PARAMETER_CATEGORY, CATEGORY_TASKS);
		return itemFragment;
	}

	@Override
	protected void addSelectComponent() {
		AbstractSelect select = createSelectComponent();
		if (select != null) {
			grid.addComponent(select, 0, 2);
		}
	}

}
