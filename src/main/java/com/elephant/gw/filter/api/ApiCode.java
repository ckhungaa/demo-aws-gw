package com.elephant.gw.filter.api;

import java.util.EnumSet;
import java.util.Optional;

/**
 * Represent the API , which uniquely identify an API.
 */
public enum ApiCode {


	GET_HELLO_MESSAGE("GET_HELLO_MESSAGE",true,"test:get helloworld message")
;
	
    /**
     * Business key to uniquely identify a API.
     * This value is also stored in the DB.
     */
    private ApiCode(String code,boolean isStringResult,String description) {
        this.code = code;
        this.isStringResult = isStringResult;
        this.description = description;
    }

    private final String code;
    private final boolean isStringResult;
    private final String description;
    
    
    public static Optional<ApiCode> newFromCode(String code){
    	Optional<ApiCode> result = Optional.empty();
    	for(ApiCode apiCode  : EnumSet.allOf(ApiCode.class)){
    		if (code.equals(apiCode.getCode())){
    			result =  Optional.of(apiCode);
    			break;
    		}
    	}
    	return result;
    }
    
    public String getCode() {
        return code;
    }

    public boolean isStringResult(){
    	return isStringResult;
    }
    
    public String getDescription(){
    	return description;
    }
}
