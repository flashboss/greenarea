/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import it.vige.greenarea.dto.AccessoVeicoli;
import it.vige.greenarea.dto.Color;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "EclipseLink-2.3.2.v20111125-r10461", date = "2013-06-18T15:04:43")
@StaticMetamodel(Price.class)
public class Price_ {
	public static volatile SingularAttribute<Price, Integer> idPrice;
	public static volatile SingularAttribute<Price, TimeSlot> ts;
	public static volatile SingularAttribute<Price, Color> color;
	public static volatile SingularAttribute<Price, Double> maxPrice;
	public static volatile SingularAttribute<Price, Double> minPrice;
	public static volatile SingularAttribute<Price, Double> fixPrice;
	public static volatile SingularAttribute<Price, AccessoVeicoli> typeEntry;

}
