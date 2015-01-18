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
import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.TruckLoadDescriptor;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.3.2.v20111125-r10461", date="2013-06-18T15:04:43")
@StaticMetamodel(ExchangeStop.class)
public class ExchangeStop_ { 

    public static volatile SingularAttribute<ExchangeStop, Long> id;
    public static volatile ListAttribute<ExchangeStop, Freight> collectingList;
    public static volatile SingularAttribute<ExchangeStop, DBGeoLocation> location;
    public static volatile ListAttribute<ExchangeStop, Freight> deliveryList;
    public static volatile SingularAttribute<ExchangeStop, TruckLoadDescriptor> truckLoad;
    public static volatile ListAttribute<ExchangeStop, Attachment> attachments;
    public static volatile SingularAttribute<ExchangeStop, String> driverNotes;

}