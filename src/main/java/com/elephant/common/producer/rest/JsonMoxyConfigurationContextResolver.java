package com.elephant.common.producer.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

@Provider
public class JsonMoxyConfigurationContextResolver implements ContextResolver<MoxyJsonConfig> {
 
    private final MoxyJsonConfig config;
 
    public JsonMoxyConfigurationContextResolver() {
 
    	config = new MoxyJsonConfig();
    	config.setFormattedOutput(true);
    	config.setMarshalEmptyCollections(false);
    	config.setIncludeRoot(false);
    }
 
    @Override
    public MoxyJsonConfig getContext(Class<?> objectType) {
        return config;
    }
}

