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
package it.vige.greenarea.sgrl.webservices;

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
