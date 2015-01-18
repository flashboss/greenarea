package it.vige.greenarea.bpm.custom.ui;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.vaadin.addon.customfield.CustomField;

import com.vaadin.data.Property;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;

/**
 * A field for selecting time values.
 * <p>
 * Uses {@link Date} as internal data type.
 * 
 * @author Thomas Mattsson / Vaadin OY
 */
public class TimeField extends CustomField {

	private static final long serialVersionUID = -676425827861766118L;

	private static final String VALUE_AM = "am";
	private static final String VALUE_PM = "pm";

	private boolean use24HourClock = true;
	private Locale givenLocale = null;

	private final NativeSelect hourSelect;
	private final NativeSelect minuteSelect;
	private final NativeSelect secondSelect;
	private final NativeSelect ampmSelect;

	private int resolution = DateField.RESOLUTION_MIN;

	private boolean maskInternalValueChange = false;

	private HorizontalLayout root;

	public TimeField(String caption) {
		this();
		setCaption(caption);
	}

	public TimeField() {

		root = new HorizontalLayout();
		setCompositionRoot(root);

		hourSelect = getSelect();
		fill(hourSelect, use24HourClock ? 23 : 12, use24HourClock);
		root.addComponent(hourSelect);

		minuteSelect = getSelect();
		fill(minuteSelect, 59, true);
		root.addComponent(minuteSelect);

		secondSelect = getSelect();
		fill(secondSelect, 59, true);
		root.addComponent(secondSelect);

		ampmSelect = getSelect();
		ampmSelect.addItem(VALUE_AM);
		ampmSelect.addItem(VALUE_PM);
		root.addComponent(ampmSelect);

		// when setValue() is called, update selects
		addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = 3383351188340627219L;

			public void valueChange(Property.ValueChangeEvent event) {
				if (maskInternalValueChange) {
					return;
				}
				maskInternalValueChange = true;
				updateFields();
				maskInternalValueChange = false;
			}
		});

		addStyleName("timefield");
		setSizeUndefined();

		updateFields();
	}

	/**
	 * Returns hour value in 24-hour format (0-23)
	 */
	@SuppressWarnings("deprecation")
	public int getHours() {
		Date d = (Date) getValue();
		return d.getHours();
	}

	/**
	 * Set hour in 24-hour format (0-23)
	 * 
	 * @param hours
	 */
	public void setHours(int hours) {

		if (!use24HourClock) {

			if (hours > 12) {
				ampmSelect.select(VALUE_PM);
				hours %= 12;
			} else {
				ampmSelect.select(VALUE_AM);
			}

			if (hours == 0) {
				hours = 12;
			}
		}
		hourSelect.setValue(hours);
	}

	@SuppressWarnings("deprecation")
	public int getMinutes() {
		Date d = (Date) getValue();
		return d.getMinutes();
	}

	public void setMinutes(int minutes) {
		minuteSelect.setValue(minutes);
	}

	@SuppressWarnings("deprecation")
	public int getSeconds() {
		Date d = (Date) getValue();
		return d.getSeconds();
	}

	public void setSeconds(int seconds) {
		secondSelect.setValue(seconds);
	}

	private void fill(NativeSelect s, int max, boolean includeZero) {

		s.removeAllItems();
		int startVal;
		if (includeZero) {
			startVal = 0;
		} else {
			startVal = 1;
		}
		for (int i = startVal; i <= max; i++) {
			s.addItem(i);
		}
	}

	private NativeSelect getSelect() {
		NativeSelect select = new NativeSelect() {
			private static final long serialVersionUID = 7956455947702461208L;

			@Override
			public String getItemCaption(Object itemId) {

				if (itemId instanceof Integer) {
					int val = (Integer) itemId;

					if (val < 10) {
						return "0" + val;
					}
					return val + "";
				}
				return super.getItemCaption(itemId);
			}
		};
		select.setImmediate(true);
		select.setNullSelectionAllowed(false);
		select.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = 3383351188340627219L;

			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				if (maskInternalValueChange) {
					return;
				}
				maskInternalValueChange = true;
				updateValue();
				TimeField.this.fireValueChange(true);
				maskInternalValueChange = false;

			}
		});
		return select;
	}

	/**
	 * This method gets called when any of the internal Select components are
	 * called.
	 */
	@SuppressWarnings("deprecation")
	private void updateValue() {

		// if the value of any select is null at this point, we need to init it
		// to zero to prevent exceptions.

		if (secondSelect.getValue() == null) {
			secondSelect.setValue(0);
		}
		if (minuteSelect.getValue() == null) {
			minuteSelect.setValue(0);
		}
		if (hourSelect.getValue() == null) {
			hourSelect.setValue(0);
		}
		if (ampmSelect.getValue() == null) {
			ampmSelect.setValue(VALUE_AM);
		}

		Date newDate = new Date(0);
		newDate.setSeconds(0);
		newDate.setMinutes(0);
		newDate.setHours(0);

		int val;
		if (resolution <= DateField.RESOLUTION_HOUR) {
			val = (Integer) hourSelect.getValue();

			if (use24HourClock) {
				newDate.setHours(val);
			} else {
				if (VALUE_PM.equals(ampmSelect.getValue()) && val != 12) {
					val += 12;
					val %= 24;
				} else if (VALUE_AM.equals(ampmSelect.getValue()) && val == 12) {
					val = 0;
				}
				newDate.setHours(val);
			}
		}
		if (resolution < DateField.RESOLUTION_HOUR) {
			val = (Integer) minuteSelect.getValue();
			newDate.setMinutes(val);
		}
		if (resolution < DateField.RESOLUTION_MIN) {
			val = (Integer) secondSelect.getValue();
			newDate.setSeconds(val);
		}

		setValue(newDate);
	}

	/**
	 * This method gets called when we update the actual value (Date) of this
	 * TimeField.
	 */
	@SuppressWarnings("deprecation")
	private void updateFields() {

		minuteSelect.setVisible(resolution < DateField.RESOLUTION_HOUR);
		secondSelect.setVisible(resolution < DateField.RESOLUTION_MIN);
		ampmSelect.setVisible(!use24HourClock);

		Date val = (Date) getValue();
		if (val == null) {
			hourSelect.setValue(null);
			minuteSelect.setValue(null);
			secondSelect.setValue(null);
			ampmSelect.setValue(null);
			return;
		}

		fill(hourSelect, use24HourClock ? 23 : 12, use24HourClock);
		if (use24HourClock) {
			hourSelect.setValue(val.getHours());
		} else {
			int h = val.getHours();
			if (h == 0) {
				h = 12;
			} else {
				h %= 12;
			}
			hourSelect.setValue(h);
		}

		minuteSelect.setVisible(resolution < DateField.RESOLUTION_HOUR);
		minuteSelect.setValue(val.getMinutes());

		secondSelect.setVisible(resolution < DateField.RESOLUTION_MIN);
		secondSelect.setValue(val.getSeconds());
		if (val.getHours() < 12) {
			ampmSelect.setValue(VALUE_AM);
		} else {
			ampmSelect.setValue(VALUE_PM);
		}

		// fix 12:xx am
		if (!use24HourClock && val.getHours() == 0) {
			hourSelect.setValue(12);
		}
	}

	/**
	 * Sets locale that is used for time format detection. Defaults to browser
	 * locale.
	 */
	@Override
	public void setLocale(Locale l) {
		givenLocale = l;

		DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT,
				givenLocale);
		String time = df.format(new Date());

		if (time.contains("am") || time.contains("AM") || time.contains("pm")
				|| time.contains("PM")) {
			use24HourClock = false;
		} else {
			use24HourClock = true;
		}
		maskInternalValueChange = true;
		updateFields();
		maskInternalValueChange = false;
	}

	/**
	 * Only resolutions of HOUR, MIN, SECOND are supported. Default is
	 * DateField.RESOLUTION_MIN.
	 * 
	 * @see DateField#setResolution(int)
	 * @param resolution
	 */
	public void setResolution(int resolution) {

		if (this.resolution < resolution) {
			if (resolution > DateField.RESOLUTION_SEC) {
				secondSelect.setValue(0);
			}
			if (resolution > DateField.RESOLUTION_MIN) {
				minuteSelect.setValue(0);
			}
		}
		this.resolution = resolution;
		maskInternalValueChange = true;
		updateFields();
		maskInternalValueChange = false;
	}

	/**
	 * @see DateField#getResolution()
	 * @return
	 */
	public int getResolution() {
		return resolution;
	}

	@Override
	public void attach() {
		super.attach();

		// if there isn't a given locale at this point, use the one from the
		// browser
		if (givenLocale == null) {
			// we have access to application after attachment
			Locale locale = ((WebApplicationContext) getApplication()
					.getContext()).getBrowser().getLocale();
			setLocale(locale);
		}
	}

	public void set24HourClock(boolean use24hourclock) {
		use24HourClock = use24hourclock;
		maskInternalValueChange = true;
		updateFields();
		maskInternalValueChange = false;
	}

	public boolean is24HourClock() {
		return use24HourClock;
	}

	@Override
	public Class<?> getType() {
		return Date.class;
	}

	@Override
	public void setTabIndex(int tabIndex) {
		hourSelect.setTabIndex(tabIndex);
		minuteSelect.setTabIndex(tabIndex);
		secondSelect.setTabIndex(tabIndex);
		ampmSelect.setTabIndex(tabIndex);
	}

	@Override
	public int getTabIndex() {
		return hourSelect.getTabIndex();
	}
}
