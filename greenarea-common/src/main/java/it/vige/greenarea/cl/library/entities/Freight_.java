package it.vige.greenarea.cl.library.entities;

import it.vige.greenarea.cl.library.entities.Attachment;
import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.FreightItemState;
import it.vige.greenarea.cl.library.entities.FreightType;
import it.vige.greenarea.cl.library.entities.Transport;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.3.2.v20111125-r10461", date="2013-06-18T15:04:43")
@StaticMetamodel(Freight.class)
public class Freight_ { 

    public static volatile SingularAttribute<Freight, Integer> weight;
    public static volatile SingularAttribute<Freight, Integer> width;
    public static volatile SingularAttribute<Freight, Boolean> keepUpStanding;
    public static volatile SingularAttribute<Freight, ExchangeStop> dropDownPoint;
    public static volatile SingularAttribute<Freight, String> codeId;
    public static volatile SingularAttribute<Freight, Integer> height;
    public static volatile SingularAttribute<Freight, Transport> transport;
    public static volatile SingularAttribute<Freight, String> description;
    public static volatile SingularAttribute<Freight, Integer> volume;
    public static volatile SingularAttribute<Freight, ExchangeStop> pickUpPoint;
    public static volatile ListAttribute<Freight, Attachment> attachments;
    public static volatile SingularAttribute<Freight, FreightType> ft;
    public static volatile SingularAttribute<Freight, FreightItemState> freightState;
    public static volatile SingularAttribute<Freight, Boolean> stackable;
    public static volatile SingularAttribute<Freight, Integer> leng;

}