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
package it.vige.greenarea.itseasy.lib.mqData;

import it.vige.greenarea.cl.library.entities.FreightType;
import it.vige.greenarea.itseasy.lib.configurationData.FreightAttribs;

import java.io.Serializable;
import java.util.HashMap;

public class MqFreightData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1579771594017508290L;
	private String itemID; // quello che mi passa l'ecomm e che si trova scritto
							// sulla scatola
	private String descrizione;
	private Integer volume;
	private Integer height;
	private Integer lenght;
	private Integer width;
	private Integer weight;
	private Boolean stackable;
	private Boolean keepUpStanding;
	private FreightType type;
	private HashMap<String, String> attributi; // include dimensioni, peso
												// ecc....

	public MqFreightData(String itemID, String descrizione,
			HashMap<String, String> attributi) {
		this.itemID = itemID;
		this.descrizione = descrizione;

		// cerco tra gli attributi
		this.attributi = new HashMap<String, String>();
		if ((attributi != null) && (!attributi.isEmpty())) {
			// cerco il tipo se non lo trovo metto un default
			String type = attributi.get("Type");
			if (type != null) {
				this.type = type.equals("B") ? FreightType.DOCUMENTI
						: FreightType.ALTRO_TIPO;
				attributi.remove("Type");
			} else {
				this.type = FreightType.DOCUMENTI;
			}
			for (FreightAttribs fa : FreightAttribs.values()) {
				switch (fa) {
				case Height:
					if (attributi.containsKey(fa.name())) {
						this.height = Integer.getInteger(attributi.get(fa
								.name()));
						attributi.remove(fa.name());
					}
					break;
				case Length:
					if (attributi.containsKey(fa.name())) {
						this.lenght = Integer.getInteger(attributi.get(fa
								.name()));
						attributi.remove(fa.name());
					}
					break;
				case Volume:
					if (attributi.containsKey(fa.name())) {
						this.volume = Integer.getInteger(attributi.get(fa
								.name()));
						attributi.remove(fa.name());
					}
					break;
				case Weight:
					if (attributi.containsKey(fa.name())) {
						this.weight = Integer.getInteger(attributi.get(fa
								.name()));
						attributi.remove(fa.name());
					}
					break;
				case Width:
					if (attributi.containsKey(fa.name())) {
						this.width = Integer
								.getInteger(attributi.get(fa.name()));
						attributi.remove(fa.name());
					}
					break;
				case KeepUpStanding:
					if (attributi.containsKey(fa.name())) {
						this.keepUpStanding = Boolean.getBoolean(attributi
								.get(fa.name()));
						attributi.remove(fa.name());
					}
					break;
				case Stackable:
					if (attributi.containsKey(fa.name())) {
						this.stackable = Boolean.getBoolean(attributi.get(fa
								.name()));
						attributi.remove(fa.name());
					}
					break;
				}
			}
			this.attributi.putAll(attributi);
		}
	}

	public HashMap<String, String> getAttributi() {
		return attributi;
	}

	public void setAttributi(HashMap<String, String> attributi) {
		this.attributi = attributi;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getLenght() {
		return lenght;
	}

	public void setLenght(Integer lenght) {
		this.lenght = lenght;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Boolean getStackable() {
		return stackable;
	}

	public void setStackable(Boolean stackable) {
		this.stackable = stackable;
	}

	public Boolean getKeepUpStanding() {
		return keepUpStanding;
	}

	public void setKeepUpStanding(Boolean keepUpStanding) {
		this.keepUpStanding = keepUpStanding;
	}

	public FreightType getType() {
		return type;
	}

	public void setType(FreightType type) {
		this.type = type;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("");
		if (this.itemID != null) {
			sb.append("[").append(itemID).append("]");
		}
		if (this.descrizione != null) {
			sb.append(" <").append(descrizione).append(">");
		}

		if ((this.attributi != null) || (!this.attributi.isEmpty())) {
			sb.append(" ").append(attributi.toString());
		}
		return sb.toString();
	}
}
