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
package it.vige.greenarea.itseasy.lib.mqClientUtil;

import it.vige.greenarea.itseasy.lib.configurationData.MqConstants;

public class ItseasyStoreInfo implements MqConstants {

	int objStoreType;
	String url;
	String principal;
	String credentials;

	/*
	 * public ItseasyStoreInfo() { objStoreType = new
	 * Integer(ConfigurationSettings
	 * .getProperty(ConfigurationSettings.getINITIAL_CONTEXT_TYPE())); if
	 * (objStoreType == OBJ_FACTORY_FS_TYPE) { url =
	 * ConfigurationSettings.getProperty
	 * (ConfigurationSettings.getFILE_SYSTEM_URL()); principal = ""; credentials
	 * = "";
	 * 
	 * } else if (objStoreType == OBJ_FACTORY_LDAP_TYPE) { url =
	 * ConfigurationSettings.getProperty(ConfigurationSettings.getLDAP_URL());
	 * principal =
	 * ConfigurationSettings.getProperty(ConfigurationSettings.getLDAP_PRINCIPAL
	 * ()); credentials =
	 * ConfigurationSettings.getProperty(ConfigurationSettings
	 * .getLDAP_Credential());
	 * 
	 * } }
	 */

	public ItseasyStoreInfo(int type, String u, String p, String c) {
		objStoreType = type;
		url = u;
		principal = p;
		credentials = c;
	}

	public int getObjStoreType() {
		return objStoreType;
	}

	public String getUrl() {
		return url;
	}

	public String getPrincipal() {
		return principal;
	}

	public String getCredentials() {
		return credentials;
	}

	@Override
	public boolean equals(Object o) {

		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(o instanceof ItseasyStoreInfo)) {
			return false;
		}
		ItseasyStoreInfo other = (ItseasyStoreInfo) o;
		if (this.objStoreType != other.objStoreType) {
			return false;
		}
		if (!this.url.equals(other.url)) {
			return false;
		}
		if (!this.principal.equals(other.principal)) {
			return false;
		}
		if (!this.credentials.equals(other.credentials)) {
			return false;
		}
		return true;

	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 89 * hash + this.objStoreType;
		hash = 89 * hash + (this.url != null ? this.url.hashCode() : 0);
		hash = 89 * hash + (this.principal != null ? this.principal.hashCode() : 0);
		hash = 89 * hash + (this.credentials != null ? this.credentials.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "STORE INFO: URL-->" + this.url + " | principal-->" + this.principal + " | credentials-->"
				+ this.credentials;

	}
}
