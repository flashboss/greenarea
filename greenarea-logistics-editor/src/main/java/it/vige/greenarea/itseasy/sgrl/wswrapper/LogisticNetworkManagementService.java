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
package it.vige.greenarea.itseasy.sgrl.wswrapper;

import it.vige.greenarea.sgrl.webservices.LogisticNetworkManagement;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "LogisticNetworkManagement", targetNamespace = "http://webservices.sgrl.greenarea.vige.it/", wsdlLocation = "http://NBW72018044431.vige.local:8080/SGRL/LogNetUpload?wsdl")
public class LogisticNetworkManagementService extends Service {

	private static URL LOGISTICNETWORKMANAGEMENT_WSDL_LOCATION;
	private static WebServiceException LOGISTICNETWORKMANAGEMENT_EXCEPTION;
	private final static QName LOGISTICNETWORKMANAGEMENT_QNAME = new QName(
			"http://webservices.sgrl.greenarea.vige.it/",
			"LogisticNetworkManagement");

	public static void setRootUrl(String root) {
		URL url = null;
		WebServiceException e = null;
		try {
			url = new URL(root + "LogisticNetworkManagement?wsdl");
		} catch (MalformedURLException ex) {
			e = new WebServiceException(ex);
		}
		LOGISTICNETWORKMANAGEMENT_WSDL_LOCATION = url;
		LOGISTICNETWORKMANAGEMENT_EXCEPTION = e;
	}

	public LogisticNetworkManagementService() {
		super(__getWsdlLocation(), LOGISTICNETWORKMANAGEMENT_QNAME);
	}

	public LogisticNetworkManagementService(WebServiceFeature... features) {
		super(__getWsdlLocation(), LOGISTICNETWORKMANAGEMENT_QNAME, features);
	}

	public LogisticNetworkManagementService(URL wsdlLocation) {
		super(wsdlLocation, LOGISTICNETWORKMANAGEMENT_QNAME);
	}

	public LogisticNetworkManagementService(URL wsdlLocation,
			WebServiceFeature... features) {
		super(wsdlLocation, LOGISTICNETWORKMANAGEMENT_QNAME, features);
	}

	public LogisticNetworkManagementService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public LogisticNetworkManagementService(URL wsdlLocation,
			QName serviceName, WebServiceFeature... features) {
		super(wsdlLocation, serviceName, features);
	}

	/**
	 * 
	 * @return returns LogisticNetworkManagement
	 */
	@WebEndpoint(name = "LogisticNetworkManagementPort")
	public LogisticNetworkManagement getLogisticNetworkManagementPort() {
		return super.getPort(new QName(
				"http://webservices.sgrl.greenarea.vige.it/",
				"LogisticNetworkManagementPort"),
				LogisticNetworkManagement.class);
	}

	/**
	 * 
	 * @param features
	 *            A list of {@link javax.xml.ws.WebServiceFeature} to configure
	 *            on the proxy. Supported features not in the
	 *            <code>features</code> parameter will have their default
	 *            values.
	 * @return returns LogisticNetworkManagement
	 */
	@WebEndpoint(name = "LogisticNetworkManagementPort")
	public LogisticNetworkManagement getLogisticNetworkManagementPort(
			WebServiceFeature... features) {
		return super.getPort(new QName(
				"http://webservices.sgrl.greenarea.vige.it/",
				"LogisticNetworkManagementPort"),
				LogisticNetworkManagement.class, features);
	}

	private static URL __getWsdlLocation() {
		if (LOGISTICNETWORKMANAGEMENT_EXCEPTION != null) {
			throw LOGISTICNETWORKMANAGEMENT_EXCEPTION;
		}
		return LOGISTICNETWORKMANAGEMENT_WSDL_LOCATION;
	}

}
