package it.vige.greenarea.cl.admin.entity;

import it.vige.greenarea.cl.bean.RequestParameter;
import it.vige.greenarea.dto.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class RequestView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7569440536261908243L;
	private int idMission;
	private int idTimeSlot;
	private Date dateMiss;
	private String userName;
	private String carPlate;
	private String company;
	private ArrayList<RequestParameter> reqParList;
	private double[] qu;
	private double price;
	private Color color;

	public RequestView() {
	}

	public String getCarPlate() {
		return carPlate;
	}

	public void setCarPlate(String carPlate) {
		this.carPlate = carPlate;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	public ArrayList<RequestParameter> getReqParList() {
		return reqParList;
	}

	public void setReqParList(ArrayList<RequestParameter> reqParList) {
		this.reqParList = reqParList;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
