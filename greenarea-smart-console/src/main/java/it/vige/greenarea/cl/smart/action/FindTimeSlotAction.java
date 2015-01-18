/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.smart.action;

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.smart.restclient.RestClient;
import it.vige.greenarea.dto.Sched;

import java.util.List;

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
public class FindTimeSlotAction extends org.apache.struts.action.Action {

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

		RestClient rc = new RestClient();
		List<TimeSlot> liTiSlo = rc.findAllTimeSlots();
		if (liTiSlo == null) {
			logger.error("Lista vuota!!");
			return mapping.findForward("error");
		}
		List<Sched> liSche = rc.getAllSchedules();
		request.getSession().setAttribute("liTiSlo", liTiSlo);
		request.getSession().setAttribute("size", liTiSlo.size());
		request.getSession().setAttribute("liSche", liSche);
		request.getSession().setAttribute("liScheSi", liSche.size());

		return mapping.findForward("step1");
	}
}
