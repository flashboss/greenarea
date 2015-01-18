package it.vige.greenarea.test.file;

import static org.junit.Assert.assertEquals;
import it.vige.greenarea.Utilities;

import org.junit.Test;

public class DateTest {

	@Test
	public void testShippingId() throws Exception {
		String id = Utilities.createMockShippingId();
		assertEquals(id.length(), 17);
	}
}
