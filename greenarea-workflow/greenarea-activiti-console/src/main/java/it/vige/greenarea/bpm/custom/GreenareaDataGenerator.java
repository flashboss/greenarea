package it.vige.greenarea.bpm.custom;

import it.vige.greenarea.bpm.GreenareaDemoData;

import org.activiti.explorer.demo.DemoDataGenerator;

public class GreenareaDataGenerator extends DemoDataGenerator {

	private GreenareaDemoData greenareaDemoData = new GreenareaDemoData();

	@Override
	protected void initDemoGroups() {
		greenareaDemoData.initDemoGroups(identityService);
	}

	@Override
	protected void initDemoUsers() {
		greenareaDemoData.initDemoUsers(identityService);
	}

}
