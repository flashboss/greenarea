/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import static it.vige.greenarea.cl.library.entities.Transport.TransportState.waiting;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;
import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.Leg;
import it.vige.greenarea.dto.TipoRichiesta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author 00917308
 */
@Entity
@XmlRootElement
@XmlAccessorType(FIELD)
public class Transport implements Serializable {

	private static final long serialVersionUID = 1641689719180992793L;
	@Id
	@Column(name = "ALFACODE", nullable = false, length = 40)
	private String alfacode;
	private String codiceFiliale;
	private String roundCode;

	@Lob
	private GeoLocation source;
	@Lob
	private GeoLocation destination;
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "name", column = @Column(name = "src_name")),
			@AttributeOverride(name = "surname", column = @Column(name = "src_surname")),
			@AttributeOverride(name = "mobile", column = @Column(name = "src_mobile")),
			@AttributeOverride(name = "phone", column = @Column(name = "src_phone")),
			@AttributeOverride(name = "email", column = @Column(name = "src_email")),
			@AttributeOverride(name = "country", column = @Column(name = "src_country")),
			@AttributeOverride(name = "adminAreaLevel1", column = @Column(name = "src_adminAreaLevel1")),
			@AttributeOverride(name = "adminAreaLevel2", column = @Column(name = "src_adminAreaLevel2")),
			@AttributeOverride(name = "city", column = @Column(name = "src_city")),
			@AttributeOverride(name = "street", column = @Column(name = "src_street")),
			@AttributeOverride(name = "number", column = @Column(name = "src_number")),
			@AttributeOverride(name = "zipCode", column = @Column(name = "src_zipCode")),
			@AttributeOverride(name = "latitude", column = @Column(name = "src_latitude")),
			@AttributeOverride(name = "longitude", column = @Column(name = "src_longitude")),
			@AttributeOverride(name = "radius", column = @Column(name = "src_radius")) })
	private DBGeoLocation pickup;
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "name", column = @Column(name = "dst_name")),
			@AttributeOverride(name = "surname", column = @Column(name = "dst_surname")),
			@AttributeOverride(name = "mobile", column = @Column(name = "dst_mobile")),
			@AttributeOverride(name = "phone", column = @Column(name = "dst_phone")),
			@AttributeOverride(name = "email", column = @Column(name = "dst_email")),
			@AttributeOverride(name = "country", column = @Column(name = "dst_country")),
			@AttributeOverride(name = "adminAreaLevel1", column = @Column(name = "dst_adminAreaLevel1")),
			@AttributeOverride(name = "adminAreaLevel2", column = @Column(name = "dst_adminAreaLevel2")),
			@AttributeOverride(name = "city", column = @Column(name = "dst_city")),
			@AttributeOverride(name = "street", column = @Column(name = "dst_street")),
			@AttributeOverride(name = "number", column = @Column(name = "dst_number")),
			@AttributeOverride(name = "zipCode", column = @Column(name = "dst_zipCode")),
			@AttributeOverride(name = "latitude", column = @Column(name = "dst_latitude")),
			@AttributeOverride(name = "longitude", column = @Column(name = "dst_longitude")),
			@AttributeOverride(name = "radius", column = @Column(name = "dst_radius")) })
	private DBGeoLocation dropdown;
	@ManyToOne
	private Mission mission;
	@ManyToOne
	private TransportServiceClass serviceClass;
	@OneToMany(mappedBy = "transport", cascade = REMOVE)
	private List<Freight> freightItems;
	@ElementCollection(fetch = EAGER)
	private List<Attachment> attachments;
	@OneToOne(mappedBy = "transport", optional = false, cascade = REMOVE)
	private ShippingOrder shippingOrder;
	private double cost;
	private double totalVolume;
	@Lob
	private HashMap<String, String> attributes = new HashMap<String, String>();

	private TransportState transportState;
	@Lob
	private ArrayList<Leg> route = new ArrayList<Leg>();
	private int activeLegIndex;// indice dell'ultima leg il cui vector ha in
								// carico il trasporto

	// da city logistics
	@ManyToOne
	private TimeSlot timeSlot;
	private Date timeAccept;
	private Date timeClosing;
	private Date timeRank;
	private Date dateMiss;
	private TipoRichiesta tipo;
	private String operatoreLogistico;

	public Transport() {
		freightItems = new ArrayList<Freight>();
		attachments = new ArrayList<Attachment>();
	}

	public Transport(String alfacode) {
		this();
		this.alfacode = alfacode;
		this.transportState = waiting;
	}

	public GeoLocation getSource() {
		return source;
	}

	public void setSource(GeoLocation source) {
		this.source = source;
	}

	public GeoLocation getDestination() {
		return destination;
	}

	public void setDestination(GeoLocation destination) {
		this.destination = destination;
	}

	public DBGeoLocation getPickup() {
		return pickup;
	}

	public void setPickup(DBGeoLocation pickup) {
		this.pickup = pickup;
	}

	public DBGeoLocation getDropdown() {
		return dropdown;
	}

	public void setDropdown(DBGeoLocation dropdown) {
		this.dropdown = dropdown;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public TransportServiceClass getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(TransportServiceClass serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getAlfacode() {
		return alfacode;
	}

	public void setAlfacode(String alfacode) {
		this.alfacode = alfacode;
	}

	public List<Freight> getFreightItems() {
		return freightItems;
	}

	public void setFreightItems(List<Freight> freightItems) {
		this.freightItems = freightItems;
	}

	public Mission getMission() {
		return mission;
	}

	public void setMission(Mission mission) {
		this.mission = mission;
	}

	public TransportState getTransportState() {
		return transportState;
	}

	public void setTransportState(TransportState transportState) {
		this.transportState = transportState;
	}

	public double getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(double totalVolume) {
		this.totalVolume = totalVolume;
	}

	public ShippingOrder getShippingOrder() {
		return shippingOrder;
	}

	public void setShippingOrder(ShippingOrder shippingOrder) {
		this.shippingOrder = shippingOrder;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}

	public ArrayList<Leg> getRoute() {
		return route;
	}

	public void setRoute(ArrayList<Leg> route) {
		this.route = route;
	}

	public int getActiveLegIndex() {
		return activeLegIndex;
	}

	public void setActiveLegIndex(int activeLegIndex) {
		this.activeLegIndex = activeLegIndex;
	}

	public TimeSlot getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}

	public Date getTimeAccept() {
		return timeAccept;
	}

	public void setTimeAccept(Date timeAccept) {
		this.timeAccept = timeAccept;
	}

	public Date getTimeClosing() {
		return timeClosing;
	}

	public void setTimeClosing(Date timeClosing) {
		this.timeClosing = timeClosing;
	}

	public Date getTimeRank() {
		return timeRank;
	}

	public void setTimeRank(Date timeRank) {
		this.timeRank = timeRank;
	}

	public Date getDateMiss() {
		return dateMiss;
	}

	public void setDateMiss(Date dateMiss) {
		this.dateMiss = dateMiss;
	}

	public enum TransportState {

		waiting, assigned, on_delivery, aborted, incomplete, completed, rejected, unknown
	};

	public TipoRichiesta getTipo() {
		return tipo;
	}

	public void setTipo(TipoRichiesta tipo) {
		this.tipo = tipo;
	}

	public String getOperatoreLogistico() {
		return operatoreLogistico;
	}

	public void setOperatoreLogistico(String operatoreLogistico) {
		this.operatoreLogistico = operatoreLogistico;
	}
	
	public String getCodiceFiliale() {
		return codiceFiliale;
	}

	public void setCodiceFiliale(String codiceFiliale) {
		this.codiceFiliale = codiceFiliale;
	}

	public String getRoundCode() {
		return roundCode;
	}

	public void setRoundCode(String roundCode) {
		this.roundCode = roundCode;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (alfacode != null ? alfacode.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Transport)) {
			return false;
		}
		Transport other = (Transport) object;
		if ((this.alfacode == null && other.alfacode != null)
				|| (this.alfacode != null && !this.alfacode
						.equals(other.alfacode))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("[alfacode = ").append(this.alfacode);
		sb.append("] ");
		sb.append(" - cost:").append(this.cost);
		sb.append(" - totalVolume:").append(this.totalVolume);
		sb.append(" - status:").append(this.transportState);
		sb.append(" - class:").append(this.serviceClass);
		sb.append(" - from:").append(this.source);
		sb.append(" - to:").append(this.destination);

		return sb.toString();

	}
}
