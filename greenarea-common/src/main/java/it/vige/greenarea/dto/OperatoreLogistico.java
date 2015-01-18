package it.vige.greenarea.dto;

import java.util.List;

public class OperatoreLogistico extends GreenareaUser {

	private static final long serialVersionUID = -9221064473650137388L;

	private List<Richiesta> ritiri;

	public OperatoreLogistico() {
	}

	public OperatoreLogistico(GreenareaUser user) {
		super(user.getId());
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.password = user.getPassword();
	}

	public List<Richiesta> getRitiri() {
		return ritiri;
	}

	public void setRitiri(List<Richiesta> ritiri) {
		this.ritiri = ritiri;
	}

}
