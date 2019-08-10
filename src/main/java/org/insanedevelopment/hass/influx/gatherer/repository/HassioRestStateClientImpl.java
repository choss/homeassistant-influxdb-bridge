package org.insanedevelopment.hass.influx.gatherer.repository;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.insanedevelopment.hass.influx.gatherer.model.json.HassIoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

@Repository
public class HassioRestStateClientImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(HassioRestStateClientImpl.class);

	private RestTemplate template;

	private WebClient webClient;

	@Value("${hassio.token}")
	private String bearerToken;

	@Value("${hassio.host}:${hassio.port}")
	private String hostname;

	@Value("${hassio.secure}")
	private Boolean isSecureCommunication;

	@PostConstruct
	public void init() {
		template = new RestTemplate();
		template.getInterceptors().add(new BearerTokenInterceptor(bearerToken));
		webClient = WebClient.create(expandUrl("", Scheme.HTTP));
	}

	public List<HassIoState> getAllCurrentStates() {
		String url = expandUrl("/api/states", Scheme.HTTP);
		ResponseEntity<List<HassIoState>> result = template.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<HassIoState>>() {
		});
		LOGGER.debug(".getAllCurrentStates - reply from service " + result);

		if (result.hasBody()) {
			return result.getBody();
		} else {
			return Collections.emptyList();
		}
	}

	public void subscribeToChanges() {
		Flux<String> flux = webClient
				.get()
				.uri("/api/stream")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
				.accept(MediaType.APPLICATION_STREAM_JSON)
				.retrieve().bodyToFlux(String.class);
		flux.subscribe(s -> LOGGER.debug(".subscribeToChanges Got message {}", s));
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
