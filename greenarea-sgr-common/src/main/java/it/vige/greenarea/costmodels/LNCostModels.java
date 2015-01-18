/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.costmodels;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;

import org.slf4j.Logger;

/**
 * 
 * @author 00917308
 */
public class LNCostModels {

	private static Logger logger = getLogger(LNCostModels.class);

	private static HashMap<String, Class<?>> costModelsMap = new HashMap<String, Class<?>>();
	static {
		Class<?>[] classes = new Class<?>[] { ConstantCost.class,
				DistanceBasedCost.class };
		for (int i = 0; i < classes.length; i++) {
			if (!classes[i].isInterface())
				costModelsMap.put(classes[i].getSimpleName(), classes[i]);
		}
	}

	public static LNICostFunction getNewCostFunction(String costModelName) {
		LNICostFunction costFunction = null;

		Class<?> c = costModelsMap.get(costModelName);
		if (c != null) {
			try {
				Object o = c.newInstance();
				costFunction = (LNICostFunction) o;
			} catch (Exception ex) {
				logger.error("errore sgr common", ex);
			}
		}
		return costFunction;
	}

	public static String[] getCostModelNames() {
		return (String[]) costModelsMap.keySet().toArray();
	}

}
