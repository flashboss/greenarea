package it.vige.greenarea.dto;

import java.io.Serializable;

public class Filtro implements Serializable {

	private static final long serialVersionUID = -5497044025192500748L;
	private String roundCode;
	private String operatoreLogistico;

	public Filtro(String roundCode, String operatoreLogistico) {
		super();
		this.roundCode = roundCode;
		this.operatoreLogistico = operatoreLogistico;
	}

	public String getRoundCode() {
		return roundCode;
	}

	public void setRoundCode(String roundCode) {
		this.roundCode = roundCode;
	}

	public String getOperatoreLogistico() {
		return operatoreLogistico;
	}

	public void setOperatoreLogistico(String operatoreLogistico) {
		this.operatoreLogistico = operatoreLogistico;
	}

	@Override
	public String toString() {
		return roundCode + " | " + operatoreLogistico;
	}
}
