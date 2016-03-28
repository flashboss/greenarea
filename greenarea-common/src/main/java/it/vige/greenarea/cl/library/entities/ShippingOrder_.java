/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.greenarea.cl.library.entities;

import java.sql.Timestamp;
import java.util.HashMap;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import it.vige.greenarea.cl.library.entities.OrderStatus.StateValue;

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
