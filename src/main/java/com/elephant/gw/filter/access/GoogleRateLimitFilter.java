package com.elephant.gw.filter.access;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elephant.common.exception.ApiException;
import com.elephant.common.exception.SystemErrorCode;
import com.elephant.common.persistence.domain.ApiPermission;
import com.elephant.gw.filter.dto.RequestContext;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;

public class GoogleRateLimitFilter implements ContainerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(GoogleRateLimitFilter.class);

	@Inject
	private RequestContext rc;

	private static Map<String, RateLimiter> codeToRateLimiterMap = Maps.newConcurrentMap();

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		logger.debug("begin");
		ApiPermission apiPermission = rc.getCurrentApiPermission();
		if (apiPermission.getRateLimit() != null) {
			logger.debug("check rate limit,current setting is {}",apiPermission.getRateLimit());
			// need both has value
			RateLimiter rateLimiter = codeToRateLimiterMap.get(apiPermission.getCode());
			if (rateLimiter == null) {
				logger.debug("create new one");
				rateLimiter = RateLimiter.create(apiPermission.getRateLimit());
				codeToRateLimiterMap.put(apiPermission.getCode(), rateLimiter);
			}
			// config updated?
			if (rateLimiter.getRate() != apiPermission.getRateLimit()) {
				logger.debug("config updated,repleace old one");
				rateLimiter = RateLimiter.create(apiPermission.getRateLimit());
				codeToRateLimiterMap.put(apiPermission.getCode(), rateLimiter);
			}
			ApiException.throwIfFalse(rateLimiter.tryAcquire(), SystemErrorCode.RATE_LIMIT_EXCEEDED);
		}
	}

}
