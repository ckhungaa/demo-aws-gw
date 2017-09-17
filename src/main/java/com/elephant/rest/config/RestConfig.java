package com.elephant.rest.config;

import java.util.logging.Logger;

import javax.json.stream.JsonGenerator;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;

import com.elephant.common.constant.SystemApplicationConstant;
import com.elephant.gw.filter.access.GoogleRateLimitFilter;
import com.elephant.gw.filter.account.ValidateAccountFilter;
import com.elephant.gw.filter.api.InvokedApiMapper;
import com.elephant.gw.filter.aws.AWSSecurityFilter;
import com.elephant.gw.filter.exception.ExceptionMappingFilter;
import com.elephant.gw.filter.logging.ApplicationLoggingEventListener;
import com.elephant.gw.filter.portal.PortalRequestHeaderStandardFilter;

@ApplicationPath("v1")
public class RestConfig extends ResourceConfig{

	private LoggingFilter loggerFilter = new LoggingFilter(Logger.getLogger(LoggingFilter.class.getName()), true);
	public RestConfig(){
		register(ExceptionMappingFilter.class);
		register(InvokedApiMapper.class,SystemApplicationConstant.PRIORIRTY_CONTENT_RESOLVER);
		register(ValidateAccountFilter.class);
		register(AWSSecurityFilter.class,SystemApplicationConstant.PRIORIRTY_AUTHORIZATION);
		register(PortalRequestHeaderStandardFilter.class);
		register(ApplicationLoggingEventListener.class);
		register(GoogleRateLimitFilter.class,SystemApplicationConstant.PRIORIRTY_LAST);

		register(loggerFilter);
        property(JsonGenerator.PRETTY_PRINTING, true);
		setupMOXyJsonBinding();
	}
	
	 private void setupMOXyJsonBinding() {
	        final MoxyJsonConfig moxyConfig = new MoxyJsonConfig();
	        moxyConfig.setFormattedOutput(true);
	        moxyConfig.setMarshalEmptyCollections(true);
	        register(moxyConfig.resolver());
	 }
}
