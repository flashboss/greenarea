package it.vige.greenarea.bpm.mail;

import org.subethamail.smtp.server.SMTPServer;

public class WiserMain {

	public static void main(String[] args) {
		MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory();
		SMTPServer smtpServer = new SMTPServer(myFactory);
		smtpServer.setPort(25000);
		smtpServer.start();
		while (true);
	}
}
