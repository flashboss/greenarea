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
package it.vige.greenarea.net;

import java.io.Serializable;

public abstract class Edge implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2975160431123197575L;
	protected boolean bidirectional = true;
	protected Node source = null;
	protected Node target = null;

	public boolean isBidirectional() {
		return bidirectional;
	}

	protected void setBidirectional(boolean bidirectional) {
		this.bidirectional = bidirectional;
	}

	public Node getSource() {
		return source;
	}

	protected Edge setSource(Node source) {
		this.source = source;
		return this;
	}

	public Node getTarget() {
		return target;
	}

	protected Edge setTarget(Node target) {
		this.target = target;
		return this;
	}

	public abstract double getCost();

}
