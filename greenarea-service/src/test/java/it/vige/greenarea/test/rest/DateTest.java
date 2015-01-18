package it.vige.greenarea.test.rest;

import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.junit.Test;

public class DateTest {

	@Test
	public void simpleDateParsing() {
		DateFormat formatter = new SimpleDateFormat(
				"EEE MMM d HH:mm:ss z yyyy", Locale.US);
		try {
			formatter.parse("Fri May 16 00:00:00 CEST 2014");
		} catch (ParseException e) {
			fail();
		}
	}
}
