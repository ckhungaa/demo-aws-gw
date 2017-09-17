package com.elephant.common.config.file;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

@ApplicationScoped
public class ConfigurationValueProducer {

	@Inject
	private PropertyResolver propertyResolver;
	
	@Produces
    @ConfigurationValue
    public String getStringConfigValue(InjectionPoint ip) {
		
        ConfigurationValue configurationValue = ip.getAnnotated().getAnnotation(ConfigurationValue.class);
        String key = configurationValue.property();
        String defaultValue = configurationValue.defaultValue();
        String value = propertyResolver.getValue(key).orElse(defaultValue);
        return value;
    }
	
	@Produces
    @ConfigurationValue
    public Integer getIntegerConfigValue(InjectionPoint ip) {
		
		String value = getStringConfigValue(ip);
        return Integer.valueOf(value.trim());
    }
	
	@Produces
    @ConfigurationValue
    public Double getDoubleConfigValue(InjectionPoint ip) {
		
		String value = getStringConfigValue(ip);
        return Double.valueOf(value);
    }
	
	@Produces
    @ConfigurationValue
    public Boolean getBooleanConfigValue(InjectionPoint ip) {
		
		String value = getStringConfigValue(ip);
        return Boolean.valueOf(value);
    }
	
	
}
