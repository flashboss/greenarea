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
package it.vige.greenarea.bpm.custom.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VRefresher extends Widget implements Paintable {

	public static final String TAGNAME = "refresher";

	private static final int STOP_THRESHOLD = 0;

	public static final String VARIABLE_REFRESH_EVENT = "r";

	private ApplicationConnection client;
	private final Poller poller;
	private boolean pollerSuspendedDueDetach;

	public VRefresher() {
		setElement(Document.get().createDivElement());
		if (BrowserInfo.get().isIE6()) {
			getElement().getStyle().setProperty("overflow", "hidden");
			getElement().getStyle().setProperty("height", "0");
		}
		poller = new Poller();
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		if (client.updateComponent(this, uidl, true)) {
			return;
		}

		poller.cancel();
		this.client = client;
		if (client.updateComponent(this, uidl, true)) {
			return;
		}

		int pollingInterval = uidl.getIntAttribute("pollinginterval");
		if (pollingInterval > STOP_THRESHOLD) {
			poller.scheduleRepeating(pollingInterval);
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		if (pollerSuspendedDueDetach) {
			poller.run();
		}
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		poller.cancel();
		pollerSuspendedDueDetach = true;
	}

	class Poller extends Timer {
		@Override
		public void run() {
			// Just send something, to trigger the server side event.
			client.updateVariable(client.getPid(getElement()), VARIABLE_REFRESH_EVENT, 0, false);
			client.sendPendingVariableChanges();
		}
	}
}