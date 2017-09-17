package com.elephant.gw.filter.api;

import javax.inject.Inject;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elephant.common.constant.SystemApplicationConstant;
import com.elephant.common.persistence.service.AccountService;
import com.elephant.gw.filter.dto.RequestContext;

/**
 * Register the necessary providers for each resource method
 */
@PreMatching
public class InvokedApiMapper implements DynamicFeature {
	
	@Inject
	private RequestContext requestContext;
	
	@Inject
	private AccountService accountService;
	
	private static final Logger logger = LoggerFactory.getLogger(InvokedApiMapper.class);
	
	
	@Override
	public void configure(ResourceInfo ri, FeatureContext context) {
		logger.debug("configure");
		configureCloudApi(ri, context);
	}

	private void configureCloudApi(ResourceInfo ri, FeatureContext context) {
		ApiId api = ri.getResourceMethod().getAnnotation(ApiId.class);
		logger.debug("is api found: {}",api == null);
		
		if (api != null) {
			ApiPermissionFilter filter = new ApiPermissionFilter(api , requestContext,accountService);
			context.register(filter, SystemApplicationConstant.PRIORIRTY_CONTENT_RESOLVER);
		}
	}

}
