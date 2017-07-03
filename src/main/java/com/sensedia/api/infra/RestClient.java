package com.sensedia.api.infra;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestClient {

	private final String url;

	private RestClient(String url) {
		this.url = url;
	}

	public static RestClient url(String url) {
		return new RestClient(url);
	}

	public PagedOperation get(Limit limit, Offset offset) {
		return new PagedOperation(limit, offset);
	}

	public static class Limit {
		private final Integer value;

		private Limit(Integer value) {
			this.value = value;
		}

		Integer get() {
			return value;
		}

		public static Offset of(Integer value) {
			return new Offset(value);
		}
	}

	public static class Offset {
		private final Integer value;

		private Offset(Integer value) {
			this.value = value;
		}

		Integer get() {
			return value;
		}

		public static Offset of(Integer value) {
			return new Offset(value);
		}
	}

	public class PagedOperation {
		private final Limit limit;

		private final Offset offset;

		private final RestTemplate client;

		public PagedOperation(Limit limit, Offset offset) {
			this.limit = limit;
			this.offset = offset;
			this.client = new RestTemplate();
		}

		public PagedOperation onRequest(RequestHandler handler) {
			handler.onRequest(this.client);
			return this;
		}

		public <T> PagedOperation onResponse(ResponseHandler<T> handler, Class<T> type) {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("_limit", this.limit.get());
			parameters.put("_offset", this.offset.get());

			ResponseEntity<T> response = this.client.getForEntity(RestClient.this.url, type, parameters);
			handler.onResponse(response);

			return this;
		}
	}

	@FunctionalInterface
	public interface RequestHandler {
		void onRequest(RestTemplate client);
	}

	@FunctionalInterface
	public interface ResponseHandler<T> {
		void onResponse(ResponseEntity<T> response);
	}
}