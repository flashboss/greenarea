/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.smart.action;

import it.vige.greenarea.cl.library.entities.TruckServiceClass;
import it.vige.greenarea.cl.smart.actionform.FindVehicleForm;

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
public class FindVehicle extends org.apache.struts.action.Action {

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
		@SuppressWarnings("unchecked")
		List<TruckServiceClass> liVe = (List<TruckServiceClass>) request
				.getSession().getAttribute("liVe");
		TruckServiceClass ve = new TruckServiceClass();
		for (int i = 0; i < liVe.size(); i++) {
			if (liVe.get(i).getId().equals(fvf.getIdV())) {
				ve.setEURO(liVe.get(i).getEURO());
				ve.setEmissionV(liVe.get(i).getEmissionV());
				ve.setFuelV(liVe.get(i).getFuelV());
				ve.setLenghtV(liVe.get(i).getLenghtV());
				ve.setMakeV(liVe.get(i).getMakeV());
				ve.setModelV(liVe.get(i).getModelV());
				ve.setWeightV(liVe.get(i).getWeightV());
			}
		}
		request.setAttribute("ve", ve);
		return mapping.findForward("step2");
	}
}
