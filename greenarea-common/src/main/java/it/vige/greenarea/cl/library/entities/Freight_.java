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

import it.vige.greenarea.cl.library.entities.Attachment;
import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.FreightItemState;
import it.vige.greenarea.cl.library.entities.FreightType;
import it.vige.greenarea.cl.library.entities.Transport;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.3.2.v20111125-r10461", date="2013-06-18T15:04:43")
@StaticMetamodel(Freight.class)
public class Freight_ { 

    public static volatile SingularAttribute<Freight, Integer> weight;
    public static volatile SingularAttribute<Freight, Integer> width;
    public static volatile SingularAttribute<Freight, Boolean> keepUpStanding;
    public static volatile SingularAttribute<Freight, ExchangeStop> dropDownPoint;
    public static volatile SingularAttribute<Freight, String> codeId;
    public static volatile SingularAttribute<Freight, Integer> height;
    public static volatile SingularAttribute<Freight, Transport> transport;
    public static volatile SingularAttribute<Freight, String> description;
    public static volatile SingularAttribute<Freight, Integer> volume;
    public static volatile SingularAttribute<Freight, ExchangeStop> pickUpPoint;
    public static volatile ListAttribute<Freight, Attachment> attachments;
    public static volatile SingularAttribute<Freight, FreightType> ft;
    public static volatile SingularAttribute<Freight, FreightItemState> freightState;
    public static volatile SingularAttribute<Freight, Boolean> stackable;
    public static volatile SingularAttribute<Freight, Integer> leng;

}