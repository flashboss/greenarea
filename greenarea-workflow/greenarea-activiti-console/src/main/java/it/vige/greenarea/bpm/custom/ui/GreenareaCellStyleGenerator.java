package it.vige.greenarea.bpm.custom.ui;

import it.vige.greenarea.bpm.custom.ui.data.PageListItem;

import org.activiti.explorer.data.LazyLoadingQuery;

import com.vaadin.ui.Table.CellStyleGenerator;

public class GreenareaCellStyleGenerator implements CellStyleGenerator {

	private static final long serialVersionUID = 5806192748054553116L;

	private LazyLoadingQuery lazyLoadingQuery;

	public GreenareaCellStyleGenerator(LazyLoadingQuery lazyLoadingQuery) {
		this.setLazyLoadingQuery(lazyLoadingQuery);
	}

	@Override
	public String getStyle(Object itemId, Object propertyId) {
		PageListItem pageItem = (PageListItem) lazyLoadingQuery
				.loadSingleResult(itemId + "");
		Boolean hidden = pageItem.isHidden();
		String result = "";
		if (hidden)
			result += "hidden-";
		int level = getLevel(pageItem);
		if (level == 0) {
			return result + "father";
		} else if (pageItem.isFather()) {
			if (pageItem.getPage().getChildren().get(0).isHidden())
				return result + "level-" + level + " v-table-row-closed";
			else
				return result + "level-" + level + " v-table-row-opened";
		} else {
			return result + "level-" + level;
		}
	}

	public LazyLoadingQuery getLazyLoadingQuery() {
		return lazyLoadingQuery;
	}

	public void setLazyLoadingQuery(LazyLoadingQuery lazyLoadingQuery) {
		this.lazyLoadingQuery = lazyLoadingQuery;
	}

	public static int getLevel(PageListItem pageItem) {
		String id = pageItem.getItemProperty("id").getValue() + "";
		return id.split("-").length - 1;
	}

}
