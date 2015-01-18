package it.vige.greenarea.cl.library.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "EclipseLink-2.3.2.v20111125-r10461", date = "2013-06-18T15:04:43")
@StaticMetamodel(TapGroupData.class)
public class TapGroupData_ {

	public static volatile SingularAttribute<TapGroupData, Integer> id;
	public static volatile SingularAttribute<TapGroupData, String> name;
	public static volatile ListAttribute<TapGroupData, TapParamData> params;
	public static volatile SingularAttribute<TapGroupData, TapOutData> tapOutData;

}
