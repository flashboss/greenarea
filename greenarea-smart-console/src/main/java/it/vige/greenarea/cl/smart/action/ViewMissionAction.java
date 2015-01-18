/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.smart.action;

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.smart.actionform.ViewIdMissionForm;
import it.vige.greenarea.cl.smart.restclient.RestClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;

/**
 *
 * 
 */
public class ViewMissionAction extends org.apache.struts.action.Action {

	private Logger logger = getLogger(getClass());

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
		ViewIdMissionForm vimf = (ViewIdMissionForm) form;
		int idMission = vimf.getIdMission();
		RestClient rc = new RestClient();
		logger.info(idMission + "");
		String idM = String.valueOf(idMission);
		logger.info(idM);
		Request req = new Request();

		req = rc.getInfoRequest(idM);

		logger.info("Id Mission: " + req.getIdMission() + " Trovata");
		request.getSession().setAttribute("request", req);
		return mapping.findForward("view1");
	}
}
