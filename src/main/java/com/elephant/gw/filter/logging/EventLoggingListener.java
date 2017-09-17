package com.elephant.gw.filter.logging;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elephant.common.persistence.service.RequestLogService;
import com.elephant.common.utils.NetworkUtil;
import com.elephant.gw.filter.dto.RequestContext;

public class EventLoggingListener implements RequestEventListener{

	private static final Logger logger = LoggerFactory.getLogger(EventLoggingListener.class);
	
	private RequestContext rc;
	
    private HttpServletRequest httpServletRequest;
    
    private RequestLogService requestLogService;
	
	public EventLoggingListener(RequestContext rc,HttpServletRequest httpServletRequest,RequestLogService requestLogService) {
		this.rc = rc;
		this.httpServletRequest = httpServletRequest;
		this.requestLogService = requestLogService;
	}

	@Override
	public void onEvent(RequestEvent event) {
		switch (event.getType()) {
		case REQUEST_MATCHED:
			logger.debug("request matched");
			ContainerRequest request = event.getContainerRequest();
            request.bufferEntity();
            String requestBody = request.readEntity(String.class);
            rc.getRequestLog().setBody(requestBody);
			break;
		case FINISHED:
			logger.debug("request finished");
			try {
				request = event.getContainerRequest();
				//ip
		        String sourceIP = NetworkUtil.getIpAddress(httpServletRequest);
		        rc.getRequestLog().setIp(sourceIP);
		        //uri
		        UriBuilder requestUrlBulder = request.getUriInfo().getRequestUriBuilder()
	            		.scheme(null)
	            		.host(null)
	            		.port(-1);
		        rc.getRequestLog().setUri(requestUrlBulder.toString());

		        //request id
		        ContainerResponse response = event.getContainerResponse();
		        if (response != null) {
		        	Integer responseCode = response.getStatus();
		        	String responseMessage = response.getStatusInfo().toString();
		        	
		        	rc.getRequestLog().setResponseCode(responseCode);
		        	rc.getRequestLog().setResponseMsg(responseMessage);
		        }
		        requestLogService.save(rc.getRequestLog());
			} catch (Exception e) {
				logger.error("fail to save request log,{}",e);
			}
			
			break;
		default:
			break;
		}
		
	}

}
