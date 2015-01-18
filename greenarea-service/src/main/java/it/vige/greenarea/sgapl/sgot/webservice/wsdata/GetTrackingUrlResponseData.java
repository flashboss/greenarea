/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class GetTrackingUrlResponseData implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5425316173522163552L;
	private ResultOperationResponse result;
    private String url;

    public GetTrackingUrlResponseData() {
    }

    public GetTrackingUrlResponseData(ResultOperationResponse result) {
        this.result = result;
        this.url = "";
    }

    public ResultOperationResponse getResult() {
        return result;
    }

    public void setResult(ResultOperationResponse result) {
        this.result = result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
