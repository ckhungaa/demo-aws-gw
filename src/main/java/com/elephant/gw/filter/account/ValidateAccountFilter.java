package com.elephant.gw.filter.account;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elephant.common.constant.SystemAwsConstant;
import com.elephant.common.constant.SystemProperties;
import com.elephant.common.exception.ApiException;
import com.elephant.common.exception.SystemErrorCode;
import com.elephant.common.persistence.domain.Account;
import com.elephant.common.persistence.service.AccountService;
import com.elephant.common.utils.NetworkUtil;
import com.elephant.gw.filter.dto.RequestContext;
import com.google.common.base.MoreObjects;

/**
 * Filter to validate the Account setting
 */
@PreMatching
public class ValidateAccountFilter implements ContainerRequestFilter {
    
    static final Logger logger = LoggerFactory.getLogger(ValidateAccountFilter.class);
    
    @Inject
    private AccountService accountService;

    @Inject
    private RequestContext rc;
    
    @Context
    private HttpServletRequest httpServletRequest;
    
    @Inject
    private SystemProperties systemProperties;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

    	logger.debug("begin");
    	String xDebugPublicKey = MoreObjects.firstNonNull(containerRequestContext.getHeaderString(SystemAwsConstant.AUTH_DEBUG_PUBLIC_KEY) , "");
    	logger.debug("x-debug = {}",xDebugPublicKey);
    	if (!(systemProperties.isDebugMode() && !xDebugPublicKey.isEmpty())){
		
			//validate public key
	        String publicKey = containerRequestContext.getHeaders().getFirst(SystemAwsConstant.AUTH_PUBLIC_KEY);
	    	logger.debug("publicKey = {}",publicKey);
	        ApiException.throwIfNullOrEmptyString(publicKey, SystemErrorCode.MISS_X_AUTH_PUBLIC_KEY);
	        //get current account by public key and put it in request context
	        Optional<Account> accOpt = accountService.findByPublicKey(publicKey.trim());
	        Account currentAccount = ApiException.throwIfEmpty(accOpt, SystemErrorCode.INVALID_X_AUTH_PUBLIC_KEY);
			rc.setCurrentAccount(currentAccount);
	
			//begin validate
			//validate account is active
			ApiException.throwIfFalse(currentAccount.isActive(), SystemErrorCode.ACCOUNT_IS_NOT_ACTIVE);
			//check start date and end date
			ApiException.throwIfFalse(currentAccount.isInValidPeriod(), SystemErrorCode.ACCOUNT_IS_NOT_IN_VALID_PERIOD);
	
			//check request IP is valid
	        String sourceIP = NetworkUtil.getIpAddress(httpServletRequest);
	    	logger.debug("sourceIP = {}",sourceIP);
	        ApiException.throwIfFalse(currentAccount.isValidIp(sourceIP), SystemErrorCode.REQUEST_IP_NOT_ACCEPTED);

    	}else {
    		//
    		Optional<Account> accOpt = accountService.findByPublicKey(xDebugPublicKey.trim());
	        Account currentAccount = ApiException.throwIfEmpty(accOpt, SystemErrorCode.INVALID_X_DEBUG_PUBLIC_KEY);
			rc.setCurrentAccount(currentAccount);
		}
   }
}
