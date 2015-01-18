package it.vige.greenarea.cl.library.entities;

import java.util.HashMap;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "EclipseLink-2.3.2.v20111125-r10461", date = "2013-06-18T15:04:43")
@StaticMetamodel(ShippingItem.class)
public class ShippingItem_ {

	public static volatile SingularAttribute<ShippingItem, String> id;
	public static volatile SingularAttribute<ShippingItem, ShippingOrder> shippingOrder;
	public static volatile SingularAttribute<ShippingOrder, HashMap<String, String>> attributes;
	public static volatile SingularAttribute<ShippingOrder, String> description;

}
