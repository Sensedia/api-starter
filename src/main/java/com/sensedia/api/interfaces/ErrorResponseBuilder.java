package com.sensedia.api.interfaces;

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponseBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(ErrorResponseBuilder.class);
	
	private ResponseEntity<?> response;
	
	private ErrorResponseBuilder(Exception e) {
		this.init(e);
	}
	
	private void init(Exception e){
		if(e instanceof DataIntegrityViolationException){
			DataIntegrityViolationException dive = (DataIntegrityViolationException) e;
			String message = dive.getMessage().toUpperCase();
			if(dive.getMostSpecificCause() != null) {
				message = dive.getMostSpecificCause().getMessage().toUpperCase();
			}
			
			Iterable<String> keys = Dictionary.get().keys();
			for(String key : keys) {
				if(message.contains(key)){
					this.response = toResponse(key);
				}
			}
			
			return;
		}
		
		this.response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	private ResponseEntity<Set<Error>> toResponse(String key) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
			.body(Collections.singleton(new Error(key)));
	}
	
	public ErrorResponseBuilder withDefaultIntegrityErrorKey(String key){
		if(this.response == null) {
			this.response = this.toResponse(key);
		}
		return this;
	}
	
	public ResponseEntity<?> build(){
		return this.response;
	}
	
	public static ErrorResponseBuilder onException(Exception e){
		logger.error(e.getMessage(), e);
		return new ErrorResponseBuilder(e);
	}
}