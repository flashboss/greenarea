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
package it.vige.greenarea.gtg.webservice;

import static it.vige.greenarea.gtg.webservice.auth.LDAPauth.doAuthentication;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import com.unboundid.ldap.sdk.LDAPException;

import it.vige.greenarea.gtg.ejb.MissionHandlerBean;
import it.vige.greenarea.gtg.webservice.exceptions.GTGexception;
import it.vige.greenarea.gtg.webservice.wsdata.FreightItemAction;
import it.vige.greenarea.gtg.webservice.wsdata.MissionItem;

@WebService(serviceName = "GTGservice")
public class GTGservice {
	@EJB
	private MissionHandlerBean missionHandlerBean;

	@Resource
	WebServiceContext wsContext;

	/**
	 * This is a sample web service operation
	 */
	@WebMethod(operationName = "hello")
	public String hello(@WebParam(name = "name") String txt) {
		return "Hello " + txt + " !";
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "userLogin")
	public void userLogin() throws LDAPException {
		// TODO write your implementation code here:
		doAuthentication(wsContext);
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "getMissions")
	public List<MissionItem> getMissions(@WebParam(name = "dateTime") String dateTime) throws LDAPException {

		String user = doAuthentication(wsContext);

		return missionHandlerBean.getMissions(dateTime, user);
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "changeMissionState")
	public void changeMissionState(@WebParam(name = "missionId") Long missionId, @WebParam(name = "state") int state,
			@WebParam(name = "cause") int cause, @WebParam(name = "note") String note,
			@WebParam(name = "dateTime") String dateTime) throws LDAPException, GTGexception {

		String user = doAuthentication(wsContext);

		missionHandlerBean.changeMissionState(user, missionId, state, cause, note, dateTime);
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "notifyFreightItemAction")
	public void notifyFreightItemAction(
			@WebParam(name = "freightItemsAction") List<FreightItemAction> freightItemsAction)
					throws LDAPException, GTGexception {

		String user = doAuthentication(wsContext);
		try {
			missionHandlerBean.notifyFreightItemActions(user, freightItemsAction);
		} catch (Exception ex) {
			ex.getMessage();
		}
	}
}
