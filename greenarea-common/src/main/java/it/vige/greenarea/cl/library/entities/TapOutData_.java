package it.vige.greenarea.cl.library.entities;

import java.util.GregorianCalendar;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "EclipseLink-2.3.2.v20111125-r10461", date = "2013-06-18T15:04:43")
@StaticMetamodel(TapOutData.class)
public class TapOutData_ {

	public static volatile SingularAttribute<TapOutData, Integer> id;
	public static volatile SingularAttribute<TapOutData, String> vin;
	public static volatile SingularAttribute<TapOutData, String> serviceProvider;
	public static volatile SingularAttribute<TapOutData, String> codeFunction;
	public static volatile SingularAttribute<TapOutData, GregorianCalendar> date;
	public static volatile ListAttribute<TapOutData, TapGroupData> groups;

}
