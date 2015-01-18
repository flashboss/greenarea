/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgaplconsole.controllers.utils;

/**
 *
 * @author 00917377
 */

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;

public class ReverseProxyViewHandler extends ViewHandler {

	private Logger logger = getLogger(getClass());

	ViewHandler defaultHandler;

	public ReverseProxyViewHandler(ViewHandler defaultHandler) {
		this.defaultHandler = defaultHandler;
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

	/*
	 * @Override public String getActionURL(FacesContext context, String viewId)
	 * { String actionURL = defaultHandler.getActionURL(context, viewId); String
	 * prefijoProxy =
	 * context.getExternalContext().getInitParameter("es.acme.faces.PROXY_PREFIX"
	 * );
	 * 
	 * if (prefijoProxy == null) { return actionURL; } else { return
	 * prefijoProxy + actionURL; } }
	 * 
	 * @Override public String getResourceURL(FacesContext context, String path)
	 * { return defaultHandler.getResourceURL(context, path); }
	 */

	@Override
	public String getActionURL(FacesContext context, String viewId) {

		String actionURL = defaultHandler.getActionURL(context, viewId);

		String res = getProxyiedURL(context, actionURL);
		logger.debug("result in getActionURL = " + res);
		return res;
	}

	@Override
	public String getResourceURL(FacesContext context, String path) {
		// String res = getProxyiedURL(context, path);
		String res = getProxyiedURL(context, path);
		logger.debug("result in getResourceURL = " + res);
		logger.debug("result in getResourceURL = " + res);
		return res;
	}

	/**
	 * Actual string replacement. Simply replaces the context path in the passed
	 * URL. For the root context, (/) just removes the context path.
	 * 
	 * @param context
	 *            Faces context (used to retrieve application context path and
	 *            init parameter)
	 * @param path
	 *            Action or resource path to be modified
	 * @return URL with changed context path
	 */
	private String getProxyiedURL(FacesContext context, String path) {

		String contextPath = context.getExternalContext()
				.getRequestContextPath();
		String proxyPath = context.getExternalContext().getInitParameter(
				"it.vige.greenarea.sgaplconsole.PROXY_CONTEXT_PATH");
		logger.debug("contextPath = " + contextPath + " proxyPath = "
				+ proxyPath);

		// For root proxy context, simply remove context path
		if ("/".equals(proxyPath)) {
			return path.replaceFirst(contextPath, "");
		} else {
			return path.replaceFirst(contextPath, proxyPath);
		}
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
