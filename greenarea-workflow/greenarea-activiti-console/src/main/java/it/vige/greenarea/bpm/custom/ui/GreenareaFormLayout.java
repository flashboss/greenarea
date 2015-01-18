package it.vige.greenarea.bpm.custom.ui;

import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_FORM_TABLE;

import com.vaadin.ui.VerticalLayout;

public class GreenareaFormLayout extends VerticalLayout {

	private static final long serialVersionUID = 3096232893210552169L;

	public GreenareaFormLayout() {
		super();
		setSpacing(true);
		setMargin(false);
		setStyleName(STYLE_FORM_TABLE);
	}

}
