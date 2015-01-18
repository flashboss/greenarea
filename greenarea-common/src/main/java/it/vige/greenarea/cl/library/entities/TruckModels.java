/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

import java.util.HashMap;

/**
 *
 * @author 00917308
 */
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
