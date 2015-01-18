/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import it.vige.greenarea.sgapl.sgot.webservice.wsdata.CommonData.ResultStatus;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 */
public class ResultOperationResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4307831224702195935L;
	private ResultStatus status;
	private String errorCode;
	private String errorDescription;

	public ResultOperationResponse() {
	}

	public ResultOperationResponse(ResultStatus status) {
		this.status = status;
		errorCode = "";
		errorDescription = "";
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public ResultStatus getStatus() {
		return status;
	}

	public void setStatus(ResultStatus status) {
		this.status = status;
	}
}
