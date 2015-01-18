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
			email.setAuthentication("greenareavige@gmail.com", "vulitgreenarea");
			email.setTLS(true);
			email.send();
		} catch (EmailException e) {
			fail();
		}

	}
}
