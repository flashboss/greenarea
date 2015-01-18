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
package it.vige.greenarea.cl.enforcement.action;

import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.enforcement.actionform.FindVehicleForm;
import it.vige.greenarea.cl.enforcement.rest.RestClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 *
 * 
 */
public class FindVehicleAction extends org.apache.struts.action.Action {

	/**
	 * This is the action called from the Struts framework.
	 * 
	 * @param mapping
	 *            The ActionMapping used to select this instance.
	 * @param form
	 *            The optional ActionForm bean for this request.
	 * @param request
	 *            The HTTP Request we are processing.
	 * @param response
	 *            The HTTP Response we are processing.
	 * @throws java.lang.Exception
	 * @return
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FindVehicleForm fvf = (FindVehicleForm) form;
		RestClient rc = new RestClient();
		String idTs = "1";
		Request req = rc.getInfoRequest(idTs, fvf.getIdVehicle());
		String status = "Non Autorizzato";
		request.setAttribute("req", req);
		if (req.getColor().equals("green") || req.getColor().equals("yellow"))
			status = "Autorizzato";
		request.setAttribute("status", status);
		return mapping.findForward("step1");
	}
}
