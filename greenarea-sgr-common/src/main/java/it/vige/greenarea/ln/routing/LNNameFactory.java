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
package it.vige.greenarea.ln.routing;

import java.util.HashMap;
import java.util.HashSet;

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
