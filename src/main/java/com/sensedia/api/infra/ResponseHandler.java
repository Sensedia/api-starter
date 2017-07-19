package com.sensedia.api.infra;

public interface ResponseHandler<T> {

	void onResponse(T response);
	
	void onError(Exception exception);
}