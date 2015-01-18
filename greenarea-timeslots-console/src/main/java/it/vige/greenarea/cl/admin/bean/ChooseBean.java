/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.admin.bean;

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.ParameterTS;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;

/**
 * <p>
 * Class: ChooseBean
 * </P>
 * <p>
 * Description: Questa classe ?? in grado di gestire un array checkbox
 * </P>
 */

@ManagedBean(name = "choose")
@SessionScoped
public class ChooseBean {

	private Logger logger = getLogger(getClass());

	public int[] favNumberInt;

	public ParameterTS[] arrayParametri;

	int size;

	public ParameterTS[] getArrayParametri() {
		return arrayParametri;
	}

	public void setArrayParametri(ParameterTS[] arrayParametri) {
		this.arrayParametri = arrayParametri;
	}

	public int[] getFavNumberInt() {
		return favNumberInt;
	}

	public void setFavNumberInt(int[] favNumberInt) {
		this.favNumberInt = favNumberInt;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void remove(int rem) {
		logger.info("Dentro Remove");
		int si = this.favNumberInt.length;
		logger.info(size + "");
		for (int i = 0; i < si; i++) {
			if (rem == this.favNumberInt[i]) {
				logger.info(this.favNumberInt[i] + "Rimosso");
				this.favNumberInt[i] = 0;
			}
		}
	}

}
