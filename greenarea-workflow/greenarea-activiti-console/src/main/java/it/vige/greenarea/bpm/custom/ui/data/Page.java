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
