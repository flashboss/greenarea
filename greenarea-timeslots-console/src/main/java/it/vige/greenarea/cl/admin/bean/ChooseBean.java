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
package it.vige.greenarea.cl.admin.bean;

import static org.slf4j.LoggerFactory.getLogger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.ParameterTS;

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
