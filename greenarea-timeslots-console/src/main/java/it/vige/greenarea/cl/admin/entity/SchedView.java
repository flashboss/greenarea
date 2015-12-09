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
package it.vige.greenarea.cl.admin.entity;

import java.io.Serializable;

public class SchedView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6401148935016758137L;
	private Integer idSc;
	private int idTimeSlot;

	private String timeSlot;

	private String timeSlotData;
	private String timeSlotOra;

	private String timeAcceptData;
	private String timeAcceptOra;

	private String timeClosingData;
	private String timeClosingOra;

	private String timeRankData;
	private String timeRankOra;

	private int request;

	public SchedView() {
	}

	public Integer getIdSc() {
		return idSc;
	}

	public void setIdSc(Integer idSc) {
		this.idSc = idSc;
	}

	public int getIdTimeSlot() {
		return idTimeSlot;
	}

	public void setIdTimeSlot(int idTimeSlot) {
		this.idTimeSlot = idTimeSlot;
	}

	public int getRequest() {
		return request;
	}

	public void setRequest(int request) {
		this.request = request;
	}

	public String getTimeAcceptData() {
		return timeAcceptData;
	}

	public void setTimeAcceptData(String timeAcceptData) {
		this.timeAcceptData = timeAcceptData;
	}

	public String getTimeAcceptOra() {
		return timeAcceptOra;
	}

	public void setTimeAcceptOra(String timeAcceptOra) {
		this.timeAcceptOra = timeAcceptOra;
	}

	public String getTimeClosingData() {
		return timeClosingData;
	}

	public void setTimeClosingData(String timeClosingData) {
		this.timeClosingData = timeClosingData;
	}

	public String getTimeClosingOra() {
		return timeClosingOra;
	}

	public void setTimeClosingOra(String timeClosingOra) {
		this.timeClosingOra = timeClosingOra;
	}

	public String getTimeRankData() {
		return timeRankData;
	}

	public void setTimeRankData(String timeRankData) {
		this.timeRankData = timeRankData;
	}

	public String getTimeRankOra() {
		return timeRankOra;
	}

	public void setTimeRankOra(String timeRankOra) {
		this.timeRankOra = timeRankOra;
	}

	public String getTimeSlotData() {
		return timeSlotData;
	}

	public void setTimeSlotData(String timeSlotData) {
		this.timeSlotData = timeSlotData;
	}

	public String getTimeSlotOra() {
		return timeSlotOra;
	}

	public void setTimeSlotOra(String timeSlotOra) {
		this.timeSlotOra = timeSlotOra;
	}

	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

}
