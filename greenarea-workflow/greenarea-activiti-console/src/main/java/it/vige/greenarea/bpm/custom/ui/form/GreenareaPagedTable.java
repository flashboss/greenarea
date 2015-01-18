package it.vige.greenarea.bpm.custom.ui.form;

import static it.vige.greenarea.bpm.custom.GreenareaMessages.PAGINAZIONE_PAG;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PAGINAZIONE_PER_PAGINA;
import static org.activiti.explorer.ExplorerApp.get;
import it.vige.greenarea.vo.Selectable;

import java.util.Collection;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.ui.form.FormPropertyRenderer;

import com.jensjansson.pagedtable.PagedTable;
import com.jensjansson.pagedtable.PagedTableContainer;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.Reindeer;

public class GreenareaPagedTable<T> extends PagedTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6285946622664858939L;

	private int maxRecords = 3;

	private Collection<T> values;

	private GreenareaFormPropertiesForm greenareaFormPropertiesForm;

	public GreenareaPagedTable(Collection<T> values,
			GreenareaFormPropertiesForm greenareaFormPropertiesForm) {
		this.values = values;
		this.greenareaFormPropertiesForm = greenareaFormPropertiesForm;
	}

	public GreenareaPagedTable(Collection<T> values,
			GreenareaFormPropertiesForm greenareaFormPropertiesForm, int maxRecords) {
		this(values, greenareaFormPropertiesForm);
		this.maxRecords = maxRecords;
	}

	@Override
	public HorizontalLayout createControls() {
		I18nManager I18nManager = get().getI18nManager();
		final PagedTableContainer container = (PagedTableContainer) getContainerDataSource();
		Label itemsPerPageLabel = new Label(
				I18nManager.getMessage(PAGINAZIONE_PER_PAGINA));
		final ComboBox itemsPerPageSelect = new ComboBox();

		itemsPerPageSelect.addItem("3");
		itemsPerPageSelect.addItem("10");
		itemsPerPageSelect.addItem("25");
		itemsPerPageSelect.addItem("50");
		itemsPerPageSelect.setImmediate(true);
		itemsPerPageSelect.setNullSelectionAllowed(false);
		itemsPerPageSelect.setWidth("50px");
		itemsPerPageSelect.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = -2255853716069800092L;

			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				setPageLength(Integer.valueOf(String.valueOf(event
						.getProperty().getValue())));
			}
		});
		itemsPerPageSelect.select(maxRecords + "");
		Label pageLabel = new Label(I18nManager.getMessage(PAGINAZIONE_PAG)
				+ "&nbsp;", Label.CONTENT_XHTML);
		final TextField currentPageTextField = new TextField();
		currentPageTextField.setValue(String.valueOf(getCurrentPage()));
		currentPageTextField.addValidator(new IntegerValidator(null));
		Label separatorLabel = new Label("&nbsp;/&nbsp;", Label.CONTENT_XHTML);
		final Label totalPagesLabel = new Label(
				String.valueOf(getTotalAmountOfPages()), Label.CONTENT_XHTML);
		currentPageTextField.setStyleName(Reindeer.TEXTFIELD_SMALL);
		currentPageTextField.setImmediate(true);
		currentPageTextField.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = -2255853716069800092L;

			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				if (currentPageTextField.isValid()
						&& currentPageTextField.getValue() != null) {
					int page = Integer.valueOf(String
							.valueOf(currentPageTextField.getValue()));
					setCurrentPage(page);
				}
			}
		});
		pageLabel.setWidth(null);
		currentPageTextField.setWidth("20px");
		separatorLabel.setWidth(null);
		totalPagesLabel.setWidth(null);

		HorizontalLayout controlBar = new HorizontalLayout();
		HorizontalLayout pageSize = new HorizontalLayout();
		HorizontalLayout pageManagement = new HorizontalLayout();
		final Button first = new Button("<<", new ClickListener() {
			private static final long serialVersionUID = -355520120491283992L;

			public void buttonClick(ClickEvent event) {
				setCurrentPage(0);
				if (values != null && !values.isEmpty()
						&& values.iterator().next() instanceof Selectable) {
					Selectable selectable = (Selectable) values.toArray()[container
							.getStartIndex()];
					GreenareaFormPropertiesComponent greenareaFormPropertiesComponent = (GreenareaFormPropertiesComponent) greenareaFormPropertiesForm
							.getComponent(1);
					FormProperty formProperty = greenareaFormPropertiesComponent
							.getFormProperties().get(0);
					FormPropertyRenderer formPropertyRenderer = greenareaFormPropertiesComponent
							.getRenderer(formProperty);
					greenareaFormPropertiesComponent
							.getForm()
							.getField(
									greenareaFormPropertiesComponent
											.getFormProperties().get(0).getId())
							.setValue(
									formPropertyRenderer
											.getPropertyLabel(formProperty)
											+ " " + selectable.getValue());
				}
			}
		});
		final Button previous = new Button("<", new ClickListener() {
			private static final long serialVersionUID = -355520120491283992L;

			public void buttonClick(ClickEvent event) {
				previousPage();
				if (values != null && !values.isEmpty()
						&& values.iterator().next() instanceof Selectable) {
					Selectable selectable = (Selectable) values.toArray()[container
							.getStartIndex()];
					GreenareaFormPropertiesComponent greenareaFormPropertiesComponent = (GreenareaFormPropertiesComponent) greenareaFormPropertiesForm
							.getComponent(1);
					FormProperty formProperty = greenareaFormPropertiesComponent
							.getFormProperties().get(0);
					FormPropertyRenderer formPropertyRenderer = greenareaFormPropertiesComponent
							.getRenderer(formProperty);
					greenareaFormPropertiesComponent
							.getForm()
							.getField(
									greenareaFormPropertiesComponent
											.getFormProperties().get(0).getId())
							.setValue(
									formPropertyRenderer
											.getPropertyLabel(formProperty)
											+ " " + selectable.getValue());
				}
			}
		});
		final Button next = new Button(">", new ClickListener() {
			private static final long serialVersionUID = -1927138212640638452L;

			public void buttonClick(ClickEvent event) {
				nextPage();
				if (values != null && !values.isEmpty()
						&& values.iterator().next() instanceof Selectable) {
					Selectable selectable = (Selectable) values.toArray()[container
							.getStartIndex()];
					GreenareaFormPropertiesComponent greenareaFormPropertiesComponent = (GreenareaFormPropertiesComponent) greenareaFormPropertiesForm
							.getComponent(1);
					FormProperty formProperty = greenareaFormPropertiesComponent
							.getFormProperties().get(0);
					FormPropertyRenderer formPropertyRenderer = greenareaFormPropertiesComponent
							.getRenderer(formProperty);
					greenareaFormPropertiesComponent
							.getForm()
							.getField(
									greenareaFormPropertiesComponent
											.getFormProperties().get(0).getId())
							.setValue(
									formPropertyRenderer
											.getPropertyLabel(formProperty)
											+ " " + selectable.getValue());
				}
			}
		});
		final Button last = new Button(">>", new ClickListener() {
			private static final long serialVersionUID = -355520120491283992L;

			public void buttonClick(ClickEvent event) {
				setCurrentPage(getTotalAmountOfPages());
				if (values != null && !values.isEmpty()
						&& values.iterator().next() instanceof Selectable) {
					Selectable selectable = (Selectable) values.toArray()[container
							.getStartIndex()];
					GreenareaFormPropertiesComponent greenareaFormPropertiesComponent = (GreenareaFormPropertiesComponent) greenareaFormPropertiesForm
							.getComponent(1);
					FormProperty formProperty = greenareaFormPropertiesComponent
							.getFormProperties().get(0);
					FormPropertyRenderer formPropertyRenderer = greenareaFormPropertiesComponent
							.getRenderer(formProperty);
					greenareaFormPropertiesComponent
							.getForm()
							.getField(
									greenareaFormPropertiesComponent
											.getFormProperties().get(0).getId())
							.setValue(
									formPropertyRenderer
											.getPropertyLabel(formProperty)
											+ " " + selectable.getValue());
				}
			}
		});
		first.setStyleName(Reindeer.BUTTON_LINK);
		previous.setStyleName(Reindeer.BUTTON_LINK);
		next.setStyleName(Reindeer.BUTTON_LINK);
		last.setStyleName(Reindeer.BUTTON_LINK);

		itemsPerPageLabel.addStyleName("pagedtable-itemsperpagecaption");
		itemsPerPageSelect.addStyleName("pagedtable-itemsperpagecombobox");
		pageLabel.addStyleName("pagedtable-pagecaption");
		currentPageTextField.addStyleName("pagedtable-pagefield");
		separatorLabel.addStyleName("pagedtable-separator");
		totalPagesLabel.addStyleName("pagedtable-total");
		first.addStyleName("pagedtable-first");
		previous.addStyleName("pagedtable-previous");
		next.addStyleName("pagedtable-next");
		last.addStyleName("pagedtable-last");

		itemsPerPageLabel.addStyleName("pagedtable-label");
		itemsPerPageSelect.addStyleName("pagedtable-combobox");
		pageLabel.addStyleName("pagedtable-label");
		currentPageTextField.addStyleName("pagedtable-label");
		separatorLabel.addStyleName("pagedtable-label");
		totalPagesLabel.addStyleName("pagedtable-label");
		first.addStyleName("pagedtable-button");
		previous.addStyleName("pagedtable-button");
		next.addStyleName("pagedtable-button");
		last.addStyleName("pagedtable-button");

		pageSize.addComponent(itemsPerPageLabel);
		pageSize.addComponent(itemsPerPageSelect);
		pageSize.setComponentAlignment(itemsPerPageLabel, Alignment.MIDDLE_LEFT);
		pageSize.setComponentAlignment(itemsPerPageSelect,
				Alignment.MIDDLE_LEFT);
		pageSize.setSpacing(true);
		pageManagement.addComponent(first);
		pageManagement.addComponent(previous);
		pageManagement.addComponent(pageLabel);
		pageManagement.addComponent(currentPageTextField);
		pageManagement.addComponent(separatorLabel);
		pageManagement.addComponent(totalPagesLabel);
		pageManagement.addComponent(next);
		pageManagement.addComponent(last);
		pageManagement.setComponentAlignment(first, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(previous, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(pageLabel, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(currentPageTextField,
				Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(separatorLabel,
				Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(totalPagesLabel,
				Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(next, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(last, Alignment.MIDDLE_LEFT);
		pageManagement.setWidth(null);
		pageManagement.setSpacing(true);
		controlBar.addComponent(pageSize);
		controlBar.addComponent(pageManagement);
		controlBar.setComponentAlignment(pageManagement,
				Alignment.MIDDLE_CENTER);
		controlBar.setWidth("100%");
		controlBar.setExpandRatio(pageSize, 1);
		addListener(new PageChangeListener() {
			public void pageChanged(PagedTableChangeEvent event) {
				first.setEnabled(container.getStartIndex() > 0);
				previous.setEnabled(container.getStartIndex() > 0);
				next.setEnabled(container.getStartIndex() < container
						.getRealSize() - getPageLength());
				last.setEnabled(container.getStartIndex() < container
						.getRealSize() - getPageLength());
				currentPageTextField.setValue(String.valueOf(getCurrentPage()));
				totalPagesLabel.setValue(getTotalAmountOfPages());
				itemsPerPageSelect.setValue(String.valueOf(getPageLength()));
			}
		});
		controlBar.setStyleName("pagination");
		return controlBar;
	}

	public Collection<T> getValues() {
		return values;
	}

	public void setValues(Collection<T> values) {
		this.values = values;
	}

	public GreenareaFormPropertiesForm getGreenareaFormPropertiesForm() {
		return greenareaFormPropertiesForm;
	}

	public void setGreenareaFormPropertiesForm(
			GreenareaFormPropertiesForm greenareaFormPropertiesForm) {
		this.greenareaFormPropertiesForm = greenareaFormPropertiesForm;
	}
}
