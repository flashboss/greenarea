package it.vige.greenarea.bpm.custom.ui.admin.data;

import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_ADMIN_HOME_DEFINIZIONE_FILTRO;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_ADMIN_HOME_FILTRI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_ADMIN_HOME_VISUALIZZAZIONE_FILTRI;
import static java.util.Arrays.asList;
import static org.activiti.explorer.ExplorerApp.get;
import it.vige.greenarea.bpm.custom.ui.data.Page;
import it.vige.greenarea.bpm.custom.ui.data.PageListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.activiti.explorer.I18nManager;
import org.activiti.explorer.data.AbstractLazyLoadingQuery;

import com.vaadin.data.Item;

public class AdminHomeListQuery extends AbstractLazyLoadingQuery {

	private I18nManager i18nManager = get().getI18nManager();

	private static final long serialVersionUID = -5121714812512928958L;
	private Page definizioneFiltro = new Page("0-0",
			i18nManager.getMessage(MAIN_MENU_ADMIN_HOME_DEFINIZIONE_FILTRO),
			null, false);
	private Page visualizzazioneFiltri = new Page(
			"0-1",
			i18nManager.getMessage(MAIN_MENU_ADMIN_HOME_VISUALIZZAZIONE_FILTRI),
			null, false);
	private Page filtri = new Page("0",
			i18nManager.getMessage(MAIN_MENU_ADMIN_HOME_FILTRI),
			new ArrayList<Page>(asList(new Page[] { definizioneFiltro,
					visualizzazioneFiltri })), false);

	private List<Item> items = new ArrayList<Item>(asList(new Item[] {
			new PageListItem(filtri), new PageListItem(definizioneFiltro),
			new PageListItem(visualizzazioneFiltri) }));

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public List<Item> loadItems(int start, int count) {
		return items;
	}

	@Override
	public Item loadSingleResult(String id) {
		return items.get(new Integer(id));
	}

	@Override
	public void setSorting(Object[] propertyIds, boolean[] ascending) {
		Collections.sort(items, new Comparator<Item>() {

			@Override
			public int compare(Item o1, Item o2) {
				return 0;
			}
		});
	}

}
