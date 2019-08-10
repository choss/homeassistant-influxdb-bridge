package org.insanedevelopment.hass.influx.gatherer.repository;

import java.util.function.BiConsumer;

import org.insanedevelopment.hass.influx.gatherer.model.json.HassIoState;

public interface HassIoStateChangeObserver extends BiConsumer<HassIoState, HassIoState> {

	public void update(HassIoState oldState, HassIoState newState);

	@Override
	default void accept(HassIoState t, HassIoState u) {
		update(t, u);
	}

}
