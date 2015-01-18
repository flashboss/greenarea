/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgaplconsole.data;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class Attributi implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4702296460247388193L;
	String key;
    String val;

    public Attributi() {
    }

    public Attributi(String key, String val) {
        this.key = key;
        this.val = val;
    }
    
    

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "< "+key + " : " + val+"> ";
    }
}
