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
package it.vige.greenarea.cl.library.entities;

import static javax.persistence.GenerationType.AUTO;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Customer implements Serializable {

	private static final long serialVersionUID = 1270151328772169585L;

	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;

	// Id dell'istanza di sgapl che mi da' il servizio (Company)
	// dati di autenticazione
	private String userName;
	private String password;

	// riferimento webService per eventi notifica
	private String notificationCBurl;

	public Customer() {
	}

	public Customer(String userName, String password, String notificationCBurl) {
		this.userName = userName;
		this.password = password;
		this.notificationCBurl = notificationCBurl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNotificationCBurl() {
		return notificationCBurl;
	}

	public void setNotificationCBurl(String notificationCBurl) {
		this.notificationCBurl = notificationCBurl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((notificationCBurl == null) ? 0 : notificationCBurl.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (notificationCBurl == null) {
			if (other.notificationCBurl != null)
				return false;
		} else if (!notificationCBurl.equals(other.notificationCBurl))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClientAccount[ id=" + id + " ]: " + userName;
	}
}
