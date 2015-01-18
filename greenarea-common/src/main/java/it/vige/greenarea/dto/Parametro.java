package it.vige.greenarea.dto;

import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.TimeSlot;

import java.io.Serializable;

public class Parametro implements Serializable {

	private static final long serialVersionUID = -5097600631383064176L;
	private int id;
	private int idGen;
	private String nome;
	private String descrizione;
	private String unitaMisura;
	private String tipo;
	private boolean attivo;
	private double valoreMinimo;
	private double valoreMassimo;
	private String peso;

	public Parametro() {

	}

	public Parametro(int id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	public Parametro(String nome, String descrizione, String unitaMisura,
			String tipo, boolean attivo, double valoreMinimo,
			double valoreMassimo, String peso) {
		super();
		this.nome = nome;
		this.descrizione = descrizione;
		this.unitaMisura = unitaMisura;
		this.tipo = tipo;
		this.attivo = attivo;
		this.valoreMinimo = valoreMinimo;
		this.valoreMassimo = valoreMassimo;
		this.peso = peso;
	}

	public Parametro(ParameterGen parametroGen, ParameterTS parametroTS,
			TimeSlot timeSlot) {
		this(parametroGen.getNamePG(), parametroGen.getDescription(),
				parametroGen.getMeasureUnit(), parametroGen.getTypePG().name(),
				parametroGen.isUseType(), parametroTS.getMinValue(),
				parametroTS.getMaxValue(), parametroTS.getWeight().name());
		this.id = parametroTS.getId();
		this.idGen = parametroGen.getId();
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getUnitaMisura() {
		return unitaMisura;
	}

	public void setUnitaMisura(String unitaMisura) {
		this.unitaMisura = unitaMisura;
	}

	public boolean isAttivo() {
		return attivo;
	}

	public void setAttivo(boolean attivo) {
		this.attivo = attivo;
	}

	public double getValoreMinimo() {
		return valoreMinimo;
	}

	public void setValoreMinimo(double valoreMinimo) {
		this.valoreMinimo = valoreMinimo;
	}

	public double getValoreMassimo() {
		return valoreMassimo;
	}

	public void setValoreMassimo(double valoreMassimo) {
		this.valoreMassimo = valoreMassimo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdGen() {
		return idGen;
	}

	public void setIdGen(int idGen) {
		this.idGen = idGen;
	}

	@Override
	public String toString() {
		return descrizione;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (attivo ? 1231 : 1237);
		result = prime * result
				+ ((descrizione == null) ? 0 : descrizione.hashCode());
		result = prime * result + id;
		result = prime * result + idGen;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((peso == null) ? 0 : peso.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		result = prime * result
				+ ((unitaMisura == null) ? 0 : unitaMisura.hashCode());
		long temp;
		temp = Double.doubleToLongBits(valoreMassimo);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(valoreMinimo);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Parametro other = (Parametro) obj;
		if (attivo != other.attivo)
			return false;
		if (descrizione == null) {
			if (other.descrizione != null)
				return false;
		} else if (!descrizione.equals(other.descrizione))
			return false;
		if (id != other.id)
			return false;
		if (idGen != other.idGen)
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (peso == null) {
			if (other.peso != null)
				return false;
		} else if (!peso.equals(other.peso))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		if (unitaMisura == null) {
			if (other.unitaMisura != null)
				return false;
		} else if (!unitaMisura.equals(other.unitaMisura))
			return false;
		if (Double.doubleToLongBits(valoreMassimo) != Double
				.doubleToLongBits(other.valoreMassimo))
			return false;
		if (Double.doubleToLongBits(valoreMinimo) != Double
				.doubleToLongBits(other.valoreMinimo))
			return false;
		return true;
	}
}
