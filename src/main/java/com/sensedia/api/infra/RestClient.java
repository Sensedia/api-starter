package com.sensedia.api.infra;

import java.util.Map;

import org.apache.http.entity.ContentType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class RestClient {

	private final String url;
	
	private final HttpHeaders header;
	
	private RestClient(String url, HttpHeaders header) {
		this.url = url;
		this.header = header;
	}

	public static RestClient create(String url) {
		return create(url, new HttpHeaders());
	}
	
	public static RestClient create(String url, HttpHeaders header) {
		return new RestClient(url, header);
	}
	
	public static ResponseEntityHandler<Void> voidHandler(){
		return new ResponseEntityHandler<Void>(){
			@Override
			public void onResponse(ResponseEntity<Void> response) {
				//Do nothing
			}
		};
	}
	
	public <T> PostOperation<T> post(Object body, ContentType type){
		this.header.add(HttpHeaders.CONTENT_TYPE, type.getMimeType());
		return new PostOperation<>(this.url, this.header, body);
	}
	
	public <T> PostOperation<T> post(){
		return new PostOperation<>(this.url, this.header, null);
	}
	
	public <T> GetOperation<T> get(Map<String, ?> parameters){
		return new GetOperation<>(this.url, this.header, parameters);
	}
	
	public <T> GetOperation<T> get(){
		return new GetOperation<>(this.url, this.header, null);
	}

	public <T> PagedOperation<T> get(Limit limit, Offset offset) {
		return new PagedOperation<T>(this.url, this.header, limit, offset);
	}
	
	public static class PostOperation<T> extends AbstractOperation<PostOperation<T>>{
		private final Object body;
		
		public PostOperation(String url, HttpHeaders header, Object body) {
			super(url, header);
			this.body = body;
		}
		
		public void onResponse(ResponseEntityHandler<T> handler, Class<T> responseType){
			if(this.body == null){
				this.call(responseType, handler);
				return;
			}
			this.call(this.body, responseType, handler);
		}
		
		public void onResponse(ResponseEntityHandler<Void> handler){
			if(this.body == null){
				this.call(Void.class, handler);
				return;
			}
			this.call(this.body, Void.class, handler);
		}
		
		@Override
		protected HttpMethod method() {
			return HttpMethod.POST;
		}
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
	
	public static class GetOperation<T> extends AbstractOperation<GetOperation<T>>{
		private final Map<String, ?> parameters;

		public GetOperation(String url, HttpHeaders header, Map<String, ?> parameters) {
			super(url, header);
			this.parameters = parameters;
		}
		
		Map<String, ?> getParameters() {
			return parameters;
		}
		
		public void onResponse(ResponseEntityHandler<T> handler, Class<T> type) {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.url());
			
			if(this.parameters != null){
				for(String key : this.parameters.keySet()){
					builder.queryParam(key, this.parameters.get(key));
				}
			}
			
			this.call(builder.build().encode().toUri(), type, handler);
		}
		
		@Override
		protected HttpMethod method() {
			return HttpMethod.GET;
		}
	}

	public static class PagedOperation<T> extends AbstractOperation<PagedOperation<T>> {
		private final Limit limit;

		private final Offset offset;
		
		public PagedOperation(String url, HttpHeaders header, Limit limit, Offset offset) {
			super(url, header);
			this.limit = limit;
			this.offset = offset;
		}

		public void onResponse(ResponseEntityHandler<T> handler, Class<T> type) {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.url())
					.queryParam("_limit", this.limit.get())
					.queryParam("_offset", this.offset.get());
			
			this.call(builder.build().encode().toUri(), null, type, handler);
		}
		
		@Override
		protected HttpMethod method() {
			return HttpMethod.GET;
		}
	}
	
	@FunctionalInterface
	public static interface RequestHandler {
		void onRequest(RestTemplate client);
	}

	@FunctionalInterface
	public static interface ResponseEntityHandler<T> {
		void onResponse(ResponseEntity<T> response);
	}
}