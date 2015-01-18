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

import static it.vige.greenarea.cl.library.entities.OrderStatus.StateValue.unknown;
import it.vige.greenarea.cl.library.entities.OrderStatus.StateValue;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class ShippingOrder implements Serializable {

	private static final long serialVersionUID = 2979884146600991158L;
	@Id
	private String id;
	private String codiceFiliale;
	private String roundCode;
	private Timestamp creationTimestamp;

	/*
	 * ordinante Riferimento alfanumerico relativo all'autorit?? (persona fisica
	 * o giuridica) che intende fruire del servizio e che, fisicamente o
	 * virtualmente, "firma" l'Ordine di Spedizione. In generale un "ordinante"
	 * ?? un "riferimento" esterno al sistema, nel senso che non ?? un dato
	 * trattato nell'ambito di SGAPL ma serve per identificare chi abbia
	 * richiesto una spedizione; in tal senso il fatto che sia il cliente del
	 * sito eCommerce ??, dal punto di vista del sistema, irrilevante. La
	 * WebApplication che implementa questa funzione dovr?? farsi carico di
	 * reperire il dettaglio dell'ordinante da un sistema esterno (es. il CRM)
	 * ed inserire il riferimento nell'Ordine di Spedizione; refContract Indica
	 * un riferimento esterno che identifica il contesto in cui viene ordinata
	 * la spedizione; nel caso di un trasporto effettuato per conto della PMI
	 * stessa, questo campo ?? nullo; oggetto ?? costituito da un insieme di
	 * colli, ciascuno univocamente identificabile da un apposito codice, che
	 * devono essere trasportati, con indicato tutto il dettaglio necessario.
	 * Occorre a tal proposito evidenziare che differenti procedure e controlli
	 * dovranno essere applicati nel caso dei diversi tipi di materiali
	 * trasportati, come ad esempio in riferimento a materiali fragili (vetri) o
	 * deperibili (latticini o farmaci) o pericolosi (chimici). La definizione
	 * di questo valore ?? da intendersi strutturata e complessa; punto di
	 * raccolta Indica il luogo in cui l'oggetto del trasporto sar?? preso in
	 * carico dal primo Vettore per iniziare il trasporto; si tenga presente che
	 * potrebbe essere necessario includere qui anche l'autorit?? che ?? deputata
	 * a consegnare l'oggetto al Vettore stesso; punto di consegna Indica il
	 * luogo in cui l'oggetto del trasporto sar?? consegnato dall'ultimo Vettore
	 * deputato al trasporto al destinatario finale; termini di consegna
	 * Contiene eventuali indicatori tecnici rilevanti ai fini del rispetto
	 * dello SLA concordato; note Campo a disposizione per eventuali
	 * osservazioni testuali; stato Campo che identifica la situazione
	 * dell'Ordine di Spedizione secondo il diagramma di stato riportato in
	 * Figura 3
	 */
	@ManyToOne
	private Customer customer;
	@OneToMany(mappedBy = "shippingOrder", orphanRemoval = true)
	private List<ShippingItem> shippingItems;
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
	private DBGeoLocation mittente;
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
	private DBGeoLocation destinatario;
	@Lob
	private HashMap<String, String> deliveryTerms; // es catena del
													// freddo:si/no,
													// deperibile:si/no,
													// priorita'....
	private String note;
	private StateValue orderStatus;
	private String cost; // contiene il costo del trasporto come restituito da
							// GAT
	private String trackingURL;
	@OneToOne
	private Transport transport;
	private String operatoreLogistico;

	public ShippingOrder() {
		this.shippingItems = new ArrayList<ShippingItem>();
		this.mittente = new DBGeoLocation();
		this.destinatario = new DBGeoLocation();
		this.deliveryTerms = new HashMap<String, String>();
		this.creationTimestamp = new Timestamp(
				new GregorianCalendar().getTimeInMillis());
		this.cost = null;
		this.customer = null;
		this.note = null;
		this.orderStatus = unknown;
		this.trackingURL = "";
	}

	public ShippingOrder(String id) {
		this();
		this.id = id;
	}

	public ShippingOrder(String id, Customer ordinante, DBGeoLocation mittente,
			DBGeoLocation destinatario,
			HashMap<String, String> terminiDiConsegna, String operatoreLogistico) {
		this(id);
		this.customer = ordinante;
		this.mittente = mittente;
		this.destinatario = destinatario;
		this.deliveryTerms = terminiDiConsegna;
		this.operatoreLogistico = operatoreLogistico;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Timestamp getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Timestamp creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public DBGeoLocation getMittente() {
		return mittente;
	}

	public void setMittente(DBGeoLocation mittente) {
		this.mittente = mittente;
	}

	public DBGeoLocation getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(DBGeoLocation destinatario) {
		this.destinatario = destinatario;
	}

	public HashMap<String, String> getDeliveryTerms() {
		return deliveryTerms;
	}

	public void setDeliveryTerms(HashMap<String, String> deliveryTerms) {
		this.deliveryTerms = deliveryTerms;
	}

	public StateValue getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(StateValue orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<ShippingItem> getShippingItems() {
		return shippingItems;
	}

	public void setShippingItems(List<ShippingItem> shippingItems) {
		this.shippingItems = shippingItems;
	}

	public Transport getTransport() {
		return transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getTrackingURL() {
		return trackingURL;
	}

	public void setTrackingURL(String trackingURL) {
		this.trackingURL = trackingURL;
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
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof ShippingOrder)) {
			return false;
		}
		ShippingOrder other = (ShippingOrder) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("ShippingOrderData :");

		sb.append("\n- shId: ").append(this.id);
		sb.append("\n- mittente: ").append(mittente.toString());
		sb.append("\n- destinatario: ").append(destinatario.toString());
		if (note != null)
			sb.append("\n- note: ").append(note.toString());
		if (orderStatus != null)
			sb.append("\n- orderStatus: ").append(orderStatus.toString());
		if (cost != null)
			sb.append("\n- cost: ").append(cost.toString());
		if (trackingURL != null)
			sb.append("\n- trackingURL: ").append(trackingURL.toString());
		return sb.toString();
	}
}