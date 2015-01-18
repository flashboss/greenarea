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

import it.vige.greenarea.dto.StatoMissione;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "EclipseLink-2.3.2.v20111125-r10461", date = "2013-06-18T15:04:43")
@StaticMetamodel(Mission.class)
public class Mission_ {

	public static volatile SingularAttribute<Mission, Timestamp> startTime;
	public static volatile SingularAttribute<Mission, Long> id;
	public static volatile SingularAttribute<Mission, Timestamp> expireTime;
	public static volatile ListAttribute<Mission, ExchangeStop> exchangeStops;
	public static volatile ListAttribute<Mission, Transport> transports;
	public static volatile SingularAttribute<Mission, Vehicle> truck;
	public static volatile SingularAttribute<Mission, String> description;
	public static volatile ListAttribute<Mission, Attachment> attachments;
	public static volatile SingularAttribute<Mission, StatoMissione> missionState;
	public static volatile SingularAttribute<Mission, String> ownerUser;
	public static volatile SingularAttribute<Mission, String> name;
	public static volatile SingularAttribute<Mission, String> company;
	public static volatile SingularAttribute<Mission, TimeSlot> timeSlot;
	public static volatile SingularAttribute<Mission, String> addressList;
	public static volatile ListAttribute<Mission, ValueMission> valuesMission;
	public static volatile SingularAttribute<Mission, Double> resVikor;

}