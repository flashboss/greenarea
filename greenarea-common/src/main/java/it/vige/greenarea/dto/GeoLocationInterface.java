/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.dto;

/**
 *
 * @author 00917308
 */
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
