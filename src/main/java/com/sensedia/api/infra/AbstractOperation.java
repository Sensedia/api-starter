package com.sensedia.api.infra;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.sensedia.api.infra.RestClient.RequestHandler;
import com.sensedia.api.infra.RestClient.ResponseEntityHandler;
import com.sensedia.api.interfaces.Error;

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

	protected <T> void call(Object body, Class<T> responseType, ResponseEntityHandler<T> handler) {
		try {
			HttpEntity<?> requestEntity = new HttpEntity<>(body, this.header);
			ResponseEntity<T> response = this.client.exchange(this.url, this.method(), requestEntity, responseType);
			handler.onResponse(response);
		} catch (Exception e) {
			this.handle(e, handler);
		}
	}

	protected <T> void call(Class<T> responseType, ResponseEntityHandler<T> handler) {
		try {
			HttpEntity<?> requestEntity = new HttpEntity<>(this.header);
			ResponseEntity<T> response = this.client.exchange(this.url, this.method(), requestEntity, responseType);
			handler.onResponse(response);
		} catch (Exception e) {
			this.handle(e, handler);
		}
	}

	protected <T> void call(URI uri, Class<T> responseType, ResponseEntityHandler<T> handler) {
		try {
			HttpEntity<?> requestEntity = new HttpEntity<>(this.header);
			ResponseEntity<T> response = this.client.exchange(uri, this.method(), requestEntity, responseType);
			handler.onResponse(response);
		} catch (Exception e) {
			this.handle(e, handler);
		}
	}

	protected <T> void call(URI uri, Object body, Class<T> responseType, ResponseEntityHandler<T> handler) {
		try {
			HttpEntity<?> requestEntity = new HttpEntity<>(body, this.header);
			ResponseEntity<T> response = this.client.exchange(uri, this.method(), requestEntity, responseType);
			handler.onResponse(response);
		} catch (Exception e) {
			this.handle(e, handler);
		}
	}

	private <T> void handle(Exception exception, ResponseEntityHandler<T> handler) {
		if (exception instanceof HttpStatusCodeException) {
			HttpStatusCodeException serverException = (HttpStatusCodeException) exception;
			ResponseEntity response = ResponseEntity.status(serverException.getStatusCode())
					.body(serverException.getResponseBodyAsString());
			handler.onResponse(response);
			return;
		}

		Error error = new Error();
		error.setCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		error.setDescription(exception.getMessage());
		ResponseEntity response = ResponseEntity.unprocessableEntity().body(error);
		handler.onResponse(response);
	}

	public String url() {
		return url;
	}

	protected abstract HttpMethod method();
}