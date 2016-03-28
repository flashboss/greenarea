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
package it.vige.greenarea.sgaplconsole.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.inject.Named;

import it.vige.greenarea.sgaplconsole.data.MonitorMsg;

@Singleton
@Named("monitorController")
public class MonitorController implements Serializable {

	private static final long serialVersionUID = -4854465558465574842L;

	private List<MonitorMsg> sbMonitor = new ArrayList<MonitorMsg>();

	public List<MonitorMsg> getSbMonitor() {
		return sbMonitor;
	}

	public void setSbMonitor(List<MonitorMsg> sbMonitor) {
		this.sbMonitor = sbMonitor;
	}

	public void clear() {
		sbMonitor = new ArrayList<MonitorMsg>();
	}

}