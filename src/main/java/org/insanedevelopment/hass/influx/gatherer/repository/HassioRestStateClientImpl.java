package org.insanedevelopment.hass.influx.gatherer.repository;

import java.time.Duration;
import java.util.function.BiConsumer;
import javax.annotation.PostConstruct;

import org.insanedevelopment.hass.influx.gatherer.model.json.HassIoState;
import org.insanedevelopment.hass.influx.gatherer.model.json.HassIoStateChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

@Repository
public class HassioRestStateClientImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(HassioRestStateClientImpl.class);

	private WebClient webClient;

	@Value("${hassio.token}")
	private String bearerToken;

	@Value("${hassio.host}:${hassio.port}")
	private String hostname;

	@Value("${hassio.secure}")
	private Boolean isSecureCommunication;

	@PostConstruct
	public void init() {
		webClient = WebClient.builder()
				.baseUrl(expandUrl("", Scheme.HTTP))
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
				.build();
	}

	public Flux<HassIoState> getAllCurrentStates() {
		Flux<HassIoState> webcallResult = webClient
				.get()
				.uri("/api/states")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(HassIoState.class);
		return webcallResult;
	}

	public void subscribeToChanges(BiConsumer<HassIoState, HassIoState> stateChangeObserver) {
		Flux<HassIoStateChangeEvent> flux = webClient
				.get()
				.uri("/api/stream")
				.accept(MediaType.APPLICATION_STREAM_JSON)
				.retrieve()
				.bodyToFlux(HassIoStateChangeEvent.class);
		flux.onErrorContinue(DecodingException.class, new SilentlyIgnoreErrors())
				.filter(e -> "state_changed".equals(e.getEventType()))
				.doOnNext(s -> LOGGER.debug(".subscribeToChanges Got message {}", s))
				.retryBackoff(Long.MAX_VALUE, Duration.ofSeconds(1), Duration.ofMinutes(5))
				.subscribe(s -> stateChangeObserver.accept(s.getData().getOldState(), s.getData().getNewState()));
	}

	private String expandUrl(String uriPart, Scheme scheme) {
		String result = null;
		switch (scheme) {
		case HTTP:
			result = isSecureCommunication ? "https://" : "http://";
			break;
		case WEBSOCKET:
			result = isSecureCommunication ? "wss://" : "ws://";
			break;
		}
		return result + hostname + uriPart;
	}

	private enum Scheme {
		HTTP, WEBSOCKET
	}
}
