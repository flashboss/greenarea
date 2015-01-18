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
package it.vige.greenarea.cl.library.entities;

import static it.vige.greenarea.dto.StatoMissione.WAITING;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;
import it.vige.greenarea.dto.StatoMissione;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Mission implements Serializable {

	private static final long serialVersionUID = -1267691015438392242L;
	public static final int TRUCKOVERLOAD = -1;
	public static final int COLOCATED = 20;
	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;
	private Timestamp startTime;
	private Timestamp expireTime;

	@OneToMany(cascade = ALL, fetch = EAGER)
	private List<ExchangeStop> exchangeStops;

	@OneToMany(mappedBy = "mission", fetch = EAGER)
	private List<Transport> transports;

	@ManyToOne
	private Vehicle truck;

	@ElementCollection(fetch = EAGER)
	private List<Attachment> attachments;

	private String description;
	private StatoMissione missionState;
	private String ownerUser;

	@XmlElement
	private String name;
	@XmlElement
	private String company;

	@ManyToOne
	private TimeSlot timeSlot;
	@XmlElement
	private String addressList;
	@XmlElement
	@OneToMany(mappedBy = "mission")
	private List<ValueMission> valuesMission;
	@XmlElement
	private double resVikor;

	public Mission() {
		exchangeStops = new ArrayList<ExchangeStop>();
		transports = new ArrayList<Transport>();
		attachments = new ArrayList<Attachment>();
		description = "";
		missionState = WAITING;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Timestamp expireTime) {
		this.expireTime = expireTime;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<ExchangeStop> getExchangeStops() {
		return exchangeStops;
	}

	public void setExchangeStops(List<ExchangeStop> exchangeStops) {
		this.exchangeStops = exchangeStops;
	}

	public Vehicle getTruck() {
		return truck;
	}

	public void setTruck(Vehicle truck) {
		this.truck = truck;
	}

	public List<Transport> getTransports() {
		return transports;
	}

	protected void setTransports(List<Transport> transports) {
		this.transports = transports;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public StatoMissione getMissionState() {
		return missionState;
	}

	public void setMissionState(StatoMissione missionState) {
		this.missionState = missionState;
	}

	public String getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(String ownerUser) {
		this.ownerUser = ownerUser;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public TimeSlot getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}

	public String getAddressList() {
		return addressList;
	}

	public void setAddressList(String addressList) {
		this.addressList = addressList;
	}

	public List<ValueMission> getValuesMission() {
		return valuesMission;
	}

	public void setValuesMission(List<ValueMission> valuesMission) {
		this.valuesMission = valuesMission;
	}

	public double getResVikor() {
		return resVikor;
	}

	public void setResVikor(double resVikor) {
		this.resVikor = resVikor;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Mission)) {
			return false;
		}
		Mission other = (Mission) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[id = ").append(id);
		sb.append("] ").append(description);
		sb.append(" - status: ").append(missionState.name());
		if (ownerUser != null) {
			sb.append(" - assigned to: ").append(ownerUser);
		}
		sb.append(" - num of trasports: ").append(transports.size());
		sb.append(" - stops: ");
		for (ExchangeStop es : exchangeStops) {
			sb.append("< ").append(es.toString()).append(">");
		}
		return sb.toString();
	}
}
