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
package it.vige.greenarea.cl.library.entities;

import java.util.HashMap;

public class TruckModels {
    public static final String FURGONATO = "FURGONATO";
    public static final HashMap<String, TruckModelInterface> directory 
                                   = new HashMap<String, TruckModelInterface>();
    static {
        directory.put("Fiat Ducato 120 LWB 35", new SimpleVanModel( 11500, 70 ));
        directory.put("Renault Trafic LL29", new SimpleVanModel( 5900, 70 ));
        directory.put(FURGONATO, new SimpleVanModel( 5000, 70 ));
    }
}
