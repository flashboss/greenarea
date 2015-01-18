/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import it.vige.greenarea.sgapl.sgot.webservice.wsdata.ResultOperationResponse;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class CommonData implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4050800305556009218L;

	public static enum EventCode { _READYTOLOAD, _LOADED, _REJECTED, _MISSEDDELIVERY, _RETURNED, _DELIVERED };
    public static enum CauseCode { normal, rejected, missedDelivery, rerouted };
    public static enum ResultStatus {OK, NOK };
    public static final String UNSOPPORTED_OPERATION = "UNSOPPORTED_OPERATION";
    public static final String NULL_REF_CONTRACT = "NULL_REF_CONTRACT";
    public static final String UNKNOWN_REF_CONTRACT = "UNKNOWN_REF_CONTRACT";
    
    public static final String REQUEST_SHIPPING_NO_DATA = "REQUEST_SHIPPING_NO_DATA";
    public static final String REQUEST_SHIPPING_NO_DEST = "REQUEST_SHIPPING_NO_DEST";
    public static final String REQUEST_SHIPPING_NO_SOURCE = "REQUEST_SHIPPING_NO_SOURCE";
    public static final String REQUEST_SHIPPING_NO_ITEMS = "REQUEST_SHIPPING_NO_ITEMS";
    public static final String REQUEST_SHIPPING_NO_TRANSPORT = "REQUEST_SHIPPING_NO_TRANSPORT";
    
    public static final String ESTIMATE_SHIPPING_NO_DEST = "ESTIMATE_SHIPPING_NO_DEST";
    public static final String ESTIMATE_SHIPPING_NO_SOURCE = "ESTIMATE_SHIPPING_NO_SOURCE";
    public static final String ESTIMATE_SHIPPING_NO_TRANSPORT = "ESTIMATE_SHIPPING_NO_TRANSPORT";
    
    public static final String CANNOT_LOCATE = "CANNOT_LOCATE";
    public static final String LOCATE_NULL_SHIPPING_ID = "LOCATE_NULL_SHIPPING_ID";
    public static final String LOCATE_UNKNOWN_SHIPPING_ID = "LOCATE_UNKNOWN_SHIPPING_ID";
    public static final String LOCATE_NOT_ALLOWED = "LOCATE_NOT_ALLOWED";

    public static final String CONFIRM_NULL_SHIPPING_ID = "CONFIRM_NULL_SHIPPING_ID";
    public static final String CONFIRM_UNKNOWN_SHIPPING_ID = "CONFIRM_UNKNOWN_SHIPPING_ID";
    public static final String CONFIRM_NOT_ALLOWED = "CONFIRM_NOT_ALLOWED";

    public static final String DROP_NULL_SHIPPING_ID = "DROP_NULL_SHIPPING_ID";
    public static final String DROP_UNKNOWN_SHIPPING_ID = "DROP_UNKNOWN_SHIPPING_ID";
    public static final String DROP_NOT_ALLOWED = "DROP_NOT_ALLOWED";
    public static final String DROP_TRANSPORT_ERROR = "DROP_TRANSPORT_ERROR";

    public static final String GET_TRACKING_NULL_SHIPPING_ID = "GET_TRACKING_NULL_SHIPPING_ID";
    public static final String GET_TRACKING_UNKNOWN_SHIPPING_ID = "GET_TRACKING_UNKNOWN_SHIPPING_ID";
    public static final String GET_TRACKING_NOT_ALLOWED = "GET_TRACKING_NOT_ALLOWED";

    public static final String GET_STATUS_NULL_SHIPPING_ID = "GET_STATUS_NULL_SHIPPING_ID";
    public static final String GET_STATUS_UNKNOWN_SHIPPING_ID = "GET_STATUS_UNKNOWN_SHIPPING_ID";
    public static final String GET_STATUS_NOT_ALLOWED = "GET_STATUS_NOT_ALLOWED";
    
     public static ResultOperationResponse createErrorResponse(String code, String desc) {

        ResultOperationResponse result = new ResultOperationResponse();
        result.setStatus(CommonData.ResultStatus.NOK);
        result.setErrorCode(code);
        result.setErrorDescription(desc);
        return result;
    }

     public static final String REVERSE_PROXY_NAME = "itseasy.vige.com";
}
