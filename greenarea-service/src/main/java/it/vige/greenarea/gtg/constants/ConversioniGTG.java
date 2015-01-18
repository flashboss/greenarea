package it.vige.greenarea.gtg.constants;

import static javax.xml.datatype.DatatypeFactory.newInstance;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.sgrl.webservices.ObjectFactory;
import it.vige.greenarea.sgrl.webservices.SgrlNode;
import it.vige.greenarea.cl.library.entities.Attachment;
import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.ShippingItem;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.TapGroupData;
import it.vige.greenarea.cl.library.entities.TapOutData;
import it.vige.greenarea.cl.library.entities.TapParamData;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.dto.Address;
import it.vige.greenarea.dto.ExchangeSite;
import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.GeoLocationInterface;
import it.vige.greenarea.gtg.webservice.wsdata.GeoReference;
import it.vige.greenarea.itseasy.lib.mqData.MY_Attachment;
import it.vige.greenarea.itseasy.lib.mqData.MqFreightData;
import it.vige.greenarea.itseasy.lib.mqData.MqShippingData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.ShippingItemData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.ShippingOrderData;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.ShippingOrderDetails;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.TransportInfo;
import it.vige.greenarea.tap.spreceiver.ws.GroupData;
import it.vige.greenarea.tap.spreceiver.ws.OutData;
import it.vige.greenarea.tap.spreceiver.ws.ParamData;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;

public class ConversioniGTG {

	private static Logger logger = getLogger(ConversioniGTG.class);

	private static ObjectFactory obj = new it.vige.greenarea.sgrl.webservices.ObjectFactory();

	public static ParamData convertiTapParamDataToParamData(
			TapParamData tapParamData) {
		ParamData paramData = new ParamData();
		paramData.setName(tapParamData.getName());
		paramData.setValue(tapParamData.getValue());
		return paramData;
	}

	public static TapParamData convertiParamDataToTapParamData(
			ParamData paramData) {
		logParamData(paramData);
		TapParamData tapParamData = new TapParamData();
		try {
			tapParamData.setName(paramData.getName());
			tapParamData.setValue(paramData.getValue());
		} catch (Exception ex) {
			logger.error("errore", ex);
		}
		return tapParamData;
	}

	public static GroupData convertiTapGroupDataToGroupData(
			TapGroupData tapGroupData) {
		GroupData groupData = new GroupData();
		groupData.setName(tapGroupData.getName());
		groupData.getParams().addAll(
				convertiTapParamDatasToParamDatas(tapGroupData.getParams()));
		return groupData;
	}

	public static TapGroupData convertiGroupDataToTapGroupData(
			GroupData groupData) {
		logGroupData(groupData);
		TapGroupData tapGroupData = new TapGroupData();
		try {
			tapGroupData.setName(groupData.getName());
			tapGroupData.getParams().addAll(
					convertiParamDatasToTapParamDatas(groupData.getParams()));
		} catch (Exception ex) {
			logger.error("errore", ex);
		}
		return tapGroupData;
	}

	public static OutData convertiTapOutDataToOutData(TapOutData tapOutData) {
		OutData outData = new OutData();
		outData.setCodeFunction(tapOutData.getCodeFunction());
		try {
			outData.setDate(newInstance().newXMLGregorianCalendar(
					tapOutData.getDate()));
		} catch (DatatypeConfigurationException e) {
			logger.error("Errore di conversione del XMLGregorianCalendar");
		}
		outData.setServiceProvider(tapOutData.getServiceProvider());
		outData.setVin(tapOutData.getVin());
		outData.getGroups().addAll(
				convertiTapGroupDatasToGroupDatas(tapOutData.getGroups()));
		return outData;
	}

	public static TapOutData convertiOutDataToTapOutData(OutData outData) {
		logOutData(outData);
		TapOutData tapOutData = new TapOutData();
		try {
			tapOutData.setCodeFunction(outData.getCodeFunction());
			tapOutData.setDate(outData.getDate().toGregorianCalendar());
			tapOutData.setServiceProvider(outData.getServiceProvider());
			tapOutData.setVin(outData.getVin());
			tapOutData.getGroups().addAll(
					convertiGroupDatasToTapGroupDatas(outData.getGroups()));
		} catch (Exception ex) {
			logger.error("errore", ex);
		}
		return tapOutData;
	}

	public static List<OutData> convertiTapOutDatasToOutDatas(
			List<TapOutData> tapOutDatas) {
		List<OutData> outDatas = new ArrayList<OutData>();
		for (TapOutData tapOutData : tapOutDatas) {
			outDatas.add(convertiTapOutDataToOutData(tapOutData));
		}
		return outDatas;
	}

