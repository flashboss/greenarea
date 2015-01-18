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
