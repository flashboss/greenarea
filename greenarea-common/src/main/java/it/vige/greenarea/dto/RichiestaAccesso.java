package it.vige.greenarea.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RichiestaAccesso implements Serializable {

	private static final long serialVersionUID = -45137880937658994L;
	private List<String> gas;
	private Date dataInizio;
	private Date dataFine;
	private List<String> operatoriLogistici;

	public List<String> getGas() {
		return gas;
	}

	public void setGas(List<String> gas) {
		this.gas = gas;
	}

	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}

	public List<String> getOperatoriLogistici() {
		return operatoriLogistici;
	}

	public void setOperatoriLogistici(List<String> operatoriLogistici) {
		this.operatoriLogistici = operatoriLogistici;
	}
}
