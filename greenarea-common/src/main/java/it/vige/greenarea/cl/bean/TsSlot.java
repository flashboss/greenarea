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
package it.vige.greenarea.cl.bean;

import java.util.ArrayList;

/**
 *
 * 
 */
public class TsSlot {

	private int dateTS;
	ArrayList<Integer> idTSList;

	public int getDateTS() {
		return dateTS;
	}

	public void setDateTS(int dateTS) {
		this.dateTS = dateTS;
	}

	public ArrayList<Integer> getIdTSList() {
		return idTSList;
	}

	public void setIdTSList(ArrayList<Integer> idTSList) {
		this.idTSList = idTSList;
	}

}
