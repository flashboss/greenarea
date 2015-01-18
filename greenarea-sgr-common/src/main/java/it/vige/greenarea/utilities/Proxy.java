/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.utilities;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 *
 * @author 00917308
 */
public class Proxy {
        public static void initialize() {
            String proxySet = Application.getProperty("proxySet");
            String p;

            if( proxySet !=null)
                if(proxySet.equals("true")){
                System.setProperty("proxySet", "true");
                p= Application.getProperty("http.proxyHost");
                    if( p!= null ) System.setProperty("http.proxyHost", p );
                p=Application.getProperty("http.proxyPort");
                    if( p!= null ) System.setProperty("http.proxyPort", p);
                p= Application.getProperty("http.nonProxyHosts");
                    if( p!= null ) System.setProperty("http.nonProxyHosts", p); //Per loquendo deve avere non proxy
                p=Application.getProperty("http.proxyUser");
                    if( p!= null ) System.setProperty("http.proxyUser", p);
                p=Application.getProperty("http.proxyPassword");
                    if( p!= null ) System.setProperty("http.proxyPassword", p);

                class MyAuthenticator extends Authenticator {

                /**
                 * Called when password authorization is needed.
                 * @return The PasswordAuthentication collected from the
                 * user, or null if none is provided.
                 */
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        //return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
                        return new PasswordAuthentication(Application.getProperty("http.proxyUser"), Application.getProperty("http.proxyPassword").toCharArray());
                    }
                }
                Authenticator.setDefault(new MyAuthenticator());
            }
            else 
            if( proxySet.equals("false"))  System.setProperty("proxySet", "false");
            else { /* do nothing */ }
    }
}
