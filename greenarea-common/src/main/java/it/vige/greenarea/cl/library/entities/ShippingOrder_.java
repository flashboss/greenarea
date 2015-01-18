package it.vige.greenarea.cl.library.entities;

import it.vige.greenarea.cl.library.entities.OrderStatus.StateValue;

import java.sql.Timestamp;
import java.util.HashMap;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "EclipseLink-2.3.2.v20111125-r10461", date = "2013-06-18T15:04:43")
@StaticMetamodel(ShippingOrder.class)
public class ShippingOrder_ {

	public static volatile SingularAttribute<ShippingOrder, String> id;
	public static volatile SingularAttribute<ShippingOrder, Timestamp> creationTimestamp;
	public static volatile SingularAttribute<ShippingOrder, Customer> customer;
	public static volatile ListAttribute<ShippingOrder, ShippingItem> shippingItems;
	public static volatile SingularAttribute<ShippingOrder, DBGeoLocation> mittente;
	public static volatile SingularAttribute<ShippingOrder, DBGeoLocation> destinatario;
	public static volatile SingularAttribute<ShippingOrder, HashMap<String, String>> deliveryTerms;
	public static volatile SingularAttribute<ShippingOrder, String> note;
	public static volatile SingularAttribute<ShippingOrder, StateValue> orderStatus;
	public static volatile SingularAttribute<ShippingOrder, String> cost;
	public static volatile SingularAttribute<ShippingOrder, String> trackingURL;
	public static volatile SingularAttribute<ShippingOrder, Transport> transport;
	public static volatile SingularAttribute<ShippingOrder, String> operatoreLogistico;
	public static volatile SingularAttribute<ShippingOrder, String> codiceFiliale;
	public static volatile SingularAttribute<ShippingOrder, String> roundCode;

}
