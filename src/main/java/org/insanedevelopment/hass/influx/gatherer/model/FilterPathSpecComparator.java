package org.insanedevelopment.hass.influx.gatherer.model;

import java.util.Comparator;
import java.util.Objects;

public class FilterPathSpecComparator implements Comparator<FilterPathSpec> {

	@Override
	public int compare(FilterPathSpec o1, FilterPathSpec o2) {
		if (Objects.equals(o1, o2)) {
			return 0;
		}
		if (o1.matches(o2.getEntityPathSpec())) {
			return 1;
		}
		if (o2.matches(o1.getEntityPathSpec())) {
			return -1;
		}

		return o1.getEntityPathSpec().compareTo(o2.getEntityPathSpec());
	}

}
