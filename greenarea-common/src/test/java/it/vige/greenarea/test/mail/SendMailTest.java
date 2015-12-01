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
package it.vige.greenarea.test.mail;

import static org.junit.Assert.fail;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Test;

public class SendMailTest {

	@Test
	public void testSendMailToGoogle() throws Exception {
		HtmlEmail email = new HtmlEmail();
		try {
			email.setSubject("prova");
			email.setHtmlMsg("<div>ciao</div>");
			email.addTo("luca.stancapiano@vige.it");
			email.setSmtpPort(587);
			email.setHostName("smtp.gmail.com");
			email.setFrom("greenareavige@gmail.com");
			email.setAuthentication("greenareavige@gmail.com", "uzouytyrpeocmvaf");
			email.setTLS(true);
			email.setDebug(true);
			email.send();
		} catch (EmailException e) {
			fail();
		}

	}
}
