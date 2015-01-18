/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.ln.swing;

/**
 * 
 * @author 00917308
 */
public interface LNeditorComponentInterface {
	public void addListener(LNeditorEventHandlerInterface h);

	public boolean isChanged();

	public Object getUserObject();
}
