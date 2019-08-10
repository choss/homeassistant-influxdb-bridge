package org.insanedevelopment.hass.influx.gatherer.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class FilterPathSpecTest {

	@Test
	public void testSomething() {
		List<FilterPathSpec> list = new ArrayList<>();
		list.add(new FilterPathSpec("sun.sun"));
		list.add(new FilterPathSpec("sun.*"));
		list.sort(new FilterPathSpecComparator());
		Assert.assertEquals("sun.sun", list.get(0).getEntityPathSpec());
		Assert.assertEquals("sun.", list.get(1).getEntityPathSpec());
	}

}
