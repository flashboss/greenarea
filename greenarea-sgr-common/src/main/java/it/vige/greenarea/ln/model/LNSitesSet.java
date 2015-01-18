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
package it.vige.greenarea.ln.model;

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.GeoLocationInterface;

import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LNSitesSet extends LNNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8470455342886119137L;

	public LNSitesSet(String name) {
		super.setName(name);
	}

	@Override
	public GeoLocation locate() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	protected HashSet<LNSite> sitesSet = new HashSet<LNSite>();

	public boolean addSite(LNSite lns) {
		if (lns == null)
			return false;
		return sitesSet.add(lns);
	}

	public boolean removeSite(LNSite lns) {
		if (lns == null)
			return false;
		return sitesSet.remove(lns);
	}

	public LNSite[] getSites() {
		LNSite[] result = new LNSite[sitesSet.size()];
		return sitesSet.toArray(result);
	}

	public LNSitesSet() {
		super();
		sitesSet = new HashSet<LNSite>();
	}

	@Override
	public void loadElement(Element el) {
		LNSite lns;
		Node n;
		super.loadElement(el);
		NodeList sites = el.getChildNodes();
		for (int i = 0; i < sites.getLength(); i++) {
			n = sites.item(i);
			if (n instanceof Element) {
				lns = new LNSite();
				lns.loadElement((Element) n);
				sitesSet.add(lns);
			}
		}
	}

	@Override
	public Element toElement() {
		Element nodeDescriptor = super.toElement();
		Document doc = ((Node) nodeDescriptor).getOwnerDocument();
		for (LNSite lns : sitesSet) {
			Node newNode = doc.importNode(lns.toElement(), true);
			nodeDescriptor.appendChild(newNode);
		}
		return nodeDescriptor;
	}

	@Override
	public boolean includes(GeoLocationInterface location) {
		boolean result = false;
		for (LNSite lns : sitesSet)
			result |= lns.includes(location);
		return result;
	}

}
