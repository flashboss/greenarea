package it.vige.greenarea.cl.library.entities;

import static javax.persistence.GenerationType.AUTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class TapOutData implements Serializable {

	private static final long serialVersionUID = 3583892844188258432L;

	@Id
	@GeneratedValue(strategy = AUTO)
	private int id;
	private String vin;
	private String serviceProvider;
	private String codeFunction;
	private GregorianCalendar date;
	@OneToMany
	private List<TapGroupData> groups;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the value of the vin property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getVin() {
		return vin;
	}

	/**
	 * Sets the value of the vin property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setVin(String value) {
		this.vin = value;
	}

	/**
	 * Gets the value of the serviceProvider property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getServiceProvider() {
		return serviceProvider;
	}

	/**
	 * Sets the value of the serviceProvider property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setServiceProvider(String value) {
		this.serviceProvider = value;
	}

	/**
	 * Gets the value of the codeFunction property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodeFunction() {
		return codeFunction;
	}

	/**
	 * Sets the value of the codeFunction property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodeFunction(String value) {
		this.codeFunction = value;
	}

	/**
	 * Gets the value of the date property.
	 * 
	 * @return possible object is {@link GregorianCalendar }
	 * 
	 */
	public GregorianCalendar getDate() {
		return date;
	}

	/**
	 * Sets the value of the date property.
	 * 
	 * @param value
	 *            allowed object is {@link GregorianCalendar }
	 * 
	 */
	public void setDate(GregorianCalendar value) {
		this.date = value;
	}

	/**
	 * Gets the value of the groups property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the groups property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getGroups().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link GroupData }
	 * 
	 * 
	 */
	public List<TapGroupData> getGroups() {
		if (groups == null) {
			groups = new ArrayList<TapGroupData>();
		}
		return this.groups;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		TapOutData other = (TapOutData) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return id + "";
	}

}
