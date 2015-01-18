package it.vige.greenarea.bpm.custom.ui.mainlayout;

import org.activiti.explorer.ui.NoParamComponentFactory;
import org.activiti.explorer.ui.alfresco.AlfrescoMainMenuBar;
import org.activiti.explorer.ui.mainlayout.MainMenuBar;

public class GreenareaMainMenuBarFactory extends NoParamComponentFactory<MainMenuBar> {

	private static final long serialVersionUID = 9056275164827001944L;

	  @Override
	  protected Class<AlfrescoMainMenuBar> getAlfrescoComponentClass() {
	    return AlfrescoMainMenuBar.class;
	  }
	  
	  @Override
	  protected Class<GreenareaMainMenuBar> getDefaultComponentClass() {
	    return GreenareaMainMenuBar.class;
	  }
}
