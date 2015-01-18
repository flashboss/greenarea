package it.vige.greenarea.gtg.db.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 *
 * @author 00917308
 */
@Entity

public class SystemVar implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String varName;
    @Lob
    private Serializable content;

    public SystemVar() {
    }

    public SystemVar(String varName) {
        this.varName = varName;
    }

    public Serializable getContent() {
        return content;
    }

    public void setContent(Serializable content) {
        this.content = content;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (varName != null ? varName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SystemVar)) {
            return false;
        }
        SystemVar other = (SystemVar) object;
        if ((this.varName == null && other.varName != null) || (this.varName != null && !this.varName.equals(other.varName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "varName=" + varName + ", content=" + content;
    }
}
