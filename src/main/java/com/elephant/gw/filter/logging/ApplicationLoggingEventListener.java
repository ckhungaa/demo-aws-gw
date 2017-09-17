package com.elephant.gw.filter.logging;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import com.elephant.common.persistence.service.RequestLogService;
import com.elephant.gw.filter.dto.RequestContext;

@Provider
public class ApplicationLoggingEventListener implements ApplicationEventListener {

    @Context
    private HttpServletRequest httpServletRequest;
    
    @Inject
    private RequestLogService requestLogService;
    
    @Inject
    private RequestContext rc;
    
	@Override
	public void onEvent(ApplicationEvent event) {
		
	}

	@Override
	public RequestEventListener onRequest(RequestEvent requestEvent) {
		return new EventLoggingListener(rc,httpServletRequest,requestLogService);
	}
}