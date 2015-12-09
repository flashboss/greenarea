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

import static it.vige.greenarea.Conversioni.convertTimestampToString;
import static it.vige.greenarea.dto.StatoMissione.WAITING;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.Transport;

@XmlType(name = "Mission")
@XmlAccessorType(XmlAccessType.FIELD)
public class MissionItem implements Serializable {

	private static final long serialVersionUID = -509613856432103711L;
	private Long id;
	@XmlElement(required = true)
	private String description;
	@XmlElement(required = true)
	private String start;
	private String expire;
	@XmlElement(name = "state")
	private int missionState;
	private TruckInfo truckInfo;
	@XmlElement(required = true, nillable = false)
	private List<ExchangeStopItem> exchangeStops;
	@XmlElement(required = true, nillable = false)
	private List<TransportItem> transports;
	@XmlElement(required = true, nillable = false)
	private Set<FreightItem> freights;

	public MissionItem() {
		this.id = new Long(0);
		this.description = "";
		this.truckInfo = new TruckInfo();
		this.exchangeStops = new ArrayList<ExchangeStopItem>();
		this.transports = new ArrayList<TransportItem>();
		this.freights = new HashSet<FreightItem>();
		this.missionState = WAITING.ordinal();
	}

	public MissionItem(Mission m) {
		this();
		this.id = m.getId();
		if ((m.getDescription() == null) || (m.getDescription().isEmpty())) {
			this.description = "Mission " + m.getId();
		} else {
			this.description = m.getDescription();
		}
		this.start = convertTimestampToString(m.getStartTime());
		this.expire = convertTimestampToString(m.getExpireTime());
		this.missionState = m.getMissionState().ordinal();
		this.truckInfo = new TruckInfo(m.getTruck());
		this.exchangeStops = new ArrayList<ExchangeStopItem>();
		this.freights = new HashSet<FreightItem>();
		for (ExchangeStop es : m.getExchangeStops()) {
			this.exchangeStops.add(new ExchangeStopItem(es));
			for (Freight f : es.getCollectingList()) {
				this.freights.add(new FreightItem(f));
			}
			for (Freight f : es.getDeliveryList()) {
				this.freights.add(new FreightItem(f));
			}
		}
		// TODO vedere come creare lista freight
		this.transports = new ArrayList<TransportItem>();
		for (Transport t : m.getTransports()) {
			this.transports.add(new TransportItem(t));
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getExpire() {
		return expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TruckInfo getTruckInfo() {
		return truckInfo;
	}

	public void setTruckInfo(TruckInfo truckInfo) {
		this.truckInfo = truckInfo;
	}

	public List<ExchangeStopItem> getExchangeStops() {
		return exchangeStops;
	}

	public void setExchangeStops(List<ExchangeStopItem> exchangeStops) {
		this.exchangeStops = exchangeStops;
	}

	public List<TransportItem> getTransports() {
		return transports;
	}

	public void setTransports(List<TransportItem> transports) {
		this.transports = transports;
	}

	public Set<FreightItem> getFreights() {
		return freights;
	}

	public void setFreights(Set<FreightItem> freights) {
		this.freights = freights;
	}

	public int getMissionState() {
		return missionState;
	}

	public void setMissionState(int missionState) {
		this.missionState = missionState;
	}
}
