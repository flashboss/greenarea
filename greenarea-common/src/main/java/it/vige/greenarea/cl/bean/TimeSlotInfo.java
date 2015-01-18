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
package it.vige.greenarea.cl.bean;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeSlotInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8439181070144105824L;
	@XmlElement
	private Integer idTS;
	@XmlElement
	private int timeToAcceptRequest;
	@XmlElement
	private int timeToStopRequest;
	@XmlElement
	private int timeToRun;
	@XmlElement
	private int tollerance;
	@XmlElement
	private String wmy; // Stringa per giorni/settimana/mesi
	@XmlElement
	private String startTS;
	@XmlElement
	private String finishTS;
	@XmlElement
	private String dayStart;
	@XmlElement
	private String dayFinish;
	@XmlElement
	private ArrayList<ParameterInfo> parInfoList;

	public TimeSlotInfo() {
		this.parInfoList = new ArrayList<ParameterInfo>();
	}

	public String getDayFinish() {
		return dayFinish;
	}

	public void setDayFinish(String dayFinish) {
		this.dayFinish = dayFinish;
	}

	public String getDayStart() {
		return dayStart;
	}

	public void setDayStart(String dayStart) {
		this.dayStart = dayStart;
	}

	public String getFinishTS() {
		return finishTS;
	}

	public void setFinishTS(String finishTS) {
		this.finishTS = finishTS;
	}

	public Integer getIdTS() {
		return idTS;
	}

	public void setIdTS(Integer idTS) {
		this.idTS = idTS;
	}

	public ArrayList<ParameterInfo> getParInfoList() {
		return parInfoList;
	}

	public void setParInfoList(ArrayList<ParameterInfo> parInfoList) {
		this.parInfoList = parInfoList;
	}

	public String getStartTS() {
		return startTS;
	}

	public void setStartTS(String startTS) {
		this.startTS = startTS;
	}

	public int getTimeToAcceptRequest() {
		return timeToAcceptRequest;
	}

	public void setTimeToAcceptRequest(int timeToAcceptRequest) {
		this.timeToAcceptRequest = timeToAcceptRequest;
	}

	public int getTimeToRun() {
		return timeToRun;
	}

	public void setTimeToRun(int timeToRun) {
		this.timeToRun = timeToRun;
	}

	public int getTimeToStopRequest() {
		return timeToStopRequest;
	}

	public void setTimeToStopRequest(int timeToStopRequest) {
		this.timeToStopRequest = timeToStopRequest;
	}

	public int getTollerance() {
		return tollerance;
	}

	public void setTollerance(int tollerance) {
		this.tollerance = tollerance;
	}

	public String getWmy() {
		return wmy;
	}

	public void setWmy(String wmy) {
		this.wmy = wmy;
	}

	public void addParameterInfo(ParameterInfo pi) {
		this.parInfoList.add(pi);
	}

}
