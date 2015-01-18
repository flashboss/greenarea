/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.lib.mqData;

import java.io.Serializable;


/**
 *
 * @author 00917308
 */


public class MY_Attachment implements Serializable {
   
    /**
	 * 
	 */
	private static final long serialVersionUID = 1691415131806681500L;


	private String name;
    
  
    Serializable contents;

    public MY_Attachment(){}

    public Serializable getContents() {
        return contents;
    }

    public void setContents(Serializable contents) {
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