	public static List<TapOutData> convertiOutDatasToTapOutDatas(
			List<OutData> outDatas) {
		List<TapOutData> tapOutDatas = new ArrayList<TapOutData>();
		for (OutData outData : outDatas) {
			tapOutDatas.add(convertiOutDataToTapOutData(outData));
		}
		return tapOutDatas;
	}

	public static List<GroupData> convertiTapGroupDatasToGroupDatas(
			List<TapGroupData> tapGroupDatas) {
		List<GroupData> groupDatas = new ArrayList<GroupData>();
		for (TapGroupData tapGroupData : tapGroupDatas) {
			groupDatas.add(convertiTapGroupDataToGroupData(tapGroupData));
		}
		return groupDatas;
	}

	public static List<TapGroupData> convertiGroupDatasToTapGroupDatas(
			List<GroupData> groupDatas) {
		List<TapGroupData> tapGroupDatas = new ArrayList<TapGroupData>();
		for (GroupData groupData : groupDatas) {
			tapGroupDatas.add(convertiGroupDataToTapGroupData(groupData));
		}
		return tapGroupDatas;
	}

	public static List<ParamData> convertiTapParamDatasToParamDatas(
			List<TapParamData> tapParamDatas) {
		List<ParamData> paramDatas = new ArrayList<ParamData>();
		for (TapParamData tapParamData : tapParamDatas) {
			paramDatas.add(convertiTapParamDataToParamData(tapParamData));
		}
		return paramDatas;
	}

	public static List<TapParamData> convertiParamDatasToTapParamDatas(
			List<ParamData> paramDatas) {
		List<TapParamData> tapParamDatas = new ArrayList<TapParamData>();
		for (ParamData paramData : paramDatas) {
			tapParamDatas.add(convertiParamDataToTapParamData(paramData));
		}
		return tapParamDatas;
	}

	public static Address convertiIndirizzo(DBGeoLocation indirizzo) {
		Address address = new Address();
		address.setCity(indirizzo.getCity());
		address.setCountry(indirizzo.getCountry());
		address.setNumber(indirizzo.getNumber());
		address.setProvince(indirizzo.getAdminAreaLevel1());
		address.setRegion(indirizzo.getAdminAreaLevel2());
		address.setStreet(indirizzo.getStreet());
		address.setZipCode(indirizzo.getZipCode());
		return address;
	}

	public static DBGeoLocation convertiAddressToDBGeoLocation(Address indirizzo) {
		DBGeoLocation address = new DBGeoLocation();
		address.setCity(indirizzo.getCity());
		address.setCountry(indirizzo.getCountry());
		address.setNumber(indirizzo.getNumber());
		address.setAdminAreaLevel1(indirizzo.getProvince());
		address.setAdminAreaLevel2(indirizzo.getRegion());
		address.setStreet(indirizzo.getStreet());
		address.setZipCode(indirizzo.getZipCode());
		return address;
	}

	public static ShippingOrderData convertiRichiesta(ShippingOrder richiesta) {
		ShippingOrderData shippingOrderData = new ShippingOrderData(
				richiesta.getId());
		shippingOrderData.setNote(richiesta.getNote());
		shippingOrderData.setTerminiDiConsegna(richiesta.getDeliveryTerms());
		shippingOrderData.setShippingItems(convertiPacchi(richiesta
				.getShippingItems()));
		shippingOrderData.setToName(richiesta.getDestinatario().getName());
		shippingOrderData.setFromName(richiesta.getMittente().getName());
		shippingOrderData.setToAddress(convertiIndirizzo(richiesta
				.getDestinatario()));
		shippingOrderData.setFromAddress(convertiIndirizzo(richiesta
				.getMittente()));
		shippingOrderData.setOperatoreLogistico(richiesta
				.getOperatoreLogistico());
		return shippingOrderData;
	}

	public static List<ShippingOrder> convertiShippingOrderDataToShippingOrders(
			List<ShippingOrderData> richieste) {
		List<ShippingOrder> shippingOrders = null;
		if (richieste != null) {
			shippingOrders = new ArrayList<ShippingOrder>();
			for (ShippingOrderData richiesta : richieste)
				shippingOrders
						.add(convertiShippingOrderDataToShippingOrder(richiesta));
		}
		return shippingOrders;
	}

