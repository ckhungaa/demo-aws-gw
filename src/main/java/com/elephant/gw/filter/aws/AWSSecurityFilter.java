package com.elephant.gw.filter.aws;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elephant.common.constant.SystemAwsConstant;
import com.elephant.common.constant.SystemProperties;
import com.elephant.common.exception.ApiException;
import com.elephant.common.exception.SystemErrorCode;
import com.elephant.common.exception.SystemException;
import com.elephant.common.persistence.domain.Account;
import com.elephant.gw.filter.dto.RequestContext;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class AWSSecurityFilter implements ContainerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(AWSSecurityFilter.class);

	
	@Inject
	private ApiSignatureServiceImpl apiSignatureService;
	
	@Inject
	private RequestContext rc;

	@Inject
	private SystemProperties systemProperties;
	
	
	@Override
	public void filter(ContainerRequestContext containerRequestContext) throws IOException {
		
		logger.debug("begin");
    	String xDebugValue = MoreObjects.firstNonNull(containerRequestContext.getHeaderString(SystemAwsConstant.AUTH_DEBUG_PUBLIC_KEY) , "");
    	logger.debug("x-debug = {}",xDebugValue);
    	if (!(systemProperties.isDebugMode() && !xDebugValue.isEmpty())){
			Optional<Account> accountOpt = rc.getCurrentAccount();
			Account account = ApiException.throwIfEmpty(accountOpt, SystemErrorCode.INTERNAL_SERVER_ERROR_FILTER_FLOW);
			
			//current path need authentication, get signing key
			String privateKey = account.getPrivateKey();
			
			// Get request information (GET,POST,DELETE,PUT)
			String httpMethod = containerRequestContext.getMethod();
			logger.debug("httpMethod={}",httpMethod);
			
			//use getRawPath() to satisfy  RFC 3986
			String uri = containerRequestContext.getUriInfo().getRequestUri().getRawPath();
			logger.debug("uri={}",uri);
			
			//use getQueryParameters(false) to satisfy RFC 3986
			MultivaluedMap<String, String> multiValueQueryMap = containerRequestContext.getUriInfo().getQueryParameters(false);
			String canonicalizedQueryParam = apiSignatureService.canonicalizeQueryParameter(multiValueQueryMap);
			logger.debug("canonicalizedQueryParam={}",canonicalizedQueryParam);
			
			
			//signedHeaders means client represent the server what header parameter they included into calculation
			//because header may be changed
			//e.g: proxy server may add client's ip into header
			String signedHeaders = containerRequestContext.getHeaders().getFirst(SystemAwsConstant.AUTH_SIGNED_HEADERS);
			logger.debug("signedHeaders={}",signedHeaders);
			
			String authSignature = containerRequestContext.getHeaders().getFirst(SystemAwsConstant.AUTH_SIGNATURE);
			logger.debug("authSignature={}",authSignature);
			
			String requestTimestamp = containerRequestContext.getHeaders().getFirst(SystemAwsConstant.AUTH_REQUEST_TIMESTAMP);
			logger.debug("requestTimestamp={}",requestTimestamp);
			
			ApiException.throwIfNullOrEmptyString(signedHeaders, SystemErrorCode.MISS_PARAM_X_AUTH_SIGNED_HEADER);
			ApiException.throwIfNullOrEmptyString(authSignature, SystemErrorCode.MISS_PARAM_X_AUTH_SIGNATURE);
			ApiException.throwIfNullOrEmptyString(requestTimestamp, SystemErrorCode.MISS_PARAM_X_AUTH_REQUEST_TIMESTAMP);

				try {
					apiSignatureService.validateSignedHeaders(signedHeaders);
					List<String> signedHeaderList = Lists.newArrayList(signedHeaders.split(";"));

					MultivaluedMap<String, String> multiValueHeaderMap = containerRequestContext.getHeaders();
					apiSignatureService.validateHeaders(multiValueHeaderMap, signedHeaderList);
					apiSignatureService.validateRequestTimestamp(requestTimestamp);

					String canonicalHeaderParam = apiSignatureService.canonicalizeHeaderParameter(multiValueHeaderMap, signedHeaderList);
					String hashedPayload = "";
					
					if (containerRequestContext.hasEntity() ) {
						logger.debug("has entity");
			            ContainerRequest request = (ContainerRequest) containerRequestContext;
			            request.bufferEntity();
			            String payload = request.readEntity(String.class);
						logger.debug("payload: {} ",payload);
			            hashedPayload = apiSignatureService.getHashedPayload(payload);
					}else if (!Strings.isNullOrEmpty( rc.getRequestLog().getBody())) {
						String payload = rc.getRequestLog().getBody();
						logger.debug("payload: {} ",payload);
						hashedPayload = apiSignatureService.getHashedPayload(payload);
					}
					String canonicalRequest = apiSignatureService.getCanonicalRequest(httpMethod, uri, canonicalizedQueryParam,
							canonicalHeaderParam, signedHeaders,hashedPayload
							);
					String calculatedSignature = apiSignatureService.calculateSignature(privateKey, canonicalRequest);

					logger.debug("privateKey = {}",privateKey);
					logger.debug("httpMethod = {}",httpMethod);
					logger.debug("uri = {}",uri);
					logger.debug("canonicalizedQueryParam = {}", canonicalizedQueryParam);
					logger.debug("canonicalizeHeaderParam = {}", canonicalHeaderParam);
					logger.debug("signedHeaders = {}", signedHeaders);
					logger.debug("hashedPayload = {}", hashedPayload);
					logger.debug("canonicalRequest={}", canonicalRequest);
					logger.debug("calculatedSignature = {}", calculatedSignature);
					
					apiSignatureService.validateSignature(authSignature, calculatedSignature);
				} catch (SystemException e) {
					logger.warn("Aborted. Reason: {}", e.getMessage());
					throw e; //let it throw
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Aborted. Reason: {}", e.getMessage());
					throw e;
				}
				// Everything is OK
				
//			}
		}
	}
}
