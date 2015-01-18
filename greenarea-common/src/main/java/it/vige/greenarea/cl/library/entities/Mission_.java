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