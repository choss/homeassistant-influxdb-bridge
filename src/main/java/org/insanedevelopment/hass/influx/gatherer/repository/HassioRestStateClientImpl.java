package org.insanedevelopment.hass.influx.gatherer.repository;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.insanedevelopment.hass.influx.gatherer.model.json.HassIoState;
import org.insanedevelopment.hass.influx.gatherer.model.json.HassIoStateChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class HassioRestStateClientImpl {

	private static final HassIoStateListTypeReference HASSIO_STATE_LIST_TYPE_REFERENCE = new HassIoStateListTypeReference();
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

	public List<HassIoState> getAllCurrentStates() {
		Mono<List<HassIoState>> webcallResult = webClient
				.get()
				.uri("/api/states")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(HASSIO_STATE_LIST_TYPE_REFERENCE);

		List<HassIoState> result = webcallResult
				.blockOptional()
				.orElse(Collections.emptyList());

		LOGGER.debug(".getAllCurrentStates - reply from service " + result);
		return result;
	}

	public void subscribeToChanges() {
		Flux<HassIoStateChangeEvent> flux = webClient
				.get()
				.uri("/api/stream")
				.accept(MediaType.APPLICATION_STREAM_JSON)
				.retrieve()
				.bodyToFlux(HassIoStateChangeEvent.class);
		flux.onErrorContinue(new SilentlyIgnoreAllErrorsConsumer())
				.filter(e -> "state_changed".equals(e.getEventType()))
				.doOnNext(s -> LOGGER.debug(".subscribeToChanges Got message {}", s))
				.subscribe();
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

	private static final class HassIoStateListTypeReference extends ParameterizedTypeReference<List<HassIoState>> {
	}

	private enum Scheme {
		HTTP, WEBSOCKET
	}
}
