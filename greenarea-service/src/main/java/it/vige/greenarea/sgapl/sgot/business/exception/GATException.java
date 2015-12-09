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
package it.vige.greenarea.sgapl.sgot.business.exception;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.List;

import org.slf4j.Logger;

public class GATException extends Exception {

	private static final long serialVersionUID = 3941331316728269508L;

	private Logger logger = getLogger(getClass());

	public static enum GATerrorCodes {
		UNSOPPORTED_OPERATION, GET_TRANSPORT_NULL_SHIPPING_ID, GET_TRANSPORT_NO_DEST, GET_TRANSPORT_NO_SOURCE, ESTIMATE_TRANSPORT_NO_SOURCE, ESTIMATE_TRANSPORT_NO_DEST, UNKNOWN_TRANSPORT_ID, NO_TRANSPORT, CANNOT_LOCATE, CANNOT_ESTIMATE,
	};

	private static final EnumMap<GATerrorCodes, GATerrorMessage> GATerrorDesc = new EnumMap<GATerrorCodes, GATerrorMessage>(
			GATerrorCodes.class);

	static {
		GATerrorDesc.put(GATerrorCodes.UNSOPPORTED_OPERATION, new GATerrorMessage("Not supported yet.", 0));
		GATerrorDesc.put(GATerrorCodes.GET_TRANSPORT_NULL_SHIPPING_ID, new GATerrorMessage("Shipping ID is null", 0));
		GATerrorDesc.put(GATerrorCodes.GET_TRANSPORT_NO_DEST,
				new GATerrorMessage("GetTransport request with null Destination Address", 0));
		GATerrorDesc.put(GATerrorCodes.ESTIMATE_TRANSPORT_NO_SOURCE,
				new GATerrorMessage("EstimateTransport request with null Source Address", 0));
		GATerrorDesc.put(GATerrorCodes.ESTIMATE_TRANSPORT_NO_DEST,
				new GATerrorMessage("EstimateTransport request with null Destination Address", 0));
		GATerrorDesc.put(GATerrorCodes.GET_TRANSPORT_NO_SOURCE,
				new GATerrorMessage("GetTransport request with null Source Address", 0));
		GATerrorDesc.put(GATerrorCodes.UNKNOWN_TRANSPORT_ID, new GATerrorMessage("Transport ID {0} is unknown", 1));
		GATerrorDesc.put(GATerrorCodes.NO_TRANSPORT,
				new GATerrorMessage("No transport available for ShippinhOrderID={0}", 1));
		GATerrorDesc.put(GATerrorCodes.CANNOT_LOCATE, new GATerrorMessage("Cannot locate ShippinhOrderID={0}", 1));
		GATerrorDesc.put(GATerrorCodes.CANNOT_ESTIMATE,
				new GATerrorMessage("Cannot estimate cost for transport from {0} to {1}", 2));
	};

	private GATerrorCodes errorCode;
	private String message;

	private static class GATerrorMessage implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3701959697854738767L;
		private String errorDescription;
		private int argsNum;

		public GATerrorMessage(String errorDescription, int argsNum) {
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

	public GATException(GATerrorCodes errorCode, List<String> variables) {
		super();
		String desc = "Unknown error message...";
		GATerrorMessage gatMessage = GATerrorDesc.get(errorCode);
		logger.debug("%%% GAT message: " + gatMessage.getErrorDescription() + "args:" + gatMessage.getArgsNum());
		if (variables.size() == gatMessage.getArgsNum()) {
			desc = MessageFormat.format(gatMessage.getErrorDescription(), variables.toArray());
		}
		this.errorCode = errorCode;
		this.message = desc;
	}

	public String getErrorCode() {
		return errorCode.name();
	}

	@Override
	public String getMessage() {

		return message;
	}
}
