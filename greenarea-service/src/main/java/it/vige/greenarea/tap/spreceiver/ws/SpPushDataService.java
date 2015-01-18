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
package it.vige.greenarea.tap.spreceiver.ws;

import static it.vige.greenarea.gtg.constants.ConversioniGTG.convertiOutDataToTapOutData;
import static it.vige.greenarea.tap.spreceiver.ws.ResultStatus.KO;
import static it.vige.greenarea.tap.spreceiver.ws.ResultStatus.OK;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.TapGroupData;
import it.vige.greenarea.cl.library.entities.TapOutData;
import it.vige.greenarea.cl.library.entities.TapParamData;
import it.vige.greenarea.tap.facades.TapGroupDataFacade;
import it.vige.greenarea.tap.facades.TapOutDataFacade;
import it.vige.greenarea.tap.facades.TapParamDataFacade;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.slf4j.Logger;

@WebService(name = "spPushDataService", targetNamespace = "http://tap.vige.it/spReceiver/ws")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({ ObjectFactory.class })
public class SpPushDataService {

	private Logger logger = getLogger(getClass());

	@EJB
	private TapOutDataFacade tapOutDataFacade;
	@EJB
	private TapParamDataFacade tapParamDataFacade;
	@EJB
	private TapGroupDataFacade tapGroupDataFacade;

	/**
	 * 
	 * @param parameters
	 * @return returns it.vige.tap.spreceiver.ws.AckData
	 */
	@WebMethod(action = "pushData")
	@WebResult(name = "pushResponse", targetNamespace = "http://tap.vige.it/spReceiver/ws", partName = "parameters")
	public AckData pushData(
			@WebParam(name = "outData", targetNamespace = "http://tap.vige.it/spReceiver/ws", partName = "parameters") OutData parameters) {
		AckData ackData = new AckData();
		try {
			TapOutData tapOutData = convertiOutDataToTapOutData(parameters);
			List<TapGroupData> tapGroupDatas = tapOutData.getGroups();
			if (tapGroupDatas != null) {
				for (TapGroupData tapGroupData : tapGroupDatas) {
					List<TapParamData> tapParamDatas = tapGroupData.getParams();
					if (tapParamDatas != null) {
						for (TapParamData tapParamData : tapParamDatas) {
							tapParamDataFacade.create(tapParamData);
							logCreateTapParamData(tapParamData, tapParamData
									+ "");
						}
					}
				}
				for (TapGroupData tapGroupData : tapGroupDatas) {
					tapGroupDataFacade.create(tapGroupData);
					logCreateTapGroupData(tapGroupData, tapGroupData + "");
					List<TapParamData> tapParamDatas = tapGroupData.getParams();
					if (tapParamDatas != null) {
						for (TapParamData tapParamData : tapParamDatas) {
							tapParamData.setTapGroupData(tapGroupData);
							tapParamDataFacade.edit(tapParamData);
							logEditTapParamData(tapParamData,
									tapParamData + "", tapGroupData + "");
						}
					}
				}
			}
			tapOutDataFacade.create(tapOutData);
			logCreateTapOutData(tapOutData, tapOutData + "");
			if (tapGroupDatas != null) {
				for (TapGroupData tapGroupData : tapGroupDatas) {
					tapGroupData.setTapOutData(tapOutData);
					tapGroupDataFacade.edit(tapGroupData);
					logEditTapGroupData(tapGroupData, tapGroupData + "",
							tapOutData + "");
				}
			}
		} catch (Exception ex) {
			ackData.setAck(KO);
			logger.error("errore ws ", ex);
		}
		ackData.setAck(OK);
		return ackData;
	}

	/**
	 * 
	 * @return returns it.vige.tap.spreceiver.ws.KeepAliveResponse
	 */
	@WebMethod(action = "keepAlive")
	@WebResult(name = "keepAliveResponse", targetNamespace = "http://tap.vige.it/spReceiver/ws", partName = "parameters")
	public KeepAliveResponse keepAlive() {
		KeepAliveResponse keepAliveResponse = new KeepAliveResponse();
		keepAliveResponse.setServiceProvider("prova");
		keepAliveResponse.setStatus(OK);
		return keepAliveResponse;
	}

	private void logCreateTapParamData(TapParamData entity, String nome) {
		if (entity != null) {
			logger.debug("TapParamData tapParamData" + nome
					+ " = new TapParamData();");
			if (entity.getName() != null)
				logger.debug("tapParamData" + nome + ".setName(\""
						+ entity.getName() + "\");");
			if (entity.getValue() != null)
				logger.debug("tapParamData" + nome + ".setValue(\""
						+ entity.getValue() + "\");");
			logger.debug("tapParamDataFacade.create(tapParamData" + nome + ");");
		}
	}

	private void logEditTapParamData(TapParamData entity, String nomeParamData,
			String nomeGroupData) {
		if (entity != null) {
			logger.debug("tapParamData" + nomeParamData
					+ ".setTapGroupData(tapGroupData" + nomeGroupData + ");");
			logger.debug("tapParamDataFacade.edit(tapParamData" + nomeParamData
					+ ");");
		}
	}

	private void logCreateTapOutData(TapOutData entity, String nome) {
		if (entity != null) {
			logger.debug("TapOutData tapOutData" + nome
					+ " = new TapOutData();");
			if (entity.getCodeFunction() != null)
				logger.debug("tapOutData" + nome + ".setCodeFunction(\""
						+ entity.getCodeFunction() + "\");");
			if (entity.getServiceProvider() != null)
				logger.debug("tapOutData" + nome + ".setServiceProvider(\""
						+ entity.getServiceProvider() + "\");");
			if (entity.getVin() != null)
				logger.debug("tapOutData" + nome + ".setVin(\""
						+ entity.getVin() + "\");");
			if (entity.getDate() != null) {
				logger.debug("GregorianCalendar gregorianCalendar" + nome
						+ " = new GregorianCalendar();");
				logger.debug("gregorianCalendar" + nome + ".setTime(new Date("
						+ entity.getDate().getTime().getTime() + "L));");
				logger.debug("tapOutData" + nome + ".setDate(gregorianCalendar"
						+ nome + ");");
			}
			logger.debug("tapOutDataFacade.create(tapOutData" + nome + ");");
		}
	}

	private void logCreateTapGroupData(TapGroupData entity, String nome) {
		if (entity != null) {
			logger.debug("TapGroupData tapGroupData" + nome
					+ " = new TapGroupData();");
			if (entity.getName() != null)
				logger.debug("tapGroupData" + nome + ".setName(\""
						+ entity.getName() + "\");");
			logger.debug("tapGroupDataFacade.create(tapGroupData" + nome + ");");
		}
	}

	private void logEditTapGroupData(TapGroupData entity, String nomeGroupData,
			String nomeOutData) {
		if (entity != null) {
			logger.debug("tapGroupData" + nomeGroupData
					+ ".setTapOutData(tapOutData" + nomeOutData + ");");
			logger.debug("tapGroupDataFacade.edit(tapGroupData" + nomeGroupData
					+ ");");
		}
	}

}
