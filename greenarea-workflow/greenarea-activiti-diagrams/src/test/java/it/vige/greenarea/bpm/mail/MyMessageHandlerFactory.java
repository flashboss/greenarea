package it.vige.greenarea.bpm.mail;

//-------------------------------------------------------------------
//MyMessageHandlerFactory.java
//-------------------------------------------------------------------
import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;

public class MyMessageHandlerFactory implements MessageHandlerFactory {

	private Logger logger = getLogger(getClass());

	public MessageHandler create(MessageContext ctx) {
		return new Handler(ctx);
	}

	class Handler implements MessageHandler {
		MessageContext ctx;

		public Handler(MessageContext ctx) {
			this.ctx = ctx;
		}

		public void from(String from) throws RejectException {
			logger.info("FROM:" + from);
		}

		public void recipient(String recipient) throws RejectException {
			logger.info("RECIPIENT:" + recipient);
		}

		public void data(InputStream data) throws IOException {
			logger.info("MAIL DATA");
			logger.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
			logger.info(this.convertStreamToString(data));
			logger.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
		}

		public void done() {
			logger.info("Finished");
		}

		public String convertStreamToString(InputStream is) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				logger.error("activiti diagram", e);
			}
			return sb.toString();
		}

	}
}