package com.elephant.common.exception;

import com.elephant.common.exception.SystemErrorCode;

public abstract class SystemException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3165184863594379874L;

	public abstract SystemErrorCode getErrorCode();
}
