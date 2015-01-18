/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.I18N;

/**
 *
 * @author 00917308
 */
public class I18NObject {
    private Object o;
    private String name;
    
    public I18NObject( Object o ){
    name = I18N.getString(o.toString());
    this.o = o;
    }
    
    public Object getObject(){
        return o;
    }
    
    @Override
    public String toString(){
      return name;
    }
}
