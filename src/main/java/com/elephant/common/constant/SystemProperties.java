package com.elephant.common.constant;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.elephant.common.config.file.ConfigurationValue;
import com.google.common.base.MoreObjects;

@ApplicationScoped
public class SystemProperties {

	@Inject
	@ConfigurationValue(property = "system.env.is_debug",defaultValue = "true")
	private Boolean isDebugMode;
	
	public boolean isDebugMode(){
		return MoreObjects.firstNonNull(isDebugMode, true);
	}
	
	@Inject
	@ConfigurationValue(property = "system.env.aws.max_accepted_time_difference_in_second",defaultValue = "60")
	private Integer maxAcceptedTimeDiffInSecondForAws;
	
	public Long getMaxAccecptedTimeDiffInSecondForAws(){
		return maxAcceptedTimeDiffInSecondForAws.longValue();
	}
	
	
	
}
