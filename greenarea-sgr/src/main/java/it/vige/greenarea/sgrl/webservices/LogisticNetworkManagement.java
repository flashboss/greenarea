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
package it.vige.greenarea.sgrl.webservices;

import it.vige.greenarea.sgrl.LogisticNetworkManager;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "LogisticNetworkManagement")
public class LogisticNetworkManagement {

	@Inject
	private LogisticNetworkManager logisticNetworkManager;

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "upload")
	public String upload(@WebParam(name = "name") String name,
			@WebParam(name = "xmlGraph") String xmlGraph)
			throws UnsupportedEncodingException {
		logisticNetworkManager.useLN(LogisticNetworkManager.DEFAULT_NETWORK,
				xmlGraph);
		return null;
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "download")
	public String download(@WebParam(name = "name") String name) {
		return logisticNetworkManager
				.getLN(LogisticNetworkManager.DEFAULT_NETWORK);
	}
}
