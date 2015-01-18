/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.ln.model;

import it.vige.greenarea.I18N.I18N;
import it.vige.greenarea.dto.GeoLocation;

import java.util.regex.Pattern;

import org.w3c.dom.Element;

/**
 *
 * @author 00917308
 */
public abstract class LNEdge extends LNCell {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7553031000238848701L;
	public static final Pattern EdgeNamePattern = Pattern.compile("\\w{8,20}+");

    public enum Status {Enabled, Disabled};
    public enum Cathegory { UNDEFINED, Driver, Airfreight, SeaFreight, RailFreight, DeliveryCo };
    public enum Domain {UNDEFINED, PUBLIC, PRIVATE};
    private Status status;
    private Cathegory cathegory;
    private Domain domain;
    private String description;
    
    public Cathegory getCathegory() {
        return cathegory;
    }

    public void setCathegory(Cathegory cathegory) {
        this.cathegory = cathegory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
public LNEdge(String cellName){
     this();
     super.setName( cellName!=null?cellName:I18N.getString("UNDEFINED"));
    }

public LNEdge(){
    status = Status.Disabled;
    cathegory = Cathegory.UNDEFINED;
    domain = Domain.UNDEFINED;
    description = "";
}
    
    @Override
public void loadElement(Element el){
//if( el.getLocalName().equals(this.getClass().getSimpleName())){
    super.loadElement(el);
    status = Status.valueOf(el.getAttribute("status"));
    cathegory = Cathegory.valueOf(el.getAttribute("cathegory"));
    domain = Domain.valueOf(el.getAttribute("domain"));
    description = el.getAttribute("description"); 
//}
}

@Override
public Element toElement(){
 Element edgeDescriptor = super.toElement();
 edgeDescriptor.setAttribute(LNCell.LNCELLTYPE, this.getClass().getSimpleName());
 edgeDescriptor.setAttribute("status", status.toString());
 edgeDescriptor.setAttribute("cathegory", cathegory.toString());
 edgeDescriptor.setAttribute("domain", domain.toString());
 edgeDescriptor.setAttribute("description", description);

 
 return edgeDescriptor;
}

public boolean includes( GeoLocation location ){
    return false;
}
}
