/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Administrator
 */
public class ShippingItemData implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String itemID; //quello che mi passa l'ecomm e che si trova scritto sulla scatola
    private String descrizione;
    private HashMap<String, String> attributi; //include dimensioni, peso ecc....

    public ShippingItemData() {
        this.attributi = new HashMap<String, String>();
    }

    public ShippingItemData(String itemID, String descrizione, HashMap<String, String> attributi) {
        this.itemID = itemID;
        this.descrizione = descrizione;
        this.attributi = attributi;
    }

    public HashMap<String, String> getAttributi() {
        return attributi;
    }

    public void setAttributi(HashMap<String, String> attributi) {
        this.attributi = attributi;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    
}
