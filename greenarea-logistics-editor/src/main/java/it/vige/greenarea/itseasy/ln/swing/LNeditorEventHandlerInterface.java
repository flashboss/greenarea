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
package it.vige.greenarea.itseasy.ln.swing;

import java.awt.Window;

public interface LNeditorEventHandlerInterface {
	public enum LNeventType {
		save, cancel, error
	};

	public static class LNeditorEvent {
		private Object userObject = null;
		private String message = "";
		private LNeventType type;

		public LNeditorEvent(Window source, LNeventType type, Object userObject, String message) {
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
