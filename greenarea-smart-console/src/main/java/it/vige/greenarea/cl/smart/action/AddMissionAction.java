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

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.bean.ParameterInfo;
import it.vige.greenarea.cl.bean.TimeSlotInfo;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.smart.actionform.AddMissionForm;
import it.vige.greenarea.cl.smart.restclient.RestClient;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Veicolo;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class AddMissionAction extends org.apache.struts.action.Action {

	private Logger logger = getLogger(getClass());

	private DateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");

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
		AddMissionForm amf = (AddMissionForm) form;
		TimeSlotInfo tsi = (TimeSlotInfo) request.getSession().getAttribute(
				"tsi");
		RestClient rc = new RestClient();
		String[] dmyhm = amf.getDateMiss().split(" ");
		Timestamp startTime = new Timestamp(dateFormat.parse(dmyhm[0])
				.getTime());
		List<Parametro> parametri = new ArrayList<Parametro>();
		List<ParameterInfo> parameterInfos = tsi.getParInfoList();
		for (ParameterInfo parameterInfo : parameterInfos) {
			Parametro parametro = new Parametro(parameterInfo.getIdPg(),
					parameterInfo.getNamePG());
			parametri.add(parametro);
		}
		FasciaOraria fasciaOraria = new FasciaOraria(tsi.getIdTS(), parametri);
		Mission m = rc
				.buildCityLogisticsMission(new Missione(amf.getName(), amf.getCompany(), amf
						.getLunghezza(), amf.getCarico(), amf.getTappe(), amf
						.getEuro(), amf.getPeso(), null, null, new Veicolo(
						null, amf.getIdVehicle()), startTime, fasciaOraria));
		List<ValueMission> valuesMission = m.getValuesMission();
		m.setValuesMission(null);
		m = rc.addMission(m);
		for (ValueMission vm : valuesMission) {
			vm.setMission(m);
			rc.addValueMission(vm);
		}
		logger.info("Missione Creata con ID:  " + m.getId());
		request.getSession().setAttribute("mission", m);
		return mapping.findForward("step3");
	}
}
