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
package it.vige.greenarea.dto;

public interface GeoLocationInterface {
        public String getZipCode();

        public void setZipCode(String cap);

        public String getCity();

        public void setCity(String city);

        public String getCountry();
        
        public void setCountry(String country);

        public double getLatitude();

        public void setLatitude(double latitude);

        public double getLongitude();

        public void setLongitude(double longitude);

        public String getNumber();

        public void setNumber(String number);

        public long getRadius();

        public void setRadius(long radius);

        public String getAdminAreaLevel1();

        public void setAdminAreaLevel1(String region);

        public String getAdminAreaLevel2();

        public void setAdminAreaLevel2(String adminAreaLevel2);

        public String getStreet();

        public void setStreet(String street);
        
}
