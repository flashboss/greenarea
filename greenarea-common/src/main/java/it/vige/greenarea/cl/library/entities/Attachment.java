/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

/**
 *
 * @author 00917308
 */


@Embeddable
public class Attachment implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6761818910741542191L;

	@Column( name = "NAME", length = 40 )
    private String name;
    
    @Lob
    Serializable contents;

    public Attachment(){}

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

