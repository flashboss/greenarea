/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.utilities;


/**
 * 
 * @author 00917308
 */
public class NameRegistry {

	// TODO:
	public static boolean registerName(String domain, String name) {
		if (domain != null && !domain.isEmpty() && name != null
				&& !name.isEmpty()) {
			return true;

		} else
			return false;
	}

	// TODO:
	public static boolean dropName(String domain, String name) {
		if (domain != null && !domain.isEmpty() && name != null
				&& !name.isEmpty()) {
			return true;

		} else
			return false;
	}
}
