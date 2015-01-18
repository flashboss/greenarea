package it.vige.greenarea.bpm.custom.ui.pa;

import static com.vaadin.ui.themes.Reindeer.LAYOUT_WHITE;
import it.vige.greenarea.bpm.custom.ui.dettaglio.KmlDocumentViewer;

import org.activiti.explorer.ui.custom.DetailPanel;

import com.vaadin.ui.VerticalLayout;

public class DefinizioneAreaGeografica extends DetailPanel {

	private static final long serialVersionUID = -5903548585312958722L;

	protected VerticalLayout detailPanelLayout;

	public DefinizioneAreaGeografica() {
		initUi();
	}

	protected void initUi() {
		setSizeFull();
		addStyleName(LAYOUT_WHITE);

		detailPanelLayout = new VerticalLayout();
		detailPanelLayout.setWidth(100, UNITS_PERCENTAGE);
		setDetailContainer(detailPanelLayout);

		KmlDocumentViewer kmlDocumentViewer = new KmlDocumentViewer("", null);
		detailPanelLayout.addComponent(kmlDocumentViewer);

	}

}
