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
package it.vige.greenarea.sgaplconsole.models;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import it.vige.greenarea.sgaplconsole.data.Attributi;

public class AttributesModel extends ListDataModel<Attributi> implements SelectableDataModel<Attributi>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8992224067287011373L;

	public AttributesModel() {
	}

	public AttributesModel(List<Attributi> list) {
		super(list);
	}

	@Override
	public Object getRowKey(Attributi t) {
		return null;// t.getIndex();
	}

	@Override
	public Attributi getRowData(String rowKey) {
		/*
		 * @SuppressWarnings("unchecked") List<Attributi> terms =
		 * (List<Attributi>) getWrappedData();
		 * 
		 * for(Attributi t : terms) {
		 * if(rowKey.equals(String.valueOf(t.getIndex()))) { return t; } }
		 */
		return null;
	}

}