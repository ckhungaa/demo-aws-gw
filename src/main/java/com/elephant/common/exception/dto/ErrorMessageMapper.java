package com.elephant.common.exception.dto;

import com.elephant.common.exception.SystemErrorCode;

public interface ErrorMessageMapper {

   public SystemErrorCode getDefaultErrorCode();
    
	public ErrorResponseDto toErrorMessage(Throwable exp);
}
