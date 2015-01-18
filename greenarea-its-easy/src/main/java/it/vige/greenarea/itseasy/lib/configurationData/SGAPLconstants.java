/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.lib.configurationData;

/**
 *
 * @author 00917377
 */
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
