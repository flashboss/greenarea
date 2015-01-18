/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import it.vige.greenarea.dto.Peso;
import it.vige.greenarea.dto.TipoParametro;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "EclipseLink-2.3.2.v20111125-r10461", date = "2013-06-18T15:04:43")
@StaticMetamodel(ParameterTS.class)
public class ParameterTS_ {
	public static volatile SingularAttribute<ParameterTS, Integer> id;
	public static volatile SingularAttribute<ParameterTS, TimeSlot> ts;
	public static volatile SingularAttribute<ParameterTS, ParameterGen> parGen;
	public static volatile SingularAttribute<ParameterTS, Double> maxVal;
	public static volatile SingularAttribute<ParameterTS, Double> minVal;
	public static volatile SingularAttribute<ParameterTS, Peso> weight;
	public static volatile SingularAttribute<ParameterTS, TipoParametro> typePar;

}
