/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgaplconsole.data;

import java.io.Serializable;

/**
 *
 * @author 00917377
 */
public class MonitorMsg implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6531676990134725772L;
	String source;
    String props;
    String msg;

    public MonitorMsg(String s, String p, String m) {
        source = s;
        props = p;
        msg = m;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProps() {
        return props;
    }

    public void setProps(String props) {
        this.props = props;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
