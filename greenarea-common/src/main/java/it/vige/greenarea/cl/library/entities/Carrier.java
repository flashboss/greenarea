/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import static javax.persistence.GenerationType.AUTO;

import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 
 * @author 00917377
 */
@Entity
public class Carrier {

	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;

	private String name;
	private String[] commDescriptor;
	private HashMap<String, String> properties;

	public Carrier() {
	}

	/*
	 * 
	 * 
	 * Costruttore basato sul dominio di default
	 */
	public Carrier(String name) {
		this.name = name;
		this.commDescriptor = null;
		this.properties = new HashMap<String, String>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public String[] getCommDescriptor() {
		return commDescriptor;
	}

	public void setCommDescriptor(String[] commDescriptor) {
		this.commDescriptor = commDescriptor;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}
}
