package com.sensedia.api.app;

import java.util.Objects;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
	
	@Bean
	public RestTemplate restTemplate(@Value("${rest.gw.timeout}") Integer servicetimeout,
			@Value("${rest.gw.max.connection.pool}") Integer maxConnPool){
		
		String httpProxyHost = System.getProperty("http.proxyHost");
		String httpProxyPort = System.getProperty("http.proxyPort");
		String httpProxyUser = System.getProperty("http.proxyUser");
		String nonProxyHosts = System.getProperty("http.nonProxyHosts");
		String httpProxyPassword = System.getProperty("http.proxyPassword");
		
		HttpClientBuilder builder = HttpClientBuilder.create()
				.useSystemProperties()
				.setMaxConnPerRoute(maxConnPool)
				.setMaxConnTotal(maxConnPool);
				
		if(this.nonEmpty(httpProxyHost)){
			Integer proxyPort = Integer.valueOf(httpProxyPort);
			HttpHost proxyHost = new HttpHost(httpProxyHost, proxyPort);
			builder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
			builder.setRoutePlanner(new DefaultProxyRoutePlanner(proxyHost){
				@Override
                public HttpHost determineProxy(HttpHost target,
                        HttpRequest request, HttpContext context)
                                throws HttpException {
                    
					if (nonProxyHosts.contains(target.getHostName())) {
                        return null;
                    }
					
                    return super.determineProxy(target, request, context);
                }
			});
			
			if(this.nonEmpty(httpProxyUser)){
				CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		        credentialsProvider.setCredentials( 
		                new AuthScope(httpProxyHost, proxyPort), 
		                new UsernamePasswordCredentials(httpProxyUser, httpProxyPassword));
		        builder.setDefaultCredentialsProvider(credentialsProvider);
			}
		}

		HttpClient httpClient = builder.build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		
		factory.setHttpClient(httpClient);
		//set the timeout to waiting an available connection from the pool 
	    factory.setConnectionRequestTimeout(servicetimeout);
	    //set the timeout to connect
	    factory.setConnectTimeout(servicetimeout);
	    //set the timeout to receive the response
	    factory.setReadTimeout(servicetimeout);
		
		return new RestTemplate(new BufferingClientHttpRequestFactory(factory));
	}
	
	private boolean nonEmpty(String value){
		return Objects.nonNull(value) && !"".equals(value);
	}
}