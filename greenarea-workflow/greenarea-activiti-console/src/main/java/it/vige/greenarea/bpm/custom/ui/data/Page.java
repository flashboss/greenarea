package it.vige.greenarea.bpm.custom.ui.data;

import java.util.List;

public class Page {
	private String id;
	private String name;
	private List<Page> children;
	private boolean hidden;

	public Page(String id, String name, List<Page> children, boolean hidden) {
		super();
		this.id = id;
		this.name = name;
		this.children = children;
		this.hidden = hidden;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Page> getChildren() {
		return children;
	}

	public void setChildren(List<Page> children) {
		this.children = children;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

}
