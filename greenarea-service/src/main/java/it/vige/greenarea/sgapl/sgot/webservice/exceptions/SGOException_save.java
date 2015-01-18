/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.webservice.exceptions;

import java.io.Serializable;
import java.util.EnumMap;

/**
 * 
 * @author Administrator
 */
public class SGOException_save extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 676265154073657940L;

	public static enum SGOerrorCodes {

		UNSOPPORTED_OPERATION, NULL_REF_CONTRACT, UNKNOWN_REF_CONTRACT, UNKNOWN_SHIPPING_ID, REQUEST_SHIPPING_NO_DATA, REQUEST_SHIPPING_NO_DEST, REQUEST_SHIPPING_NO_SOURCE, REQUEST_SHIPPING_NO_ITEMS, REQUEST_SHIPPING_NO_TRANSPORT, ESTIMATE_SHIPPING_NO_DATA, ESTIMATE_SHIPPING_NO_DEST, ESTIMATE_SHIPPING_NO_SOURCE, ESTIMATE_SHIPPING_NO_ITEMS, ESTIMATE_SHIPPING_NO_TRANSPORT, CANNOT_LOCATE, LOCATE_NULL_SHIPPING_ID, LOCATE_UNKNOWN_SHIPPING_ID, LOCATE_NOT_ALLOWED, CONFIRM_NULL_SHIPPING_ID, CONFIRM_UNKNOWN_SHIPPING_ID, CONFIRM_NOT_ALLOWED, DROP_NULL_SHIPPING_ID, DROP_UNKNOWN_SHIPPING_ID, DROP_NOT_ALLOWED, DROP_TRANSPORT_ERROR, GET_TRACKING_NULL_SHIPPING_ID, GET_TRACKING_UNKNOWN_SHIPPING_ID, GET_TRACKING_NOT_ALLOWED, GET_STATUS_NULL_SHIPPING_ID, GET_STATUS_UNKNOWN_SHIPPING_ID, GET_STATUS_NOT_ALLOWED
	};

	private static final EnumMap<SGOerrorCodes, SGOerrorMessage> SGOerrorDesc = new EnumMap<SGOerrorCodes, SGOerrorMessage>(
			SGOerrorCodes.class);

	static {
		SGOerrorDesc.put(SGOerrorCodes.UNSOPPORTED_OPERATION,
				new SGOerrorMessage("Not supported yet.", 0));
		SGOerrorDesc.put(SGOerrorCodes.REQUEST_SHIPPING_NO_DATA,
				new SGOerrorMessage(
						"Request Shipping with null ShippingOrderData", 0));
		SGOerrorDesc.put(SGOerrorCodes.NULL_REF_CONTRACT, new SGOerrorMessage(
				"Client Account is null", 0));
		SGOerrorDesc.put(SGOerrorCodes.UNKNOWN_REF_CONTRACT,
				new SGOerrorMessage("Client Account {0} is unknown", 1));
		SGOerrorDesc.put(SGOerrorCodes.UNKNOWN_SHIPPING_ID,
				new SGOerrorMessage("ShippingOrderID {0} is unknown", 1));
		SGOerrorDesc.put(SGOerrorCodes.REQUEST_SHIPPING_NO_DEST,
				new SGOerrorMessage(
						"Request Shipping with null Destination Address", 0));
		SGOerrorDesc.put(SGOerrorCodes.REQUEST_SHIPPING_NO_SOURCE,
				new SGOerrorMessage(
						"Request Shipping with null Source Address", 0));
		SGOerrorDesc.put(SGOerrorCodes.REQUEST_SHIPPING_NO_ITEMS,
				new SGOerrorMessage(
						"Request Shipping with no ShippingItemList", 0));
		SGOerrorDesc.put(SGOerrorCodes.REQUEST_SHIPPING_NO_DATA,
				new SGOerrorMessage(
						"Request Shipping with no ShippingOrder data", 0));
		SGOerrorDesc.put(SGOerrorCodes.REQUEST_SHIPPING_NO_TRANSPORT,
				new SGOerrorMessage("Request Shipping: no transport available",
						0));
		SGOerrorDesc.put(SGOerrorCodes.ESTIMATE_SHIPPING_NO_DEST,
				new SGOerrorMessage(
						"Estimate Shipping with null Destination Address", 0));
		SGOerrorDesc.put(SGOerrorCodes.ESTIMATE_SHIPPING_NO_SOURCE,
				new SGOerrorMessage(
						"Estimate Shipping with null Source Address", 0));
		SGOerrorDesc.put(SGOerrorCodes.ESTIMATE_SHIPPING_NO_ITEMS,
				new SGOerrorMessage(
						"Estimate Shipping with no ShippingItemList", 0));
		SGOerrorDesc.put(SGOerrorCodes.ESTIMATE_SHIPPING_NO_DATA,
				new SGOerrorMessage(
						"Estimate Shipping with no ShippingOrder data", 0));
		SGOerrorDesc.put(SGOerrorCodes.ESTIMATE_SHIPPING_NO_TRANSPORT,
				new SGOerrorMessage(
						"Estimate Shipping: no transport available", 0));
		SGOerrorDesc.put(SGOerrorCodes.CANNOT_LOCATE, new SGOerrorMessage(
				"Cannot Locate shippingID {0}", 1));
		SGOerrorDesc
				.put(SGOerrorCodes.LOCATE_NULL_SHIPPING_ID,
						new SGOerrorMessage(
								"Locate Shipping Request with null ShippingOrderID",
								0));
		SGOerrorDesc
				.put(SGOerrorCodes.LOCATE_UNKNOWN_SHIPPING_ID,
						new SGOerrorMessage(
								"locate Shipping Request: shippingID {0} is unknown",
								1));
		SGOerrorDesc
				.put(SGOerrorCodes.LOCATE_NOT_ALLOWED,
						new SGOerrorMessage(
								"locate Shipping Request not allowed: shippingID {0} status is {1}",
								2));
		SGOerrorDesc
				.put(SGOerrorCodes.CONFIRM_NULL_SHIPPING_ID,
						new SGOerrorMessage(
								"Confirm Shipping Request with null ShippingOrderID",
								0));
		SGOerrorDesc.put(SGOerrorCodes.CONFIRM_UNKNOWN_SHIPPING_ID,
				new SGOerrorMessage(
						"Confirm Shipping Request: shippingID {0} is unknown",
						1));
		SGOerrorDesc
				.put(SGOerrorCodes.CONFIRM_NOT_ALLOWED,
						new SGOerrorMessage(
								"Confirm Shipping Request not allowed: shippingID {0} status is {1}",
								2));
		SGOerrorDesc.put(SGOerrorCodes.DROP_NULL_SHIPPING_ID,
				new SGOerrorMessage(
						"Drop Shipping Request with null ShippingOrderID", 0));
		SGOerrorDesc.put(SGOerrorCodes.DROP_UNKNOWN_SHIPPING_ID,
				new SGOerrorMessage(
						"Drop Shipping Request: shippingID {0} is unknown", 1));
		SGOerrorDesc
				.put(SGOerrorCodes.DROP_NOT_ALLOWED,
						new SGOerrorMessage(
								"Drop Shipping Request not allowed: shippingID {0} status is {1}",
								2));
		SGOerrorDesc
				.put(SGOerrorCodes.DROP_TRANSPORT_ERROR,
						new SGOerrorMessage(
								"Drop Shipping Request not allowed for shippingID {0}: Gat error for transport {1}  is {3}",
								2));
		SGOerrorDesc.put(SGOerrorCodes.GET_TRACKING_NULL_SHIPPING_ID,
				new SGOerrorMessage(
						"Get Tracking Request with null ShippingOrderID", 0));
		SGOerrorDesc.put(SGOerrorCodes.GET_TRACKING_UNKNOWN_SHIPPING_ID,
				new SGOerrorMessage(
						"Get Tracking Request: shippingID {0} is unknown", 1));
		SGOerrorDesc
				.put(SGOerrorCodes.GET_TRACKING_NOT_ALLOWED,
						new SGOerrorMessage(
								"Get Tracking Request not allowed: shippingID {0} status is {1}",
								2));
		SGOerrorDesc.put(SGOerrorCodes.GET_STATUS_NULL_SHIPPING_ID,
				new SGOerrorMessage(
						"Get Status Request with null ShippingOrderID", 0));
		SGOerrorDesc.put(SGOerrorCodes.GET_STATUS_UNKNOWN_SHIPPING_ID,
				new SGOerrorMessage(
						"Get Status Request: shippingID {0} is unknown", 1));
		SGOerrorDesc
				.put(SGOerrorCodes.GET_STATUS_NOT_ALLOWED,
						new SGOerrorMessage(
								"Get Status Requestnot allowed: shippingID {0} status is {1}",
								2));
	};

	private SGOerrorCodes errorCode;
	private String message;

	private static class SGOerrorMessage implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2400761414436922995L;
		private String errorDescription;
		private int argsNum;

		public SGOerrorMessage(String errorDescription, int argsNum) {
			this.errorDescription = errorDescription;
			this.argsNum = argsNum;
		}

		public int getArgsNum() {
			return argsNum;
		}

		public String getErrorDescription() {
			return errorDescription;
		}
	}

	public String getErrorCode() {
		return errorCode.name();
	}

	@Override
	public String getMessage() {
		return message;
	}
}
