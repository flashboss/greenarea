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
package it.vige.greenarea.I18N;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {
	private static Locale currentLocale;
	private static ResourceBundle messages;

	public static String[] getStrings(Object[] values) {
		if (values == null)
			return null;
		if (currentLocale == null) {
			setLocale(Locale.getDefault());
		}
		String[] result = new String[values.length];
		int i = 0;
		for (Object s : values)
			result[i++] = messages.getString(s.toString());
		return result;
	}

	public static I18NObject[] getI18NObjects(Object[] values) {
		if (values == null)
			return null;
		if (currentLocale == null) {
			setLocale(Locale.getDefault());
		}
		I18NObject[] result = new I18NObject[values.length];
		int i = 0;
		for (Object s : values)
			result[i++] = new I18NObject(s);
		return result;
	}

	public static void setLocale(Locale l) {
		currentLocale = l;
		messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);
	}

	static void setLocale(String language, String country) {
		Locale l = new Locale(language, country);
		setLocale(l);
	}

	public static Locale getLocale() {
		if (currentLocale == null) {
			setLocale(Locale.getDefault());
		}
		return currentLocale;
	}

	public static String getString(String key) {
		if (currentLocale == null) {
			setLocale(Locale.getDefault());
		}
		return messages.getString(key);
	}

}
