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
package it.vige.greenarea.cl.smart.actionform;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 *
 * 
 */
public class AddMissionForm extends org.apache.struts.action.ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7994881088528553306L;
	private String idVehicle; // Targa
	private String name;
	private String company;
	private String idTimeSlot;
	private String addressList;
	private String dateMiss;

	private String lunghezza;
	private String carico;
	private String tappe;
	private String euro;
	private String peso;

	public String getCarico() {
		return carico;
	}

	public void setCarico(String Carico) {
		this.carico = Carico;
	}

	public String getEuro() {
		return euro;
	}

	public void setEuro(String Euro) {
		this.euro = Euro;
	}

	public String getLunghezza() {
		return lunghezza;
	}

	public void setLunghezza(String Lunghezza) {
		this.lunghezza = Lunghezza;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String Peso) {
		this.peso = Peso;
	}

	public String getTappe() {
		return tappe;
	}

	public void setTappe(String Tappe) {
		this.tappe = Tappe;
	}

	public String getAddressList() {
		return addressList;
	}

	public void setAddressList(String addressList) {
		this.addressList = addressList;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDateMiss() {
		return dateMiss;
	}

	public void setDateMiss(String dateMiss) {
		this.dateMiss = dateMiss;
	}

	public String getIdTimeSlot() {
		return idTimeSlot;
	}

	public void setIdTimeSlot(String idTimeSlot) {
		this.idTimeSlot = idTimeSlot;
	}

	public String getIdVehicle() {
		return idVehicle;
	}

	public void setIdVehicle(String idVehicle) {
		this.idVehicle = idVehicle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This is the action called from the Struts framework.
	 * 
	 * @param mapping
	 *            The ActionMapping used to select this instance.
	 * @param request
	 *            The HTTP Request we are processing.
	 * @return
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		if (getName() == null || getName().length() < 1) {
			errors.add("name", new ActionMessage("error.name.required"));
			// TODO: add 'error.name.required' key to your resources
		}
		return errors;
	}
}
