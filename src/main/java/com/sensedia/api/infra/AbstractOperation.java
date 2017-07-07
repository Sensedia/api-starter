package com.sensedia.api.infra;

import org.springframework.web.client.RestTemplate;

import com.sensedia.api.infra.RestClient.RequestHandler;

@SuppressWarnings("unchecked")
abstract class AbstractOperation<T, O extends AbstractOperation<T, O>> {
	private final String url;
	
	private final RestTemplate client;

	public AbstractOperation(String url) {
		this.url = url;
		this.client = new RestTemplate();
	}
	
	public O onRequest(RequestHandler handler) {
		handler.onRequest(this.client());
		return (O) this;
	}
	
	public RestTemplate client() {
		return client;
	}
	
	public String url() {
		return url;
	}
}