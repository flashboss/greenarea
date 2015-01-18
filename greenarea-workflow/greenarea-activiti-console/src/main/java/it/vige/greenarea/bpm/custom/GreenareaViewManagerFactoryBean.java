package it.vige.greenarea.bpm.custom;

import static org.activiti.explorer.Environments.ALFRESCO;

import org.activiti.explorer.DefaultViewManager;
import org.activiti.explorer.ViewManager;
import org.activiti.explorer.ViewManagerFactoryBean;
import org.activiti.explorer.ui.alfresco.AlfrescoViewManager;

public class GreenareaViewManagerFactoryBean extends ViewManagerFactoryBean {

	private static final long serialVersionUID = -620536937856444318L;

	@Override
	public ViewManager getObject() throws Exception {
		DefaultViewManager viewManagerImpl;
		if (environment.equals(ALFRESCO)) {
			viewManagerImpl = new AlfrescoViewManager();
		} else {
			viewManagerImpl = new GreenareaViewManager();
		}
		viewManagerImpl.setMainWindow(mainWindow);
		return viewManagerImpl;
	}

}
