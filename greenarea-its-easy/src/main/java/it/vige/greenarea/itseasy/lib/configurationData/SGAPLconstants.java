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

public interface SGAPLconstants {
    public static final String UNKNOWN_STATUS = "waiting";
    public static final String READY_STATUS = "ready";
    public static final String ON_DELIVERY_STATUS = "on delivery";
    public static final String DONE_STATUS = "done";
    public static final String REJECT_STATUS = "rejected";
    
    public static final long IDLE = 0x0000;
    public static final long SHIPPED = 0x0001;
    public static final long ONGOING = 0x0002;
    public static final long EXCEPTION = 0x0100;

    
}
