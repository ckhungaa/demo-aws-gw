package com.elephant.gw.filter.portal;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;

import com.elephant.common.constant.SystemHttpConstant;
import com.elephant.common.exception.ApiException;
import com.elephant.common.exception.SystemErrorCode;
import com.elephant.gw.filter.dto.RequestContext;

@PreMatching
public class PortalRequestHeaderStandardFilter implements ContainerRequestFilter{

	@Inject
	private RequestContext rc;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String requestId = requestContext.getHeaders().getFirst(SystemHttpConstant.REQUEST_CONVERSATION_ID);
		ApiException.throwIfNullOrEmptyString(requestId, SystemErrorCode.MISS_X_REQUEST_ID);
		rc.setRequestId(requestId);
	}

}
