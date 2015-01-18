package it.vige.greenarea.cl.library.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Filter {

	@EmbeddedId
	private FilterKey id;

	@Column(insertable = false, updatable = false)
	private String operatoreLogistico;

	public Filter() {
	}

	public Filter(String roundCode, String operatoreLogistico) {
		this.id = new FilterKey(roundCode, operatoreLogistico);
		this.operatoreLogistico = operatoreLogistico;
	}

	public FilterKey getId() {
		return id;
	}

	public void setId(FilterKey id) {
		this.id = id;
	}

	public String getOperatoreLogistico() {
		return operatoreLogistico;
	}

}
