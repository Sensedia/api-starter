package com.sensedia.api.infra;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

public class LoggingConfig {
	@Bean
	public Filter logRequestFilter() {
	    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
	    filter.setIncludeQueryString(true);
	    filter.setIncludePayload(true);
	    filter.setMaxPayloadLength(5120);
	    return filter;
	}
}