/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

/**
 * 
 * @author Administrator
 */
@Entity
public class ShippingItem implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;

	@ManyToOne
	private ShippingOrder shippingOrder;
	private String description;
	@Lob
	private HashMap<String, String> attributes; // es fragile:si/no, stackable:
												// si/no, ...

	// TODO: inserire stato dell'item scorrelato da ordine

	public ShippingItem() {
	}

	public ShippingItem(String id) {
		this.id = id;
	}

	public ShippingItem(String id, String descrizione,
			HashMap<String, String> attributi) {
		this(id);
		this.description = descrizione;
		this.attributes = attributi;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}

	public ShippingOrder getShippingOrder() {
		return shippingOrder;
	}

	public void setShippingOrder(ShippingOrder shippingOrder) {
		this.shippingOrder = shippingOrder;
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
		if (!(object instanceof ShippingItem)) {
			return false;
		}
		ShippingItem other = (ShippingItem) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ShippingItem[ id=" + id + " ]: " + id + " - " + description
				+ "( " + getAttributes().toString() + ")";
	}
}
