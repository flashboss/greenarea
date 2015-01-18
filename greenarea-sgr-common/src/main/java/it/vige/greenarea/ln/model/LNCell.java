/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.ln.model;

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.I18N.I18N;
import it.vige.greenarea.costmodels.ConstantCost;
import it.vige.greenarea.costmodels.LNCostModels;
import it.vige.greenarea.costmodels.LNICostFunction;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.util.mxDomUtils;

/**
 * 
 * @author 00917308
 */
public abstract class LNCell implements Serializable, Cloneable {

	private static final long serialVersionUID = 1177331553208807435L;

	private Logger logger = getLogger(getClass());

	private String distinguishedName;
	private LNICostFunction costFunction;
	private Document doc = null;

	public static final String LNCELLTYPE = "LNCELLTYPE";
	public static final String LNCELLNAME = "LNCELLNAME";
	public static final String LNCOSTFUNCTION = "LNCOSTFUNCTION";
	public static final Pattern NodeNamePattern = Pattern.compile("\\w{8,20}+");

	public LNCell() {
		if (distinguishedName == null)
			distinguishedName = I18N.getString("UNDEFINED");
		doc = mxDomUtils.createDocument();
	}

	protected Element toElement() {
		Element nodeDescriptor = doc
				.createElement(LNCell.class.getSimpleName());
		nodeDescriptor.setAttribute(LNCell.LNCELLTYPE, this.getClass()
				.getSimpleName());
		nodeDescriptor.setAttribute(LNCell.LNCELLNAME, distinguishedName);
		if (costFunction == null)
			costFunction = new ConstantCost();
		nodeDescriptor.setAttribute(LNCell.LNCOSTFUNCTION, costFunction
				.getClass().getSimpleName());
		costFunction.toElement(nodeDescriptor);

		return nodeDescriptor;
	}

	public void loadElement(Element elt) {
		distinguishedName = elt.getAttribute(LNCELLNAME);
		String costFunctionName = elt.getAttribute(LNCOSTFUNCTION);
		if (costFunctionName.isEmpty())
			costFunctionName = ConstantCost.class.getSimpleName();
		costFunction = LNCostModels.getNewCostFunction(costFunctionName);
		if (costFunction != null)
			costFunction.loadElement(elt);
	}

	public String getName() {
		return distinguishedName;
	}

	public void setName(String name) {
		this.distinguishedName = name;
	}

	public LNICostFunction getCostFunction() {
		return costFunction;
	}

	public void setCostModel(String costModel) {
		this.costFunction = LNCostModels.getNewCostFunction(costModel);
	}

	public boolean isNameValid(String name) {
		Matcher m = LNNode.NodeNamePattern.matcher(name);
		return m.matches();
	}

	public boolean isValid() {
		return isNameValid(this.distinguishedName);
	}

	@Override
	public String toString() {
		return this.distinguishedName.concat(isValid() ? "" : I18N
				.getString("hasErrors"));
	}

	@Override
	public Object clone() {
		Object lnCell = null;
		try {
			lnCell = this.getClass().getConstructor().newInstance();
		} catch (Exception ex) {
			logger.error("errore sgr common", ex);
		}
		return lnCell;
	}
}
