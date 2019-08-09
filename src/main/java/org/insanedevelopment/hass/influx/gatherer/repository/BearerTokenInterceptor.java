package org.insanedevelopment.hass.influx.gatherer.repository;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class BearerTokenInterceptor implements ClientHttpRequestInterceptor {

	private final String token;

	public BearerTokenInterceptor(String token) {
		this.token = token;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		request.getHeaders().setBearerAuth(token);
		request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		return execution.execute(request, body);
	}

}
