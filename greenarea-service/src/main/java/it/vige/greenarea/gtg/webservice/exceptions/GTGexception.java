/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.webservice.exceptions;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.List;

import org.slf4j.Logger;

/**
 * 
 * @author Administrator
 */
public class GTGexception extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7842631902424984720L;

	private Logger logger = getLogger(getClass());

	public static enum GTGerrorCodes {

		UNSOPPORTED_OPERATION, UNKNOWN_MISSION_ID, UNKNOWN_MISSION_STATE, ALLOCATE_MISSION_NOT_ALLOWED, CLOSE_MISSION_NOT_ALLOWED, NO_MISSION_ALLOCATED, UNKNOWN_FREIGHT_ID, UNKNOWN_FREIGHT_STATE, NOTIFY_FREIGHT_STATUS_NOT_ALLOWED
	};

	private static final EnumMap<GTGerrorCodes, GTGerrorMessage> GTGerrorDesc = new EnumMap<GTGerrorCodes, GTGerrorMessage>(
			GTGerrorCodes.class);

	static {
		GTGerrorDesc.put(GTGerrorCodes.UNSOPPORTED_OPERATION,
				new GTGerrorMessage("Not supported yet.", 0));
		GTGerrorDesc.put(GTGerrorCodes.UNKNOWN_MISSION_ID, new GTGerrorMessage(
				"MissionID {0} is unknown", 1));
		GTGerrorDesc.put(GTGerrorCodes.UNKNOWN_MISSION_STATE,
				new GTGerrorMessage("New Mission State {0} is unknown", 1));
		GTGerrorDesc.put(GTGerrorCodes.UNKNOWN_FREIGHT_STATE,
				new GTGerrorMessage("New Freight State {0} is unknown", 1));
		GTGerrorDesc
				.put(GTGerrorCodes.ALLOCATE_MISSION_NOT_ALLOWED,
						new GTGerrorMessage(
								"Allocate Mission not allowed: user {0} has already a mission allocated",
								1));
		GTGerrorDesc
				.put(GTGerrorCodes.CLOSE_MISSION_NOT_ALLOWED,
						new GTGerrorMessage(
								"Complete Mission not allowed: mission {0} is not assigned to user {1}",
								2));
		GTGerrorDesc.put(GTGerrorCodes.NO_MISSION_ALLOCATED,
				new GTGerrorMessage("User {0} has no mission allocated", 1));
		GTGerrorDesc.put(GTGerrorCodes.UNKNOWN_FREIGHT_ID, new GTGerrorMessage(
				"FreightID {0} is unknown", 1));
		GTGerrorDesc
				.put(GTGerrorCodes.NOTIFY_FREIGHT_STATUS_NOT_ALLOWED,
						new GTGerrorMessage(
								"Cannot change freight status: mission {0} Not allocated to user {1}",
								2));
	};

	private GTGerrorCodes errorCode;
	private String message;

	public GTGexception(GTGerrorCodes errorCode, List<String> variables) {
		super();
		String desc = "Unknown error message...";
		GTGerrorMessage sgoMessage = GTGerrorDesc.get(errorCode);
		logger.debug("&&&&& sgo message: " + sgoMessage.getErrorDescription()
				+ "args:" + sgoMessage.getArgsNum());
		if (variables.size() == sgoMessage.getArgsNum()) {
			desc = MessageFormat.format(sgoMessage.getErrorDescription(),
					variables.toArray());
		}
		this.errorCode = errorCode;
		this.message = desc;
	}

	private static class GTGerrorMessage implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5078754778409011046L;
		private String errorDescription;
		private int argsNum;

		public GTGerrorMessage(String errorDescription, int argsNum) {
			this.errorDescription = errorDescription;
			this.argsNum = argsNum;
		}

		public int getArgsNum() {
			return argsNum;
		}

		public String getErrorDescription() {
			return errorDescription;
		}

		public void setErrorDescription(String errorDescription) {
			this.errorDescription = errorDescription;
		}

		public void setArgsNum(int argsNum) {
			this.argsNum = argsNum;
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
