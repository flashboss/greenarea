/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgaplconsole.models;

import it.vige.greenarea.sgaplconsole.data.Attributi;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author 00917377
 */
public class AttributesModel extends ListDataModel<Attributi> implements SelectableDataModel<Attributi>, Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8992224067287011373L;

	public AttributesModel() {
    }

    public AttributesModel(List<Attributi> list) {
        super(list);
    }

    @Override
    public Object getRowKey(Attributi t) {
        return null;//t.getIndex();
    }

    @Override
    public Attributi getRowData(String rowKey) {
    	/*
    	@SuppressWarnings("unchecked")
        List<Attributi> terms = (List<Attributi>) getWrappedData();  
          
        for(Attributi t : terms) {  
            if(rowKey.equals(String.valueOf(t.getIndex()))) {
                return t;
            }  
        }*/
        return null;
    }
    
}