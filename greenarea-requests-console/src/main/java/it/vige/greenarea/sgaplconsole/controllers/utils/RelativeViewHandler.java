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
package it.vige.greenarea.sgaplconsole.controllers.utils;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class RelativeViewHandler extends ViewHandler {

	private Logger logger = getLogger(getClass());

	ViewHandler defaultHandler;

	public RelativeViewHandler(ViewHandler defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	@Override
	public String getActionURL(final FacesContext context, final String viewId) {
		return getRelativeURL(context,
				this.defaultHandler.getActionURL(context, viewId));
	}

	@Override
	public String getResourceURL(final FacesContext context, final String path) {
		return getRelativeURL(context,
				this.defaultHandler.getResourceURL(context, path));
	}

	/**
	 * Transform the given URL to a relative URL <b>in the context of the
	 * current faces request</b>. If the given URL is not absolute do nothing
	 * and return the given url. The returned relative URL is "equal" to the
	 * original url but will not start with a '/'. So the browser can request
	 * the "same" resource but in a relative way and this is important behind
	 * reverse proxies!
	 * 
	 * @param context
	 * @param theURL
	 * @return
	 */
	private String getRelativeURL(final FacesContext context,
			final String theURL) {
		final HttpServletRequest request = ((HttpServletRequest) context
				.getExternalContext().getRequest());
		logger.debug("Context Path <" + getPath(request)
				+ "> e url originale <" + theURL + ">");
		String result = theURL;
		if (theURL.startsWith("/")) {
			int subpath = StringUtils.countMatches(getPath(request), "/") - 1;
			String pathPrefix = "";
			if (subpath > 0) {
				while (subpath > 0) {
					pathPrefix += "/..";
					subpath--;
				}
				pathPrefix = StringUtils.removeStart(pathPrefix, "/");
			}
			result = pathPrefix + result;
		}
		logger.debug("Result <<<" + result + ">>>>  ");
		logger.debug("--------------------------------------------------------------------");
		return result;
	}

	/**
	 * Get the url-path from the given request.
	 * 
	 * @param request
	 * @return clean path
	 */
	private String getPath(final HttpServletRequest request) {
		try {
			// TODO handle more than two '/'
			return StringUtils.replace(
					new URI(request.getRequestURI()).getPath(), "//", "/");
		} catch (final URISyntaxException e) {
			// XXX URISyntaxException ignored
			return StringUtils.EMPTY;
		}
	}

	@Override
	public Locale calculateLocale(FacesContext context) {
		return defaultHandler.calculateLocale(context);
	}

	@Override
	public String calculateRenderKitId(FacesContext context) {
		return defaultHandler.calculateRenderKitId(context);
	}

	@Override
	public UIViewRoot createView(FacesContext context, String viewId) {
		return defaultHandler.createView(context, viewId);
	}

	@Override
	public void renderView(FacesContext context, UIViewRoot viewToRender)
			throws IOException, FacesException {
		defaultHandler.renderView(context, viewToRender);
	}

	@Override
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		return defaultHandler.restoreView(context, viewId);
	}

	@Override
	public void writeState(FacesContext context) throws IOException {
		defaultHandler.writeState(context);
	}
}
