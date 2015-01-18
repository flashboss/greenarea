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
package it.vige.greenarea.itseasy.lib.configurationData;

public interface MqConstants {

    //Properties Keys on MQ topics
    final static String MQ_KEY_TIMESTAMP = "TIMESTAMP";
    final static String MQ_KEY_TRANSPORT_ID = "TRANSPORT_ID";
    final static String MQ_KEY_SHIPPING_ID = "SHIPPING_ID";
    final static String MQ_KEY_VECTOR_NAME = "VECTOR_NAME";
    final static String MQ_KEY_SOURCE_NAME = "SOURCE_NAME";
    final static String MQ_KEY_DESTINATION_NAME = "DESTINATION_NAME";
    final static String MQ_KEY_SOURCE_LOC = "SOURCE_LOC";
    final static String MQ_KEY_DESTINATION_LOC = "DESTINATION_LOC";
    final static String MQ_KEY_CAUSE = "CAUSE";
    final static String MQ_KEY_DEST_ID = "DEST_ID";
    final static String MQ_KEY_SGO_WS_OP = "SGO_WS_OP";
    final static int OBJ_FACTORY_FS_TYPE = 0;
    final static int OBJ_FACTORY_LDAP_TYPE = 1;
    final static int OBJ_FACTORY_JMS_TYPE = 2;
    final static String[] INITIAL_CONTEXT = {"com.sun.jndi.fscontext.RefFSContextFactory",
        "com.sun.jndi.ldap.LdapCtxFactory", "none"};
    final static String LDAP_itseasy_Log = "cn=ItseasyLogger";
    final static String LDAP_itseasy_CF = "cn=itseasyConnectionFactory";
    final static String JMS_PRINCIPAL = "";
    final static String JMS_Credential = "";
    final static String JMS_itseasy_CF = "cn=itseasyConnectionFactory";
    //FileSystem names for MQstore
    final static String FILE_SYSTEM_URL = "file:///C:/SGAPL_mq_store";
    final static String itseasy_Log = "ItseasyLogger";
    final static String itseasy_CF = "itseasyConnectionFactory";
    final static String gat_to_vector = "VectorTopic";
    final static String vector_to_gat = "GatTopic";
    final static String gat_to_sgo = "ItseasyDest";
    final static int MSG_TYPE_OBJECT = 0;
    final static int MSG_TYPE_TEXT = 1;
    final static int MSG_TYPE_MAP = 2;
    final static int MSG_TYPE_BYTES = 3;
    final static int MSG_TYPE_STREAM = 4;
    //dest type
    final static int DESTINATION_TYPE_QUEUE = 0;
    final static int DESTINATION_TYPE_TOPIC = 1;
    final static int DESTINATION_TYPE_TOPIC_DURABLE = 2;
    //msg delivery mode
    final static int DELIVERY_MODE_PERSISTENT = 0;
    final static int DELIVERY_MODE_NON_PERSISTENT = 1;
    //msg compression
    final static int MSG_COMPRESSED = 0;
    final static int MSG_NON_COMPRESSED = 1;
    //session transacted
    final static int SESSION_TRANSACTED = 0;
    final static int SESSION_NON_TRANSACTED = 1;
    //acknowledgeMode
    final static int AUTO_ACKNOWLEDGE = 0;
    final static int CLIENT_ACKNOWLEDGE = 1;
    final static int DUPS_OK_ACKNOWLEDGE = 2;
    final static int SESSION_TRANSACTED_MODE = 3;
    long MAX_READ_WAIT_TIMEOUT = new Long(24 * 60 * 60 * 1000); //max 1 giorno
    
    public enum FREIGHT_ATTRIBS {
        
    }
}
