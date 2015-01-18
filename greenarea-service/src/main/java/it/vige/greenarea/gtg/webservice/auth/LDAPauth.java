/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.webservice.auth;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;


/**
 * 
 * @author 00917377
 */
public class LDAPauth {

	private static Logger logger = getLogger(LDAPauth.class);

	public static String doAuthentication(WebServiceContext wsContext)
			throws LDAPException {

		String result;
		MessageContext mctx = wsContext.getMessageContext();

		Map<String, Object> http_headers = (Map) mctx
				.get(MessageContext.HTTP_REQUEST_HEADERS);
		List<Object> list = (List) http_headers.get("Authorization");

		if (list == null || list.isEmpty()) {
			result = "Authentication failed! This WS needs BASIC Authentication!";
			throw new LDAPException(ResultCode.AUTH_METHOD_NOT_SUPPORTED,
					result);
		}

		String userpass = (String) list.get(0);
		userpass = userpass.substring(5);
		byte[] buf = Base64.decodeBase64(userpass.getBytes());// decodeBase64(userpass.getBytes());

		String credentials = StringUtils.newStringUtf8(buf);
		String username;
		String password;

		int p = credentials.indexOf(":");

		if (p > -1) {

			username = credentials.substring(0, p);

			password = credentials.substring(p + 1);

		} else {

			result = "There was an error while decoding the Authentication!";
			throw new LDAPException(ResultCode.DECODING_ERROR, result);
		}
		/*
		 * Creazione di una "Identity" Se non mi serve un sottodominio, posso
		 * anche usare il costruttore Identity(usr,pwd)
		 */
		logger.debug("*** LOG *** username: " + username + " pwd: " + password);
		logger.debug("*** LOG *** username: " + username + " AUTHORIZED!");
		return username;
	}
}