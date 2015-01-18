/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.ln.swing;

import java.awt.Window;

/**
 * 
 * @author 00917308
 */
public interface LNeditorEventHandlerInterface {
	public enum LNeventType {
		save, cancel, error
	};

	public static class LNeditorEvent {
		private Object userObject = null;
		private String message = "";
		private LNeventType type;

		public LNeditorEvent(Window source, LNeventType type,
				Object userObject, String message) {
			this(type, userObject, message);
		}

		LNeditorEvent(LNeventType type, Object userObject, String message) {
			this.type = type;
			this.userObject = userObject;
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public LNeventType getType() {
			return type;
		}

		public Object getUserObject() {
			return userObject;
		}

		public void setUserObject(Object userObject) {
			this.userObject = userObject;
		}
	}

	public void handleEvent(LNeditorEvent evt);
}
