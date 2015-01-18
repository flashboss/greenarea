/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

/**
 * 
 * @author 00917308
 */
public interface TruckModelInterface {
	int TRUCKOVERLOAD = -1;

	double load(TruckLoadDescriptor descriptor, Freight p);

	double unload(TruckLoadDescriptor descriptor, Freight p);

	boolean canload(TruckLoadDescriptor descriptor, Freight p);
}
