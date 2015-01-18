/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.I18N;


import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author 00917308
 */
public class I18N {
    private static Locale currentLocale;
    private static ResourceBundle messages;
    
    /*<editor-fold defaultstate="collapsed" desc="comment">
    public static String[] getI18NcarrierTypes(){
        String[] result = new String[CarrierType.values().length];
        int i =0;
        for( CarrierType ct : CarrierType.values())result[i++]=messages.getString(ct.name());
        return result;
    }
    //</editor-fold>*/

public static String[]getStrings( Object[] values ){
 if(values == null ) return null;
 if(currentLocale==null){
  setLocale( Locale.getDefault());
 }
 String[] result = new String[values.length];
 int i =0;
 for( Object s : values )result[i++]=messages.getString(s.toString() );
 return result; 
}

public static I18NObject[] getI18NObjects( Object[] values ){
 if(values == null ) return null;
 if(currentLocale==null){
  setLocale( Locale.getDefault());
 }
 I18NObject[] result = new I18NObject[values.length];
 int i =0;
 for( Object s : values ) result[i++] = new I18NObject(s);
 return result; 
}

public static void setLocale( Locale l ){
    currentLocale = l;
    messages = ResourceBundle.getBundle("MessagesBundle",  currentLocale);
}

static void setLocale( String language, String country ){
    Locale l = new Locale(language,country);
    setLocale( l );
}

public static Locale getLocale(){
  if(currentLocale==null){
  setLocale( Locale.getDefault());
 }
return currentLocale;
}

public static String getString( String key ){
    if(currentLocale==null){
        setLocale( Locale.getDefault());
    }
    return messages.getString(key);
}


}
