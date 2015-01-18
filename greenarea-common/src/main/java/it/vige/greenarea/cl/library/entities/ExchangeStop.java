/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * 
 * @author 00917308
 */
@Entity
public class ExchangeStop implements Serializable {

	private static final long serialVersionUID = 7736708371883639516L;
	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;
	@OneToMany(mappedBy = "pickUpPoint")
	private List<Freight> collectingList;
	@OneToMany(mappedBy = "dropDownPoint")
	private List<Freight> deliveryList;
	@Column(name = "DRIVERNOTES", length = 200)
	private String driverNotes;
	@ElementCollection(fetch = EAGER)
	private List<Attachment> attachments;
	@Embedded
	private TruckLoadDescriptor truckLoad;
	@Embedded
	private DBGeoLocation location;

	public ExchangeStop() {
		location = new DBGeoLocation();
		collectingList = new ArrayList<Freight>();
		deliveryList = new ArrayList<Freight>();
		attachments = new ArrayList<Attachment>();
		truckLoad = new TruckLoadDescriptor();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<Freight> getCollectingList() {
		return collectingList;
	}

	public void setCollectingList(List<Freight> collectingList) {
		this.collectingList = collectingList;
	}

	public List<Freight> getDeliveryList() {
		return deliveryList;
	}

	public void setDeliveryList(List<Freight> deliveryList) {
		this.deliveryList = deliveryList;
	}

	public String getDriverNotes() {
		return driverNotes;
	}

	public void setDriverNotes(String driverNotes) {
		this.driverNotes = driverNotes;
	}

	public TruckLoadDescriptor getTruckLoad() {
		return truckLoad;
	}

	public void setTruckLoad(TruckLoadDescriptor truckLoad) {
		this.truckLoad = truckLoad;
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
		if (!(object instanceof ExchangeStop)) {
			return false;
		}
		ExchangeStop other = (ExchangeStop) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.getId() != null) {
			sb.append("[id = ").append(this.id).append("] ");
		}
		sb.append("<<load: ").append(this.getTruckLoad().getVolume());
		sb.append(">> ").append(this.location.toString());
		return sb.toString();
	}

	public DBGeoLocation getLocation() {
		return location;
	}

	public void setLocation(DBGeoLocation location) {
		this.location = location;
	}
}
