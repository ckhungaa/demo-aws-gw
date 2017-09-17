package com.elephant.common.exception;

import java.util.Collection;
import java.util.Optional;

import com.google.common.base.Strings;

public class ApiException extends SystemException{

	private static final long serialVersionUID = 6816212791635793096L;

	protected final SystemErrorCode errorCode;

	public ApiException(final SystemErrorCode errorCode) {
	    this.errorCode = errorCode;
	}
	
	@Override
	public SystemErrorCode getErrorCode() {
		return errorCode;
	}

	public static void throwIfTrue(boolean condition, SystemErrorCode errorCode) throws ApiException {
        if (condition) {
            throw new ApiException(errorCode);
        }
    }
    
    public static void throwIfTrue(boolean condition, ApiException e) throws ApiException {
        if (condition) {
            throw e;
        }
    }
    
    public static void throwIfFalse(boolean condition, SystemErrorCode errorCode) throws ApiException {
       throwIfTrue(!condition, errorCode);
    }
    
    public static void throwIfFalse(boolean condition, ApiException e) throws ApiException {
        throwIfTrue(!condition, e);
     }
    
    public static <T> T throwIfNull(T record, SystemErrorCode errorCode) throws ApiException {
        throwIfTrue(record == null,errorCode);
        return record;
    }

    public static <T> T throwIfEmpty(Optional<T> record, SystemErrorCode errorCode) throws ApiException {
        return throwIfNull(record.orElse(null), errorCode);
    }
    
    public static void throwIfNullOrEmptyString(String str, SystemErrorCode errorCode) throws ApiException {
        throwIfTrue(Strings.isNullOrEmpty(str),errorCode);
    }
    
    public static <T> void throwIfEmpty(Collection<T> record , SystemErrorCode errorCode) throws ApiException{
    	throwIfNull(record , errorCode);
    	throwIfTrue(record.isEmpty(),errorCode);
    }
    
    public static void throwNow(SystemErrorCode errorCode) throws ApiException {
        throw new ApiException(errorCode);
    }

	
	
	
}
