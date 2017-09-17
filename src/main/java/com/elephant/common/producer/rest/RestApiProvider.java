package com.elephant.common.producer.rest;

import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;

import com.elephant.common.config.file.ConfigurationValue;
import com.google.common.collect.Maps;

@ApplicationScoped
public class RestApiProvider {

	
	private Map<ApiSourceType, String> apiMap;
	public static java.util.logging.Logger filterLogger = java.util.logging.Logger.getLogger(LoggingFilter.class.getName());

	
	@Produces
    @RestApiSource
    public WebTarget getStringConfigValue(InjectionPoint ip) {
		
		RestApiSource annotation = ip.getAnnotated().getAnnotation(RestApiSource.class);
        ApiSourceType sourceType = annotation.source();
      
        String apiSource = getApiSource(sourceType).orElseThrow(()->new RuntimeException("can not find api source:" + sourceType));
        ClientConfig clientConfig = new ClientConfig();

        LoggingFilter loggerFilter = new LoggingFilter(filterLogger, true);
        clientConfig = clientConfig
        	.register(loggerFilter)
        	.register(MoxyJsonFeature.class)
        	.register(JsonMoxyConfigurationContextResolver.class);

		WebTarget target = ClientBuilder.newClient(clientConfig)
			.target(apiSource);
		return target;
		
    }
	
	private Optional<String> getApiSource(ApiSourceType sourceType){
		if (apiMap == null) {
			apiMap = Maps.newConcurrentMap();
		}
		return Optional.ofNullable(apiMap.get(sourceType));
	}
}
