package it.vige.greenarea.cl.library.entities;

import it.vige.greenarea.cl.library.entities.Attachment;
import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.TruckLoadDescriptor;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.3.2.v20111125-r10461", date="2013-06-18T15:04:43")
@StaticMetamodel(ExchangeStop.class)
public class ExchangeStop_ { 

    public static volatile SingularAttribute<ExchangeStop, Long> id;
    public static volatile ListAttribute<ExchangeStop, Freight> collectingList;
    public static volatile SingularAttribute<ExchangeStop, DBGeoLocation> location;
    public static volatile ListAttribute<ExchangeStop, Freight> deliveryList;
    public static volatile SingularAttribute<ExchangeStop, TruckLoadDescriptor> truckLoad;
    public static volatile ListAttribute<ExchangeStop, Attachment> attachments;
    public static volatile SingularAttribute<ExchangeStop, String> driverNotes;

}