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
package it.vige.greenarea.tap.spreceiver.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.vige.tap.spreceiver.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _KeepAliveResponse_QNAME = new QName("http://tap.vige.it/spReceiver/ws", "keepAliveResponse");
    private final static QName _PushResponse_QNAME = new QName("http://tap.vige.it/spReceiver/ws", "pushResponse");
    private final static QName _OutData_QNAME = new QName("http://tap.vige.it/spReceiver/ws", "outData");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.vige.tap.spreceiver.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AckData }
     * 
     */
    public AckData createAckData() {
        return new AckData();
    }

    /**
     * Create an instance of {@link KeepAliveResponse }
     * 
     */
    public KeepAliveResponse createKeepAliveResponse() {
        return new KeepAliveResponse();
    }

    /**
     * Create an instance of {@link OutData }
     * 
     */
    public OutData createOutData() {
        return new OutData();
    }

    /**
     * Create an instance of {@link ParamData }
     * 
     */
    public ParamData createParamData() {
        return new ParamData();
    }

    /**
     * Create an instance of {@link GroupData }
     * 
     */
    public GroupData createGroupData() {
        return new GroupData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeepAliveResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tap.vige.it/spReceiver/ws", name = "keepAliveResponse")
    public JAXBElement<KeepAliveResponse> createKeepAliveResponse(KeepAliveResponse value) {
        return new JAXBElement<KeepAliveResponse>(_KeepAliveResponse_QNAME, KeepAliveResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AckData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tap.vige.it/spReceiver/ws", name = "pushResponse")
    public JAXBElement<AckData> createPushResponse(AckData value) {
        return new JAXBElement<AckData>(_PushResponse_QNAME, AckData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OutData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tap.vige.it/spReceiver/ws", name = "outData")
    public JAXBElement<OutData> createOutData(OutData value) {
        return new JAXBElement<OutData>(_OutData_QNAME, OutData.class, null, value);
    }

}
