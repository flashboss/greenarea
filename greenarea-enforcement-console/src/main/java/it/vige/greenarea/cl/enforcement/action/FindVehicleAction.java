/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
