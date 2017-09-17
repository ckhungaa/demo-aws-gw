package com.elephant.gw.filter.access;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elehpant.common.producer.hazelcast.HazelCast;
import com.elephant.common.exception.ApiException;
import com.elephant.common.exception.SystemErrorCode;
import com.elephant.common.persistence.domain.ApiPermission;
import com.elephant.gw.filter.dto.RequestContext;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class HazelCastRateLimitFilter implements ContainerRequestFilter{

	private static final Logger logger = LoggerFactory.getLogger(HazelCastRateLimitFilter.class);
	
	@Inject
	private RequestContext rc;
	
	@Inject
	@HazelCast
	private HazelcastInstance instance;
	
	private static final String API_CODE_TO_DATE_TIME_MAP = "GW_API_CODE_TO_DATE_TIME_MAP";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		logger.debug("begin");
		ApiPermission apiPermission = rc.getCurrentApiPermission();
		if (apiPermission.getRateLimit() != null) {
			logger.debug("check rate limit,setting {}",apiPermission.getRateLimit());
			//need both has value
			IMap<String, LocalDateTime> codeToDateMap = instance.getMap(API_CODE_TO_DATE_TIME_MAP);
			try {
				codeToDateMap.lock(apiPermission.getCode());
					logger.debug("less than 1 case");
					long deltaInMilliseconds = (long) (1000 / apiPermission.getRateLimit());
					LocalDateTime nextInvokeDateTime = codeToDateMap.get(apiPermission.getCode());
					logger.debug("nextInvokeDateTime {}",nextInvokeDateTime);
					boolean isWithinRateLimit = nextInvokeDateTime != null && nextInvokeDateTime.isAfter(LocalDateTime.now());
					ApiException.throwIfTrue(isWithinRateLimit, SystemErrorCode.RATE_LIMIT_EXCEEDED);
					nextInvokeDateTime = LocalDateTime.now().plus(deltaInMilliseconds, ChronoUnit.MILLIS);
					codeToDateMap.put(apiPermission.getCode(), nextInvokeDateTime,deltaInMilliseconds,TimeUnit.MILLISECONDS);
//				}
			} catch (Exception e) {
				throw e;
			}finally {
				codeToDateMap.unlock(apiPermission.getCode());
			}
			
		}
	}

}
