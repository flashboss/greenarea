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

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import it.vige.greenarea.dto.AperturaRichieste;
import it.vige.greenarea.dto.ChiusuraRichieste;
import it.vige.greenarea.dto.Ripetizione;
import it.vige.greenarea.dto.TipologiaClassifica;
import it.vige.greenarea.dto.Tolleranza;

@Generated(value = "EclipseLink-2.3.2.v20111125-r10461", date = "2013-06-18T15:04:43")
@StaticMetamodel(TimeSlot.class)
public class TimeSlot_ {

	public static volatile SingularAttribute<TimeSlot, Integer> idTS;
	public static volatile SingularAttribute<TimeSlot, AperturaRichieste> timeToAcceptRequest;
	public static volatile SingularAttribute<TimeSlot, ChiusuraRichieste> timeToStopRequest;
	public static volatile SingularAttribute<TimeSlot, ChiusuraRichieste> timeToRun;
	public static volatile SingularAttribute<TimeSlot, Tolleranza> tollerance;
	public static volatile SingularAttribute<TimeSlot, Ripetizione> wmy;
	public static volatile SingularAttribute<TimeSlot, String> startTS;
	public static volatile SingularAttribute<TimeSlot, String> finishTS;
	public static volatile SingularAttribute<TimeSlot, String> dayStart;
	public static volatile SingularAttribute<TimeSlot, String> dayFinish;
	public static volatile SingularAttribute<TimeSlot, TipologiaClassifica> vikInd;
	public static volatile SingularAttribute<TimeSlot, String> pa;
	public static volatile SingularAttribute<TimeSlot, String> roundCode;
}
