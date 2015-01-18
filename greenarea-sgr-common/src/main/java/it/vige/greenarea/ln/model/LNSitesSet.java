/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.ln.model;

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.GeoLocationInterface;

import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author 00917308
 */
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
