/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgrl.webservices;

/**
 * 
 * @author 00917308
 */

public class SGRLServiceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5501300832428428806L;

	public enum SGRLServiceError {
		SYSTEM_ERROR, INVALID_PARAMETERS, INVALID_SOURCE_ADDRESS, INVALID_DESTINATION_ADDRESS
	};

	private SGRLServiceError err;

	public SGRLServiceException(SGRLServiceError err) {
		this.err = err;
	}

	public SGRLServiceError getSGRLServiceError() {
		return err;
	}
}
