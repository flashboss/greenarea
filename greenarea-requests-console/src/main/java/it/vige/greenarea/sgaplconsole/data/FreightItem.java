/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgaplconsole.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 00917377
 */
public class FreightItem {
    private String codeId;
    private String description;
    private int volume;
    private int height;
    private int lenght;
    private int width;
    private int weight;
    private boolean stackable;
    private boolean keepUpStanding;
    private String ft;
    private List<Attributi> attributi = new ArrayList<Attributi>();

    public FreightItem() {
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLenght() {
        return lenght;
    }

    public void setLenght(int lenght) {
        this.lenght = lenght;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public boolean isKeepUpStanding() {
        return keepUpStanding;
    }

    public void setKeepUpStanding(boolean keepUpStanding) {
        this.keepUpStanding = keepUpStanding;
    }

    public String getFt() {
        return ft;
    }

    public void setFt(String ft) {
        this.ft = ft;
    }

    public List<Attributi> getAttributi() {
        return attributi;
    }

    public void setAttributi(List<Attributi> attributi) {
        this.attributi = attributi;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("item [").append(codeId).append("] ");
        sb.append(description).append(" - Attributi: ");
        sb.append("<Tipo : ").append(ft).append("> ");
        sb.append("<Volume : ").append(volume).append("> ");        
        sb.append("<Altezza : ").append(height).append("> ");         
        sb.append("<Lunghezza : ").append(lenght).append("> ");
        sb.append("<Profondita' : ").append(width).append("> ");        
        sb.append("<Peso : ").append(weight).append("> ");         
        sb.append("<Sovrapponibile : ").append(stackable).append("> ");         
        sb.append("<Non coricare : ").append(keepUpStanding).append("> ");  
   
        for (Attributi a : attributi) {
            sb.append(a.toString());
        }
        return sb.toString();

    }
    
}
