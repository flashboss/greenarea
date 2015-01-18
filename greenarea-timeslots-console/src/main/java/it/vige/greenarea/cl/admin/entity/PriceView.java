package it.vige.greenarea.cl.admin.entity;

import it.vige.greenarea.dto.AccessoVeicoli;
import it.vige.greenarea.dto.Color;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class PriceView implements Serializable {

	private static final long serialVersionUID = -8454468931588522365L;

	private Integer idPrice;
	private Integer idTS;
	private Color color;
	private double maxPrice;
	private double minPrice;
	private double fixPrice;
	private AccessoVeicoli typeEntry;

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getFixPrice() {
		return fixPrice;
	}

	public void setFixPrice(double fixPrice) {
		this.fixPrice = fixPrice;
	}

	public Integer getIdPrice() {
		return idPrice;
	}

	public void setIdPrice(Integer idPrice) {
		this.idPrice = idPrice;
	}

	public Integer getIdTS() {
		return idTS;
	}

	public void setIdTS(Integer idTS) {
		this.idTS = idTS;
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}

	public AccessoVeicoli getTypeEntry() {
		return typeEntry;
	}

	public void setTypeEntry(AccessoVeicoli typeEntry) {
		this.typeEntry = typeEntry;
	}

	public String tipologiaAccesso() {
		String tipologiaAccess = "";

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = facesContext.getApplication()
				.getResourceBundle(facesContext, "messages");

		switch (typeEntry) {
		case NEGATO:
			tipologiaAccess = bundle.getString("fasceorarie_accesso_negato");
			break;
		case GRATUITO:
			tipologiaAccess = bundle.getString("fasceorarie_accesso_gratuito");
			break;
		case PREZZO_FISSO:
			tipologiaAccess = bundle
					.getString("fasceorarie_accesso_a_prezzo_fisso")
					+ " "
					+ fixPrice + " ???";
			break;
		case PREZZO_VARIABILE:
			tipologiaAccess = bundle
					.getString("fasceorarie_accesso_a_prezzo_variabile")
					+ " ["
					+ minPrice + " ??? - " + maxPrice + " ???]";
			break;
		default:
			tipologiaAccess = "";
			break;
		}

		return tipologiaAccess;
	}

	public String categoriaVeicolo() {
		return color.toString();
	}
}
