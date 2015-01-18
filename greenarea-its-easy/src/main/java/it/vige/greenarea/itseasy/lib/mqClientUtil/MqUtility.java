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
package it.vige.greenarea.itseasy.lib.mqClientUtil;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.slf4j.Logger;

public class MqUtility {

	public static final int MSG_message = 0;
	public static final int MSG_text = 1;
	public static final int MSG_byte = 2;
	public static final int MSG_map = 3;
	public static final int MSG_object = 4;
	public static final int MSG_stream = 5;
	public static final int MSG_unknown = -1;

	private static Logger logger = getLogger(MqUtility.class);

	public static String[] pad = { "", "0", "00", "000", "0000" };

	// private static Context jndiContext = null;
	// private static ItseasyStoreInfo currentStore = null;

	/**
	 * Returns a ConnectionFactory object.
	 * 
	 * @return a ConnectionFactory object
	 * @throws javax.naming.NamingException
	 *             (or other exception) if name cannot be found
	 */
	/*
	 * public static ConnectionFactory getConnectionFactory( ItseasyStoreInfo
	 * storeInfo, String name) throws Exception { return (ConnectionFactory)
	 * jndiLookup(storeInfo, name); }
	 */

	/**
	 * Returns a Destination object.
	 * 
	 * @param name
	 *            String specifying Destination name
	 * @param storeInfo
	 *            a ItseasyStoreInfo specifying the Object Store
	 * 
	 * @return a Destination object
	 * @throws javax.naming.NamingException
	 *             (or other exception) if name cannot be found
	 */
	/*
	 * public static Destination getDestination(ItseasyStoreInfo storeInfo,
	 * String name) throws Exception { return (Destination)
	 * jndiLookup(storeInfo, name); }
	 */

	/**
	 * Creates a JNDI API InitialContext object if none exists yet. Then looks
	 * up the string argument and returns the associated object.
	 * 
	 * @param objFactoryType
	 *            the object store type: file system or LDAP
	 * @param name
	 *            the name of the object to be looked up
	 * @param store
	 *            a object store name
	 * @param principal
	 *            security information for LDAP store
	 * @param credentials
	 *            security information for LDAP store
	 * 
	 * @return the object bound to name
	 * @throws javax.naming.NamingException
	 *             (or other exception) if name cannot be found
	 */

	public static void dumpException(Exception e) {
		Exception linked = null;

		if (e instanceof JMSException) {
			linked = ((JMSException) e).getLinkedException();
		}

		logger.error("dump exception", e);
		if (linked != null) {
			logger.error("its easy", linked);
		}
	}

	public static Map<String, String> getMessageProperties(Message m)
			throws JMSException {
		Enumeration<String> propNames = m.getPropertyNames();

		Map<String, String> result = new HashMap<String, String>();
		while (propNames.hasMoreElements()) {
			String name = propNames.nextElement();
			result.put(name, m.getStringProperty(name));
		}
		logger.debug("per il messaggio: " + m.toString()
				+ " ho trovato questa lista: " + result.toString());

		return result;
	}

	public static String getDestination(Message m) {
		Destination d;
		String s;
		try {
			d = m.getJMSDestination();
			if (d != null) {
				if (d instanceof Queue) {
					s = "Queue: " + ((Queue) d).getQueueName();
				} else {
					s = "Topic: " + ((Topic) d).getTopicName();
				}
			} else {
				s = "Unknown";
			}
		} catch (JMSException ex) {
			logger.error("get destination", ex);
			return "No dest";
		}

		return s;
	}

	/**
	 * Return a string description of the type of JMS message
	 */
	public static int messageType(Message m) {
		if (m instanceof TextMessage) {
			return MSG_text;
		} else if (m instanceof BytesMessage) {
			return MSG_byte;
		} else if (m instanceof MapMessage) {
			return MSG_map;
		} else if (m instanceof ObjectMessage) {
			return MSG_object;
		} else if (m instanceof StreamMessage) {
			return MSG_stream;
		} else if (m instanceof Message) {
			return MSG_message;
		} else {
			// Unknown Message type
			/*
			 * String type = m.getClass().getName(); StringTokenizer st = new
			 * StringTokenizer(type, "."); String s = null;
			 * 
			 * while (st.hasMoreElements()) { s = st.nextToken(); }
			 */

			return MSG_unknown;
		}
	}

