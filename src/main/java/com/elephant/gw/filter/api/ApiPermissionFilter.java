package com.elephant.gw.filter.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elephant.common.exception.ApiException;
import com.elephant.common.exception.SystemErrorCode;
import com.elephant.common.persistence.domain.Account;
import com.elephant.common.persistence.domain.ApiPermission;
import com.elephant.common.persistence.service.AccountService;
import com.elephant.gw.filter.dto.RequestContext;

public class ApiPermissionFilter implements ContainerRequestFilter{

	private static final Logger logger = LoggerFactory.getLogger(ApiPermissionFilter.class);
	
	private final ApiId currentInvokeApi;

	private RequestContext requestContext;
	
	private AccountService accountService;
	
	public ApiPermissionFilter(ApiId api, RequestContext requestContext,AccountService accountService) {
		checkNotNull(api, "Current Invoke API should is required");
		this.currentInvokeApi = api;
		this.requestContext = requestContext;
		this.accountService = accountService;
	}

	@Override
	public void filter(ContainerRequestContext arg0) throws IOException {
		logger.debug("begin");
		//validate API Permission
		Account account = ApiException.throwIfEmpty(requestContext.getCurrentAccount(), SystemErrorCode.INTERNAL_SERVER_ERROR_FILTER_FLOW);
        Optional<ApiPermission> apiPermissionOpt = accountService.findByAccountAndCode(account, currentInvokeApi.value().getCode());
        ApiPermission apiPermission = ApiException.throwIfEmpty(apiPermissionOpt, SystemErrorCode.NO_PERMISSION);
        ApiException.throwIfFalse(apiPermission.isEnabled(), SystemErrorCode.TARGET_API_IS_NOT_ENABLED);
        ApiException.throwIfFalse(apiPermission.isInValidPeriod(), SystemErrorCode.TARGET_API_IS_NOT_IN_VALID_PERIOD);
		this.requestContext.setInvokedApi(currentInvokeApi.value(),apiPermission);
		this.requestContext.setCurrentApiPermission(apiPermission);
	}

}
