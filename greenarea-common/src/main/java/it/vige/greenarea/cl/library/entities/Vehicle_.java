package it.vige.greenarea.cl.library.entities;

import it.vige.greenarea.dto.StatoVeicolo;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "EclipseLink-2.3.2.v20111125-r10461", date = "2013-06-18T15:04:43")
@StaticMetamodel(Vehicle.class)
public class Vehicle_ {

	public static volatile SingularAttribute<Vehicle, TruckServiceClass> serviceClass;
	public static volatile SingularAttribute<Vehicle, StatoVeicolo> state;
	public static volatile SingularAttribute<Vehicle, String> plateNumber;
	public static volatile ListAttribute<Vehicle, Attachment> attachments;
	public static volatile SingularAttribute<Vehicle, String> operatoreLogistico;
	public static volatile SingularAttribute<Vehicle, String> autista;
	public static volatile SingularAttribute<Vehicle, String> societaDiTrasporto;
	public static volatile SingularAttribute<Vehicle, String> codiceFiliale;
	public static volatile SingularAttribute<Vehicle, String> roundCode;
	public static volatile SingularAttribute<Vehicle, String> vin;

}