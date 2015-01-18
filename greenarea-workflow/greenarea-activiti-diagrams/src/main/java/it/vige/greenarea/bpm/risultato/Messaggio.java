package it.vige.greenarea.bpm.risultato;

import java.io.Serializable;

public class Messaggio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1311986349313321456L;
	private Tipo tipo;
	private Categoria categoria;

	public Messaggio(Tipo tipo, Categoria categoria) {
		this.tipo = tipo;
		this.categoria = categoria;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
}
