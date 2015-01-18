/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.webservice;

import it.vige.greenarea.gtg.ejb.MissionHandlerBean;
import it.vige.greenarea.gtg.webservice.auth.LDAPauth;
import it.vige.greenarea.gtg.webservice.exceptions.GTGexception;
import it.vige.greenarea.gtg.webservice.wsdata.FreightItemAction;
import it.vige.greenarea.gtg.webservice.wsdata.MissionItem;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import com.unboundid.ldap.sdk.LDAPException;

/**
 * 
 * @author 00917377
 */
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
		LDAPauth.doAuthentication(wsContext);
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "getMissions")
	public List<MissionItem> getMissions(
			@WebParam(name = "dateTime") String dateTime) throws LDAPException {

		String user = LDAPauth.doAuthentication(wsContext);

		return missionHandlerBean.getMissions(dateTime, user);
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "changeMissionState")
	public void changeMissionState(
			@WebParam(name = "missionId") Long missionId,
			@WebParam(name = "state") int state,
			@WebParam(name = "cause") int cause,
			@WebParam(name = "note") String note,
			@WebParam(name = "dateTime") String dateTime) throws LDAPException,
			GTGexception {

		String user = LDAPauth.doAuthentication(wsContext);

		missionHandlerBean.changeMissionState(user, missionId, state, cause,
				note, dateTime);
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "notifyFreightItemAction")
	public void notifyFreightItemAction(
			@WebParam(name = "freightItemsAction") List<FreightItemAction> freightItemsAction)
			throws LDAPException, GTGexception {

		String user = LDAPauth.doAuthentication(wsContext);
		try {
			missionHandlerBean.notifyFreightItemActions(user,
					freightItemsAction);
		} catch (Exception ex) {
			ex.getMessage();
		}
	}
}
