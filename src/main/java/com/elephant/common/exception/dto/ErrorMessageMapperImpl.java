package com.elephant.common.exception.dto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.elephant.common.exception.SystemErrorCode;
import com.elephant.common.exception.SystemException;
import com.elephant.gw.filter.dto.RequestContext;

@ApplicationScoped
public class ErrorMessageMapperImpl implements ErrorMessageMapper{

//	private static final Logger logger = LoggerFactory.getLogger(ErrorMessageMapper.class);

	@Inject
	private RequestContext rc;
	
	@Override
	public SystemErrorCode getDefaultErrorCode(){
		return SystemErrorCode.INTERNAL_SERVER_ERROR;
	}
	
	@Override
	public ErrorResponseDto toErrorMessage(Throwable exp) {
		
		SystemErrorCode errorCode = getDefaultErrorCode();
		
		if (exp != null) {
			if (exp instanceof SystemException) {
				SystemException portalException = (SystemException) exp;
				errorCode = portalException.getErrorCode();
			} 
		}
		
		if (errorCode == null) {
			errorCode = getDefaultErrorCode();
		}
		
		ErrorResponseDto message = toErrorMessage(errorCode);
		return message;
	}
	
	private ErrorResponseDto toErrorMessage(SystemErrorCode errorCode) {
        return ErrorResponseDto.create(errorCode, rc.getRequestId());
	}

}
