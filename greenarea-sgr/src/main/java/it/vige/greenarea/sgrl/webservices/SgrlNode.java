/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgrl.webservices;

import java.io.Serializable;

/**
 * 
 * @author 00917308
 */
public class SgrlNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1060524819054578344L;
	private String name;
	private String nameDetails;

	public SgrlNode() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameDetails() {
		return nameDetails;
	}

	public void setNameDetails(String nameDetails) {
		this.nameDetails = nameDetails;
	}

}
