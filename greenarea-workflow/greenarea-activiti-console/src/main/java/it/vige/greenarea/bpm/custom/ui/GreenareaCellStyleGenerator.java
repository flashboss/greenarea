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
