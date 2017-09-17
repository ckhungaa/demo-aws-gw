package com.elephant.gw.filter.exception;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elephant.common.exception.dto.ErrorMessageMapper;
import com.elephant.common.exception.dto.ErrorResponseDto;

@Provider
public class ExceptionMappingFilter implements ExceptionMapper<Throwable>{

	private static final Logger logger = LoggerFactory.getLogger(ExceptionMappingFilter.class);
	
	@Inject
	private ErrorMessageMapper errorMessageMapper;
	
	@Override
	public Response toResponse(Throwable exception) {
		logger.debug("check web application exception");

		if (exception instanceof WebApplicationException) {
			logger.debug(" web application exception");
			return ((WebApplicationException) exception).getResponse();
		}
		logger.debug("not web application exception");
		ErrorResponseDto response = errorMessageMapper.toErrorMessage(exception);

		if (response.getErrorCode() == errorMessageMapper.getDefaultErrorCode()) {
			logException(response.getResponseMsg(), exception);
		}
		
		return Response.status(response.getHttpResponseCode())
                .entity(response)
                .build();
	}
	
	private void logException(String message,Throwable exception){
		logger.error(message,exception);
	}
	
	
}
