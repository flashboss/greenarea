/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 */
public class OrderStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8878262480062113251L;

	public static enum StateValue {
		unknown, idle, suspended, ready, ongoing, returning, completed
	};
}
