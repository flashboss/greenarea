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
package it.vige.greenarea.sgrl;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.w3c.dom.Document;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;

import it.vige.greenarea.db.entities.LNimg;
import it.vige.greenarea.db.facade.LNimgFacade;
import it.vige.greenarea.geo.GisService;
import it.vige.greenarea.geo.GisService.GeoCodingException;
import it.vige.greenarea.geo.GoogleGis;
import it.vige.greenarea.ln.model.LNCellCodec;
import it.vige.greenarea.ln.model.LNSite;
import it.vige.greenarea.ln.model.LNSitesSet;
import it.vige.greenarea.ln.model.LogisticNetwork;
import it.vige.greenarea.utilities.Application;
import it.vige.greenarea.utilities.LNutilities;

@Singleton
public class LogisticNetworkManager {

	private Logger logger = getLogger(getClass());

	@EJB
	private LNimgFacade lNimgFacade;

	public static final String DEFAULT_NETWORK = "defaultNetwork";

	private GisService gis = new GoogleGis(Locale.getDefault());

	public GisService getGisService() {
		return gis;
	}

	public void refreshAllAddresses() {
		HashSet<LNSite> s = new HashSet<LNSite>();
		LogisticNetwork ln = LNutilities.getLogisticNetwork();
		Object[] cells = ln.getChildCells(ln.getDefaultParent(), true, false);
		for (Object c : cells) {
			if (c instanceof mxCell) {
				Object node = ((mxCell) c).getValue();
				if (node instanceof LNSite)
					s.add((LNSite) node);
				else if (node instanceof LNSitesSet)
					for (LNSite lns : ((LNSitesSet) node).getSites())
						s.add(lns);
			}
		}
		for (LNSite lns : s) {
			String zip;
			logger.info(String.format("Refreshing Address: %s, %s, %s, %s, %s, %s, %s", lns.getNumber(),
					lns.getStreet(), lns.getCity(), lns.getAdminAreaLevel1(), lns.getAdminAreaLevel2(),
					lns.getZipCode(), lns.getCountry()));
			zip = lns.getZipCode();
			try {
				lns.setLocation(getGisService().geoCode(lns));
				lns.setZipCode(zip);
			} catch (GeoCodingException ex) {
				logger.warn("sgr service", ex);
			}
		}
	}

	public void saveLogisticNetwork(String name, LogisticNetwork ln) throws UnsupportedEncodingException {
		saveLogisticNetwork(name, _encode(ln));
	}

	public void saveLogisticNetwork(String name, String xml) {
		LNimg lnimg = lNimgFacade.find(name);
		if (lnimg == null) {
			lnimg = new LNimg();
			lnimg.setName(name);
			lnimg.setTimeStamp(GregorianCalendar.getInstance().getTimeInMillis());
			lnimg.setO(xml);
			lNimgFacade.create(lnimg);
		} else {
			lnimg.setO(xml);
			lNimgFacade.edit(lnimg);
		}
	}

	private String _encode(LogisticNetwork logisticNetwork) throws UnsupportedEncodingException {
		mxCodec codec = new mxCodec();
		return URLEncoder.encode(mxXmlUtils.getXml(codec.encode(logisticNetwork.getModel())), "UTF-8");
	}

	private LogisticNetwork _decode(String xmlGraph) throws UnsupportedEncodingException {
		Document document;
		document = mxXmlUtils.parseXml(URLDecoder.decode(xmlGraph, "UTF-8"));
		mxCodec codec = new mxCodec(document);
		mxIGraphModel m = (mxIGraphModel) codec.decode(document.getDocumentElement());
		if (m == null)
			return null;
		LogisticNetwork ln = new LogisticNetwork();
		ln.setModel(m);
		return ln;
	}

	public void useLN(String name, LogisticNetwork logisticNetwork) throws UnsupportedEncodingException {
		saveLogisticNetwork(name, logisticNetwork);
		LNutilities.setLogisticNetwork(logisticNetwork);
		refreshAllAddresses();
	}

	public void useLN(String name, String xmlGraph) throws UnsupportedEncodingException {
		saveLogisticNetwork(name, xmlGraph);
		LNutilities.setLogisticNetwork(_decode(xmlGraph));
		refreshAllAddresses();
	}

	@PostConstruct
	public void execute() {
		Locale.setDefault(
				new Locale(Application.getProperty("Locale.language"), Application.getProperty("Locale.country")));
		mxCodecRegistry.register(new LNCellCodec());
		try {
			useLN(DEFAULT_NETWORK);
		} catch (UnsupportedEncodingException ex) {
			logger.warn("sgr service", ex);
		}
	}

	public void useLN(String name) throws UnsupportedEncodingException {
		mxCodecRegistry.register(new LNCellCodec());
		LNimg lnimg = lNimgFacade.find(name);
		if (lnimg != null) {
			LNutilities.setLogisticNetwork(_decode(lnimg.getO()));
			refreshAllAddresses();
		}
	}

	public String getLN(String name) {
		if (name == null)
			return null;
		LNimg lnimg = lNimgFacade.find(name);
		if (lnimg != null)
			return lnimg.getO();
		return null;
	}

	public mxGraph getActiveNetwork() {
		return LNutilities.getLogisticNetwork();
	}
}
