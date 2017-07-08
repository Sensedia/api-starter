package com.sensedia.api.infra;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class RestClient {

	private final String url;
	
	private RestClient(String url) {
		this.url = url;
	}

	public static RestClient url(String url) {
		return new RestClient(url);
	}
	
	public <T> GetOperation<T> get(Map<String, ?> parameters){
		return new GetOperation<>(this.url, parameters);
	}

	public <T> PagedOperation<T> get(Limit limit, Offset offset) {
		return new PagedOperation<T>(this.url, limit, offset);
	}

	public static class Limit {
		private final Integer value;

		private Limit(Integer value) {
			this.value = value;
		}

		Integer get() {
			return value;
		}

		public static Limit of(Integer value) {
			return new Limit(value);
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
	
	public static class GetOperation<T> extends AbstractOperation<T, GetOperation<T>>{
		private final Map<String, ?> parameters;

		public GetOperation(String url, Map<String, ?> parameters) {
			super(url);
			this.parameters = parameters;
		}
		
		Map<String, ?> getParameters() {
			return parameters;
		}
		
		public void onResponse(ResponseHandler<T> handler, Class<T> type) {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.url());
			for(String key : this.parameters.keySet()){
				builder.queryParam(key, this.parameters.get(key));
			}
			
			ResponseEntity<T> response = this.client().getForEntity(builder.build().encode().toUri(), type);
			handler.onResponse(response);
		}
	}

	public static class PagedOperation<T> extends AbstractOperation<T, PagedOperation<T>> {
		private final Limit limit;

		private final Offset offset;
		
		public PagedOperation(String url, Limit limit, Offset offset) {
			super(url);
			this.limit = limit;
			this.offset = offset;
		}

		public void onResponse(ResponseHandler<T> handler, Class<T> type) {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.url())
					.queryParam("_limit", this.limit.get())
					.queryParam("_offset", this.offset.get());
			
			ResponseEntity<T> response = this.client().getForEntity(builder.build().encode().toUri(), type);
			handler.onResponse(response);
		}
	}
	
	@FunctionalInterface
	public static interface RequestHandler {
		void onRequest(RestTemplate client);
	}

	@FunctionalInterface
	public static interface ResponseHandler<T> {
		void onResponse(ResponseEntity<T> response);
	}
}