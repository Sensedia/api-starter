package com.sensedia.api.interfaces;

import java.util.Collection;

import org.springframework.http.ResponseEntity;

public class PageableResponse<T> {
	
	private final Collection<T> data;
	
	private Long contentRange;
	private Integer acceptRange = 50;
	
	public PageableResponse(Collection<T> data) {
		this.data = data;
	}
	
	public static <T> PageableResponse<T> create(Collection<T> data){
		return new PageableResponse<>(data);
	}
	
	public PageableResponse<T> acceptRange(Integer acceptRange){
		this.acceptRange = acceptRange;
		return this;
	}
	
	public PageableResponse<T> contentRange(Long contentRange){
		this.contentRange = contentRange;
		return this;
	}
	
	public ResponseEntity<Collection<T>> build(){
		return ResponseEntity.ok()
			.header("Accept-Range", String.valueOf(this.acceptRange))
			.header("Content-Range", String.valueOf(this.contentRange))
			.body(this.data);
	}
}
