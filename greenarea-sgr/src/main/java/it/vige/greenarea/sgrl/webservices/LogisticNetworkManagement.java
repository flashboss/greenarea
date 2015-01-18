/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgrl.webservices;

import it.vige.greenarea.sgrl.LogisticNetworkManager;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * 
 * @author 00917308
 */
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