	public static ShippingOrder convertiShippingOrderDataToShippingOrder(
			ShippingOrderData richiesta) {
		ShippingOrder shippingOrder = new ShippingOrder();
		shippingOrder.setId(richiesta.getShipmentId());
		shippingOrder.setNote(richiesta.getNote());
		shippingOrder.setDeliveryTerms(richiesta.getTerminiDiConsegna());
		shippingOrder
				.setShippingItems(convertiShippingItemDataToShippingItems(richiesta
						.getShippingItems()));
		shippingOrder.setMittente(convertiAddressToDBGeoLocation(richiesta
				.getFromAddress()));
		shippingOrder.setDestinatario(convertiAddressToDBGeoLocation(richiesta
				.getToAddress()));
		shippingOrder.getDestinatario().setName(richiesta.getToName());
		shippingOrder.getMittente().setName(richiesta.getFromName());
		shippingOrder.setOperatoreLogistico(richiesta.getOperatoreLogistico());
		return shippingOrder;
	}

	public static ShippingItem convertiShippingItemDataToShippingItem(
			ShippingItemData richiesta) {
		ShippingItem shippingItem = new ShippingItem();
		shippingItem.setAttributes(richiesta.getAttributi());
		shippingItem.setDescription(richiesta.getDescrizione());
		shippingItem.setId(richiesta.getItemID());
		return shippingItem;
	}

	public static List<ShippingItem> convertiShippingItemDataToShippingItems(
			ShippingItemData[] items) {
		List<ShippingItem> shippingItems = new ArrayList<ShippingItem>();
		for (ShippingItemData si : items) {
			shippingItems.add(convertiShippingItemDataToShippingItem(si));
		}
		return shippingItems;
	}

	public static ShippingItemData[] convertiPacchi(
			List<ShippingItem> shippingItems) {
		ShippingItemData[] shippingItemsData = new ShippingItemData[shippingItems
				.size()];
		if (shippingItems != null)
			for (int i = 0; i < shippingItems.size(); i++) {
				ShippingItem shippingItem = shippingItems.get(i);
				ShippingItemData shippingItemData = new ShippingItemData();
				shippingItemData.setAttributi(shippingItem.getAttributes());
				shippingItemData.setDescrizione(shippingItem.getDescription());
				shippingItemData.setItemID(shippingItem.getId());
				shippingItemsData[i] = shippingItemData;
			}
		return shippingItemsData;
	}

	public static Attachment convertMyAttachment(MY_Attachment my) {

		Attachment result = new Attachment();
		result.setName(my.getName());
		result.setContents(my.getContents());
		return result;
	}

	public static GeoReference convertDBLocation(DBGeoLocation s) {
		GeoReference result = new GeoReference();
		result.setName(s.getName());
		result.setSurname(s.getSurname());
		result.setPhone(s.getPhone());
		result.setMobile(s.getMobile());
		result.setEmail(s.getEmail());
		result.setGeoLocation(new GeoLocation(s));
		return result;
	}

	public static ShippingOrderDetails convertShippingOrderToShippingOrderDetails(
			ShippingOrder so) {
		if (so == null) {
			return null;
		}
		ShippingOrderDetails soe = new ShippingOrderDetails();
		soe.setCost(so.getCost());
		soe.setCreationTimestamp(so.getCreationTimestamp().toString());
		soe.setDestinatario(so.getDestinatario());
		soe.setId(so.getId());
		soe.setMittente(so.getMittente());
		soe.setNote(so.getNote());
		for (ShippingItem si : so.getShippingItems()) {
			soe.getItemsList().add(
					new ShippingItemData(si.getId(), si.getDescription(), si
							.getAttributes()));
		}
		soe.setOrdinante(so.getCustomer().toString());
		soe.setStato(so.getOrderStatus().name());
		soe.setTerminiDiConsegna(so.getDeliveryTerms());
		soe.setTrackingURL(so.getTrackingURL());
		soe.setTransportID(so.getTransport().getAlfacode());
		soe.setOperatoreLogistico(so.getOperatoreLogistico());
		return soe;
	}

	public static ExchangeSite fromSgrlToSgot(SgrlNode sgrlObj) {
		ExchangeSite sgotObj = new ExchangeSite();
		sgotObj.setName(sgrlObj.getName());
		GeoLocationInterface geoLocationInterface = new GeoLocation();
		geoLocationInterface.setAdminAreaLevel1(sgrlObj.getNameDetails());
		sgotObj.setLocation(geoLocationInterface);
		sgotObj.setDescription(sgrlObj.getNameDetails());
		sgotObj.getAttributes().put("Location_notes", sgrlObj.getNameDetails());
		return sgotObj;
	}

