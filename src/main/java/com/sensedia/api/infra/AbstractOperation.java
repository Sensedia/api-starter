package com.sensedia.api.infra;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.sensedia.api.infra.RestClient.RequestHandler;
import com.sensedia.api.infra.RestClient.ResponseHandler;

@SuppressWarnings("unchecked")
abstract class AbstractOperation<O extends AbstractOperation<O>> {
	private final String url;
	
	private final RestTemplate client;
	
	private final HttpHeaders header;

	public AbstractOperation(String url, HttpHeaders header) {
		this.url = url;
		this.client = new RestTemplate();
		this.header = header;
	}
	
	public O onRequest(RequestHandler handler) {
		handler.onRequest(this.client);
		return (O) this;
	}
	
	protected <T> void call(Object body, Class<T> responseType, ResponseHandler<T> handler){
		HttpEntity<?> requestEntity = new HttpEntity<>(body, this.header);
		ResponseEntity<T> response = this.client.exchange(this.url, this.method(), requestEntity, responseType);
		handler.onResponse(response);
	}
	
	protected <T> void call(URI uri, Object body, Class<T> responseType, ResponseHandler<T> handler){
		HttpEntity<?> requestEntity = new HttpEntity<>(body, this.header);
		ResponseEntity<T> response = this.client.exchange(uri, this.method(), requestEntity, responseType);
		handler.onResponse(response);
	}
	
	public String url() {
		return url;
	}
	
	protected abstract HttpMethod method();
}