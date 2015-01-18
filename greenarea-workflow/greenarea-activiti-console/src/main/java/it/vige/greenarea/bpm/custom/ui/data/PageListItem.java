/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.vige.greenarea.bpm.custom.ui.data;

import static it.vige.greenarea.bpm.custom.ui.GreenareaCellStyleGenerator.getLevel;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

/**
 * @author Joram Barrez
 */
public class PageListItem extends PropertysetItem implements
		Comparable<PageListItem> {

	private static final long serialVersionUID = 1L;
	private Page page;

	public PageListItem(Page page) {
		this.page = page;
		addItemProperty("id", new ObjectProperty<String>(page.getId(),
				String.class));
		addItemProperty("name", new ObjectProperty<String>(page.getName(),
				String.class));
	}

	public int compareTo(PageListItem other) {
		String taskId = (String) getItemProperty("id").getValue();
		String otherTaskId = (String) other.getItemProperty("id").getValue();
		return taskId.compareTo(otherTaskId);
	}

	public boolean isFather() {
		return page.getChildren() != null;
	}

	public boolean isHidden() {
		return page.isHidden();
	}

	public void setHidden(boolean hidden) {
		page.setHidden(hidden);
	}

	private void closeItems(List<Item> items) {
		for (Item item : items) {
			PageListItem it = (PageListItem) item;
			if (getLevel(it) != 0)
				it.setHidden(true);
		}
	}

	private void openItems(List<Page> items) {
		for (Page item : items) {
			item.setHidden(false);
			List<Page> pages = item.getChildren();
			if (pages != null)
				openItems(item.getChildren());
		}
	}

	private void recSplitChildren(List<Page> children, boolean hidden) {
		if (children != null)
			for (Page child : children) {
				child.setHidden(hidden);
				recSplitChildren(child.getChildren(), hidden);
			}
	}

	private PageListItem findFather(PageListItem child, List<Item> items,
			int level) {
		for (Item item : items) {
			PageListItem pli = (PageListItem) item;
			List<Page> children = pli.getPage().getChildren();
			if (children != null && children.contains(child.getPage()))
				if (getLevel(pli) == level)
					return (pli);
				else
					return findFather(pli, items, level);
		}
		return null;

	}

	public void openItem(List<Item> items) {
		closeItems(items);
		PageListItem father = findFather(this, items, 0);
		openItems(father.getPage().getChildren());
	}

	public void splitChildren(List<Item> items) {
		if (getLevel(this) == 0) {
			closeItems(items);
		}
		List<Page> children = getPage().getChildren();
		if (children != null)
			recSplitChildren(children, !children.get(0).isHidden());
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

}