	public static it.vige.greenarea.sgrl.webservices.GeoLocation fromSgotToSgrl(
			GeoLocationInterface add) {
		it.vige.greenarea.sgrl.webservices.GeoLocation geoLoc = obj
				.createGeoLocation();
		geoLoc.setCity(add.getCity());
		geoLoc.setCountry(add.getCountry());
		geoLoc.setNumber(add.getNumber());
		geoLoc.setAdminAreaLevel1(add.getAdminAreaLevel1());
		geoLoc.setAdminAreaLevel2(add.getAdminAreaLevel2());
		geoLoc.setStreet(add.getStreet());
		geoLoc.setZipCode(add.getZipCode());
		geoLoc.setLatitude(add.getLatitude());
		geoLoc.setLongitude(add.getLongitude());
		geoLoc.setRadius(0);
		return geoLoc;
	}

	public static GeoLocationInterface fromSgrlToSgot(
			it.vige.greenarea.sgrl.webservices.GeoLocation add) {
		GeoLocationInterface geoLoc = new DBGeoLocation();
		geoLoc.setCity(add.getCity());
		geoLoc.setCountry(add.getCountry());
		geoLoc.setNumber(add.getNumber());
		geoLoc.setAdminAreaLevel1(add.getAdminAreaLevel1());
		geoLoc.setAdminAreaLevel2(add.getAdminAreaLevel2());
		geoLoc.setStreet(add.getStreet());
		geoLoc.setZipCode(add.getZipCode());
		geoLoc.setLatitude(add.getLatitude());
		geoLoc.setLongitude(add.getLongitude());
		geoLoc.setRadius(add.getRadius());
		return geoLoc;
	}

	public static MqFreightData fromSgotToMQ(ShippingItem si) {
		MqFreightData sid = new MqFreightData(si.getId(), si.getDescription(),
				si.getAttributes());
		return sid;
	}

	public static List<MqFreightData> fromSgotToMQ(List<ShippingItem> siList) {
		List<MqFreightData> sidList = new ArrayList<MqFreightData>();
		for (ShippingItem si : siList) {
			sidList.add(fromSgotToMQ(si));
		}
		return sidList;
	}

	public static MqShippingData fromSgotToMQ(ShippingOrder so) {
		Transport t = so.getTransport();

		DBGeoLocation pickup = new DBGeoLocation(t.getRoute()
				.get(t.getActiveLegIndex()).getSource().getName(), "", "", "",
				"", t.getRoute().get(t.getActiveLegIndex()).getSource()
						.getLocation());
		DBGeoLocation dropdown = new DBGeoLocation(t.getRoute()
				.get(t.getActiveLegIndex()).getDestination().getName(), "", "",
				"", "", t.getRoute().get(t.getActiveLegIndex())
						.getDestination().getLocation());

		MqShippingData sod = new MqShippingData(so.getId(), t.getAlfacode(),
				so.getMittente(), so.getDestinatario(), pickup, dropdown,
				"FURGONATO", fromSgotToMQ(so.getShippingItems()), so
						.getTransport().getAttributes(),
				new ArrayList<MY_Attachment>());
		return sod;
	}

	public static TransportInfo convertTransportToTransportInfo(Transport t) {
		if (t == null) {
			return null;
		}
		TransportInfo ti = new TransportInfo();
		ti.setId(t.getAlfacode());
		ti.setOrderID(t.getShippingOrder().getId());
		ti.setStatus(t.getTransportState().name());

		ti.setVettore(t.getRoute().get(t.getActiveLegIndex()).getVector());
		ti.setSourceSite(t.getRoute().get(t.getActiveLegIndex()).getSource()
				.getName());
		ti.setDestinationSite(t.getRoute().get(t.getActiveLegIndex())
				.getDestination().getName());

		return ti;
	}

	public static void logOutData(OutData outData) {
		if (outData != null) {
			logger.debug("outData.getCodeFunction() = "
					+ outData.getCodeFunction());
			logger.debug("outData.getServiceProvider() = "
					+ outData.getServiceProvider());
			logger.debug("outData.getVin() = " + outData.getVin());
			logger.debug("outData.getDate() = " + outData.getDate());
			List<GroupData> groups = outData.getGroups();
			if (groups != null) {
				for (GroupData groupData : groups) {
					logGroupData(groupData);
				}
			}
		}
	}

	public static void logGroupData(GroupData groupData) {
		if (groupData != null) {
			logger.debug("       groupData.getName() = " + groupData.getName());
			List<ParamData> paramDatas = groupData.getParams();
			if (paramDatas != null) {
				for (ParamData paramData : paramDatas) {
					logParamData(paramData);
				}
			}
		}
	}

	public static void logParamData(ParamData paramData) {
		if (paramData != null) {
			logger.debug("                   paramData.getName() = "
					+ paramData.getName());
			logger.debug("                   paramData.getValue() = "
					+ paramData.getValue());
		}
	}

}
