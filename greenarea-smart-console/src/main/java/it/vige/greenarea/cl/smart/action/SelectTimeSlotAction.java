/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.smart.action;

import it.vige.greenarea.cl.bean.TimeSlotInfo;
import it.vige.greenarea.cl.library.entities.TruckServiceClass;
import it.vige.greenarea.cl.smart.actionform.OptionTSForm;
import it.vige.greenarea.cl.smart.restclient.RestClient;
import it.vige.greenarea.dto.Sched;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 *
 * 
 */
public class SelectTimeSlotAction extends org.apache.struts.action.Action {

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

		OptionTSForm optForm = (OptionTSForm) form;
		String idTimeSlot = String.valueOf(optForm.getIdTimeSlot());
		RestClient rc = new RestClient();
		List<TruckServiceClass> liVe = rc.getAllTruckServiceClasses();
		TimeSlotInfo tsi = rc.getInfoTimeSlot(idTimeSlot);

		List<Sched> schedForSelectedTimeslot = rc.getSchedules(Integer
				.parseInt(idTimeSlot));
		request.getSession().setAttribute("liSche", schedForSelectedTimeslot);
		request.getSession().setAttribute("liScheSi",
				schedForSelectedTimeslot.size());

		request.getSession().setAttribute("liVe", liVe);
		request.getSession().setAttribute("sizeVe", liVe.size());
		request.getSession().setAttribute("tsi", tsi);
		request.getSession().setAttribute("liPar", tsi.getParInfoList());
		request.getSession().setAttribute("Size", tsi.getParInfoList().size());

		return mapping.findForward("step2");

	}
}
