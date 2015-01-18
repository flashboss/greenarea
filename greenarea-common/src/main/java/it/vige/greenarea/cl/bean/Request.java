/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.bean;

import it.vige.greenarea.dto.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


//import javax.ejb.Stateless;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Request implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8626313302086763727L;
	@XmlElement
	private int idMission;
	@XmlElement
	private int idTimeSlot;
	@XmlElement
	private Date dateMiss;
	@XmlElement
	private String userName;
	@XmlElement
	private String carPlate;
	@XmlElement
	private String company;
	@XmlElement
	private ArrayList<RequestParameter> reqParList;
	@XmlElement
	private double[] qu;
	@XmlElement
	private double price;
	@XmlElement
	private Color color;

	public Request() {
		this.reqParList = new ArrayList<RequestParameter>();
	}

	public ArrayList<RequestParameter> getReqParList() {
		return reqParList;
	}

	public void setReqParList(ArrayList<RequestParameter> reqParList) {
		this.reqParList = reqParList;
	}

	public Date getDateMiss() {
		return dateMiss;
	}

	public void setDateMiss(Date dateMiss) {
		this.dateMiss = dateMiss;
	}

	public int getIdMission() {
		return idMission;
	}

	public void setIdMission(int idMission) {
		this.idMission = idMission;
	}

	public int getIdTimeSlot() {
		return idTimeSlot;
	}

	public void setIdTimeSlot(int idTimeSlot) {
		this.idTimeSlot = idTimeSlot;
	}

	public void addRequestParameter(RequestParameter rp) {
		this.reqParList.add(rp);
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double[] getQu() {
		return qu;
	}

	public void setQu(double[] qu) {
		this.qu = qu;
	}

	public String getCarPlate() {
		return carPlate;
	}

	public void setCarPlate(String carPlate) {
		this.carPlate = carPlate;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}