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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import it.vige.greenarea.cl.admin.entity.ParameterGenView;
import it.vige.greenarea.cl.admin.entity.SchedView;
import it.vige.greenarea.cl.admin.rest.TimeSlotRestClient;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.dto.TipologiaParametro;

/**
 * <p>
 * Class: OpzioniBean
 * </P>
 * <p>
 * Description: Contiene tutti i meotodi Relativi alla pagina web Opzioni
 * </P>
 */
public class OpzioniBean implements Serializable {

	private static final long serialVersionUID = -8948606149044431924L;

	private Logger logger = getLogger(getClass());

	private Integer idPG;
	private String namePG;
	// typePG: beneficio (0) costo (1) contatore (2) booleano (3)
	private TipologiaParametro typePG;
	private String measureUnit;
	// useType: disabilitato (0) abilitato (1)
	private boolean useType;
	private String description;

	private SchedView schedView;

	private List<ParameterGen> listaAllParameterGen;
	private List<ParameterGenView> listaAllParameterGenView;

	TimeSlotRestClient rcOpzioniBean = new TimeSlotRestClient();
	private ParameterGen newParGen = new ParameterGen();
	// size di listaAllParameterGen
	private int allParameterGen;
	private ParameterGen parameterGen;

	public OpzioniBean() {
	}

	public int getAllParameterGen() {
		return allParameterGen;
	}

	public void setAllParameterGen(int allParameterGen) {
		this.allParameterGen = allParameterGen;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getIdPG() {
		return idPG;
	}

	public void setIdPG(Integer idPG) {
		this.idPG = idPG;
	}

	public TipologiaParametro getTypePG() {
		return typePG;
	}

	public void setTypePG(TipologiaParametro typePG) {
		this.typePG = typePG;
	}

	public String getMeasureUnit() {
		return measureUnit;
	}

	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

	public String getNamePG() {
		return namePG;
	}

	public void setNamePG(String namePG) {
		this.namePG = namePG;
	}

	public ParameterGen getNewParGen() {
		return newParGen;
	}

	public void setNewParGen(ParameterGen newParGen) {
		this.newParGen = newParGen;
	}

	public ParameterGen getParameterGen() {
		return parameterGen;
	}

	public void setParameterGen(ParameterGen parameterGen) {
		this.parameterGen = parameterGen;
	}

	public SchedView getSchedView() {
		return schedView;
	}

	public void setSchedView(SchedView schedView) {
		this.schedView = schedView;
	}

	public boolean isUseType() {
		return useType;
	}

	public void setUseType(boolean useType) {
		this.useType = useType;
	}

	/**
	 * <p>
	 * Method: allSchedulesView()
	 * </P>
	 * <p>
	 * Description: Ritorna la lista di tutti i ParameterGen
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<ParameterGenView> allSchedulesView() throws Exception {
		this.listaAllParameterGenView = new ArrayList<ParameterGenView>();
		listaAllParameterGen = rcOpzioniBean.findAllParameterGen();
		allParameterGen = listaAllParameterGen.size();
		listaAllParameterGenView = new ArrayList<ParameterGenView>();
		for (int i = 0; i < allParameterGen; i++) {
			ParameterGenView parameterGenView = new ParameterGenView();
			parameterGenView.setIdPG(listaAllParameterGen.get(i).getId());
			parameterGenView.setDescription(listaAllParameterGen.get(i).getDescription());
			parameterGenView.setIdPG(listaAllParameterGen.get(i).getId());
			parameterGenView.setTypePG(listaAllParameterGen.get(i).getTypePG());
			parameterGenView.setMeasureUnit(listaAllParameterGen.get(i).getMeasureUnit());
			parameterGenView.setNamePG(listaAllParameterGen.get(i).getNamePG());
			parameterGenView.setUseType(listaAllParameterGen.get(i).isUseType());
			listaAllParameterGenView.add(parameterGenView);
		}
		return listaAllParameterGenView;
	}

	/**
	 * <p>
	 * Method: toggleEnable(ParameterGenView item)
	 * </P>
	 * <p>
	 * Description: Aggiorna lo stato del ParameterGen
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public void toggleEnable(ParameterGenView item) throws Exception {
		for (int i = 0; i < allParameterGen; i++) {
			ParameterGenView currentItem = listaAllParameterGenView.get(i);
			if (item.getIdPG() == currentItem.getIdPG())
				currentItem.useType = !currentItem.useType;
			String status = String.format("State change of item (id=%d) '%s' => '%s'", currentItem.getIdPG(),
					currentItem.getNamePG(), (currentItem.useType ? "attivo" : "inattivo"));
			logger.info(status);
			newParGen.setId(item.getIdPG());
			newParGen.setDescription(item.getDescription());
			newParGen.setTypePG(item.getTypePG());
			newParGen.setMeasureUnit(item.getMeasureUnit());
			newParGen.setNamePG(item.getNamePG());
			newParGen.setUseType(item.getUseType());

			TimeSlotRestClient rcUpdateParameterGen = new TimeSlotRestClient();
			rcUpdateParameterGen.updateParameterGen(newParGen);
			logger.info("Parametro Mandato a Paolo : " + item.getNamePG());

		}
	}

	/**
	 * <p>
	 * Method: addParameterGen()
	 * </P>
	 * <p>
	 * Description: Aggiorna un nuovo ParameterGen al db
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public ParameterGen addParameterGen() throws Exception {

		parameterGen = new ParameterGen();

		parameterGen.setDescription(description);
		parameterGen.setTypePG(typePG);
		parameterGen.setMeasureUnit(measureUnit);
		parameterGen.setNamePG(namePG);
		parameterGen.setUseType(true);

		// Inserisco TimeSlot nel db
		TimeSlotRestClient rcAddParameterGen = new TimeSlotRestClient();
		parameterGen = rcAddParameterGen.addParameterGen(parameterGen);

		return parameterGen;
	}

	public TipologiaParametro[] getTipologiaParametri() {
		return TipologiaParametro.values();
	}

}