	/**
	 * Return a string representation of the body of a JMS bytes message. This
	 * is basically a hex dump of the body. Note, this only looks at the first
	 * 1K of the message body.
	 */
	public static String jmsBytesBodyAsString(Message m) {
		byte[] body = new byte[1024];
		int n = 0;

		if (m instanceof BytesMessage) {
			try {
				((BytesMessage) m).reset();
				n = ((BytesMessage) m).readBytes(body);
			} catch (JMSException ex) {
				return (ex.toString());
			}
		} else if (m instanceof StreamMessage) {
			try {
				((StreamMessage) m).reset();
				n = ((StreamMessage) m).readBytes(body);
			} catch (JMSException ex) {
				return (ex.toString());
			}
		}

		if (n <= 0) {
			return "<empty body>";
		} else {
			return (toHexDump(body, n) + ((n >= body.length) ? "\n. . ." : ""));
		}
	}

	/**
	 * Return a string representation of a JMS message body
	 */
	public static String jmsMsgBodyAsString(Message m) {
		if (m instanceof TextMessage) {
			try {
				return ((TextMessage) m).getText();
			} catch (JMSException ex) {
				return ex.toString();
			}
		} else if (m instanceof ObjectMessage) {
			ObjectMessage msg = (ObjectMessage) m;
			Object obj = null;

			try {
				obj = msg.getObject();

				if (obj != null) {
					return obj.getClass().getName();
				} else {
					return "null";
				}
			} catch (Exception ex) {
				return (ex.toString());
			}
		} else if (m instanceof BytesMessage) {
			return jmsBytesBodyAsString(m);
		} else if (m instanceof MapMessage) {
			MapMessage msg = (MapMessage) m;
			HashMap<String, String> props = new HashMap<String, String>();

			// Get all MapMessage properties and stuff into a hash table
			try {
				for (Enumeration enu = msg.getMapNames(); enu.hasMoreElements();) {
					String name = (enu.nextElement()).toString();
					props.put(name, (msg.getObject(name)).toString());
				}

				return props.toString();
			} catch (JMSException ex) {
				return (ex.toString());
			}

		} else if (m instanceof StreamMessage) {
			return jmsBytesBodyAsString(m);
		} else if (m instanceof Message) {
			return "Can't get body for message of type Message";
		}

		return "Unknown message type " + m;
	}

	/**
	 * Takes the JMS header fields of a JMS message and puts them in a HashMap
	 */
	public static HashMap<String, String> jmsHeadersToHashMap(Message m)
			throws JMSException {
		HashMap<String, String> hdrs = new HashMap<String, String>();
		String s = null;

		s = m.getJMSCorrelationID();
		hdrs.put("JMSCorrelationID", s);

		s = String.valueOf(m.getJMSDeliveryMode());
		hdrs.put("JMSDeliverMode", s);

		Destination d = m.getJMSDestination();

		if (d != null) {
			if (d instanceof Queue) {
				s = ((Queue) d).getQueueName() + " : Queue";
			} else {
				s = ((Topic) d).getTopicName() + " : Topic";
			}
		} else {
			s = "";
		}

		hdrs.put("JMSDestination", s);

		s = String.valueOf(m.getJMSExpiration());
		hdrs.put("JMSExpiration", s);

		s = m.getJMSMessageID();
		hdrs.put("JMSMessageID", s);

		s = String.valueOf(m.getJMSPriority());
		hdrs.put("JMSPriority", s);

		s = String.valueOf(m.getJMSRedelivered());
		hdrs.put("JMSRedelivered", s);

		d = m.getJMSDestination();

		if (d != null) {
			if (d instanceof Queue) {
				s = ((Queue) d).getQueueName();
			} else {
				s = ((Topic) d).getTopicName();
			}
		} else {
			s = "";
		}

		hdrs.put("JMSReplyTo", s);

		s = String.valueOf(m.getJMSTimestamp());
		hdrs.put("JMSTimestamp", s);

		s = m.getJMSType();
		hdrs.put("JMSType", s);

		return hdrs;
	}

	/**
	 * Takes a buffer of bytes and returns a hex dump. Each hex digit represents
	 * 4 bits. The hex digits are formatted into groups of 4 (2 bytes, 16 bits).
	 * Each line has 8 groups, so each line represents 128 bits.
	 */
	public static String toHexDump(byte[] buf, int length) {
		// Buffer must be an even length
		if ((buf.length % 2) != 0) {
			throw new IllegalArgumentException();
		}

		int value;
		StringBuffer sb = new StringBuffer(buf.length * 2);

		/*
		 * Assume buf is in network byte order (most significant byte is
		 * buf[0]). Convert two byte pairs to a short, then display as a hex
		 * string.
		 */
		int n = 0;

		while ((n < buf.length) && (n < length)) {
			value = buf[n + 1] & 0xFF; // Lower byte
			value |= ((buf[n] << 8) & 0xFF00); // Upper byte

			String s = Integer.toHexString(value);

			// Left bad with 0's
			sb.append(pad[4 - s.length()]);
			sb.append(s);
			n += 2;

			if ((n % 16) == 0) {
				sb.append("\n");
			} else {
				sb.append(" ");
			}
		}

		return sb.toString();
	}
}
