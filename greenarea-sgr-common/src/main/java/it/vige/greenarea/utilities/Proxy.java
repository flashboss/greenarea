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
package it.vige.greenarea.utilities;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

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
