/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.ln.routing;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * @author 00917308
 */
public class LNNameFactory {

	private static HashMap<String, NameSet> nameDirectory = new HashMap<String, NameSet>();

	private LNNameFactory() {
	}

	public static String createDistinguishedName(String className) {
		NameSet nameSet = nameDirectory.get(className);
		if (nameSet == null) {
			nameSet = new NameSet(className);
			nameDirectory.put(className, nameSet);
		}
		return nameSet.createDistinguishedName();
	}

	public static boolean saveDistinguishedName(String name, String nameSpace) {
		NameSet nameSet = nameDirectory.get(nameSpace);
		if (nameSet == null) {
			nameSet = new NameSet(nameSpace);
			nameDirectory.put(nameSpace, nameSet);
		} else {
			if (nameSet.contains(name))
				return false;
		}
		nameSet.add(name);
		return true;
	}

	private static class NameSet extends HashSet<String> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3538608232074833083L;
		private String nameSpace;
		private Long counter = new Long(1);

		private NameSet(String nameSpace) {
			super();
			this.nameSpace = nameSpace;
		}

		private String createDistinguishedName() {
			String distinguishedName;
			do {
				distinguishedName = nameSpace.concat((counter++).toString());
			} while (this.contains(distinguishedName));
			this.add(distinguishedName);
			return distinguishedName;
		}

	}
}
