/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.dto;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 */

public class Leg implements Serializable {
	
	private static final long serialVersionUID = -4869422424441467989L;
	private ExchangeSite source;
	private ExchangeSite destination;
	private String vector;

	public Leg() {
	}

	public Leg(ExchangeSite source, String vector, ExchangeSite destination) {
		this.source = source;
		this.vector = vector;
		this.destination = destination;
	}
	
	public ExchangeSite getDestination() {
		return destination;
	}

	public void setDestination(ExchangeSite destination) {
		this.destination = destination;
	}

	public ExchangeSite getSource() {
		return source;
	}

	public void setSource(ExchangeSite source) {
		this.source = source;
	}

	public String getVector() {
		return vector;
	}

	public void setVector(String vector) {
		this.vector = vector;
	}

	@Override
	public String toString() {
		return /* id + " - "+ */vector + ": " + source.getName() + " --> "
				+ destination.getName();
	}

}