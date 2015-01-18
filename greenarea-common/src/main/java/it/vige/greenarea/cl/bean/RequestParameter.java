/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * 
 */
// @Stateless
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RequestParameter implements Serializable {

	private static final long serialVersionUID = -7811050298223268926L;
	@XmlElement
	private int idParameter;
	@XmlElement
	private String name;
	@XmlElement
	private double valuePar;

	public RequestParameter() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValuePar() {
		return valuePar;
	}

	public void setValuePar(double valuePar) {
		this.valuePar = valuePar;
	}

	public int getIdParameter() {
		return idParameter;
	}

	public void setIdParameter(int idParameter) {
		this.idParameter = idParameter;
	}

}
