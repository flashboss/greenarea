package it.vige.greenarea.cl.library.entities;

import it.vige.greenarea.cl.library.entities.Transport.TransportState;
import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.Leg;
import it.vige.greenarea.dto.TipoRichiesta;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "EclipseLink-2.3.2.v20111125-r10461", date = "2013-06-18T15:04:43")
@StaticMetamodel(Transport.class)
public class Transport_ {

	public static volatile SingularAttribute<Transport, TransportServiceClass> serviceClass;
	public static volatile ListAttribute<Transport, Freight> freightItems;
	public static volatile SingularAttribute<Transport, GeoLocation> source;
	public static volatile SingularAttribute<Transport, Mission> mission;
	public static volatile SingularAttribute<Transport, TransportState> transportState;
	public static volatile ListAttribute<Transport, Attachment> attachments;
	public static volatile SingularAttribute<Transport, ShippingOrder> shippingOrder;
	public static volatile SingularAttribute<Transport, String> alfacode;
	public static volatile SingularAttribute<Transport, GeoLocation> destination;
	public static volatile SingularAttribute<Transport, DBGeoLocation> dropdown;
	public static volatile SingularAttribute<Transport, Integer> totalVolume;
	public static volatile SingularAttribute<Transport, DBGeoLocation> pickup;
	public static volatile SingularAttribute<Transport, DBGeoLocation> attributes;
	public static volatile SingularAttribute<Transport, Double> cost;
	public static volatile SingularAttribute<Transport, ArrayList<Leg>> route;
	public static volatile SingularAttribute<Transport, Integer> activeLegIndex;
	public static volatile SingularAttribute<Transport, TimeSlot> timeSlot;
	public static volatile SingularAttribute<Transport, Date> timeAccept;
	public static volatile SingularAttribute<Transport, Date> timeClosing;
	public static volatile SingularAttribute<Transport, Date> timeRank;
	public static volatile SingularAttribute<Transport, Integer> request;
	public static volatile SingularAttribute<Transport, Date> dateMiss;
	public static volatile SingularAttribute<Transport, TipoRichiesta> tipo;
	public static volatile SingularAttribute<Transport, String> operatoreLogistico;
	public static volatile SingularAttribute<Transport, String> codiceFiliale;
	public static volatile SingularAttribute<Transport, String> roundCode;

}