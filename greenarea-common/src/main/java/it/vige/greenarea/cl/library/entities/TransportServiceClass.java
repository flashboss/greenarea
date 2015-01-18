/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import static javax.persistence.GenerationType.AUTO;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author 00917377
 */
@Entity
public class TransportServiceClass implements Serializable {
	
	private static final long serialVersionUID = 867633970337278701L;
	@Id
    @GeneratedValue(strategy = AUTO)
    private Long id;
    private String description;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransportServiceClass)) {
            return false;
        }
        TransportServiceClass other = (TransportServiceClass) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();

        sb.append("[Id = ").append(this.id);
        sb.append("] ").append(this.description);
        
        return sb.toString();
    }
    
}
