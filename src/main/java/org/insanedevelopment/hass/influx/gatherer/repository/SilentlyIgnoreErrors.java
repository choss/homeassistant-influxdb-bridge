package org.insanedevelopment.hass.influx.gatherer.repository;

import java.util.function.BiConsumer;

public class SilentlyIgnoreErrors implements BiConsumer<Throwable, Object> {

	@Override
	public void accept(Throwable t, Object u) {
		// do nothing
	}

}
