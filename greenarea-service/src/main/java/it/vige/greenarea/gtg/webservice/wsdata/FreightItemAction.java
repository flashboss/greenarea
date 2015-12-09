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
package it.vige.greenarea.gtg.webservice.wsdata;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import it.vige.greenarea.cl.library.entities.FreightItemState;

@XmlType(name = "FreightAction")
@XmlAccessorType(XmlAccessType.FIELD)
public class FreightItemAction implements Serializable {

	private static final long serialVersionUID = -442395450963415478L;
	@XmlElement(required = true, name = "code")
	private String freightItemCode;
	private Integer cause;
	@XmlElement(required = true)
	private Long exchangeStopId;
	@XmlElement(required = true)
	private int state;
	@XmlElement(required = true)
	private String dateTime;
	private String note;

	// private Attachments attachment; eventuali dati multimedia

	public FreightItemAction() {
		this.freightItemCode = "";
		this.exchangeStopId = new Long(0);
		this.state = FreightItemState.AVAILABLE.ordinal();
		this.dateTime = "";
		this.note = "";
	}

	public FreightItemAction(String freightItemCode, Long exchangeStopId, int state, int cause, String dateTime,
			String note) {
		this.freightItemCode = freightItemCode;
		this.exchangeStopId = exchangeStopId;
		this.state = state;
		this.cause = cause;
		this.dateTime = dateTime;
		this.note = note;
	}

	public String getFreightItemCode() {
		return freightItemCode;
	}

	public void setFreightItemCode(String freightItemCode) {
		this.freightItemCode = freightItemCode;
	}

	public Integer getCause() {
		return cause;
	}

	public void setCause(Integer cause) {
		this.cause = cause;
	}

	public Long getExchangeStopId() {
		return exchangeStopId;
	}

	public void setExchangeStopId(Long exchangeStopId) {
		this.exchangeStopId = exchangeStopId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
