/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.vige.greenarea.bpm.custom.servlet;

import static org.activiti.explorer.ExplorerApp.get;

import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.activiti.explorer.servlet.ExplorerApplicationServlet;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.SessionExpiredException;

/**
 * Servlet providing Activiti and Spring integration with Vaadin
 * 
 * @author Patrick Oberg
 * @author Joram Barrez
 */
public class GreenareaExplorerApplicationServlet extends ExplorerApplicationServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected Application getExistingApplication(HttpServletRequest request, boolean allowSessionCreation)
			throws MalformedURLException, SessionExpiredException {
		return (Application) get();
	}
}
