package com.elephant.common.exception.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elephant.common.constant.SystemHttpConstant;
import com.elephant.common.exception.SystemErrorCode;
import com.elephant.common.utils.JAXBUtil;
import com.google.common.base.Objects;

/**
 * Represent a error response
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponseDto {

	private static final SystemErrorCode DEFAULT_PORTAL_ERROR_CODE = SystemErrorCode.INTERNAL_SERVER_ERROR;
	private static final String DEFAULT_REQUEST_ID = "UNKNOW";

	
	@XmlElement(name = SystemHttpConstant.RESPONSE_HEADER_REQUEST_ID)
	private String requestId;
	
	@XmlElement(name = SystemHttpConstant.RESPONSE_HEADER_RESPONSE_CODE)
	private String responseCode;
	
	@XmlElement(name = SystemHttpConstant.RESPONSE_HEADER_RESPONSE_MESSAGE)
	private String responseMsg;
	
	@XmlElement(name = SystemHttpConstant.RESPONSE_HEADER_RESPONSE_CONTENT)
	private String responseContent;
	
	@XmlTransient
	private SystemErrorCode errorCode;
	
	@XmlTransient
	private Integer httpResponseCode;
    
	
    private ErrorResponseDto(SystemErrorCode errorCode,String requestId) {
    	this.requestId = requestId;
        this.responseCode = errorCode.getResponseCode();
        this.responseMsg= errorCode.getResponseMsg();
        this.httpResponseCode = errorCode.getHttpResponseCode();
        this.errorCode = errorCode;
    }
    
    // For Moxy used only
    ErrorResponseDto() {
        this(DEFAULT_PORTAL_ERROR_CODE,DEFAULT_REQUEST_ID);
    }


    
    public static ErrorResponseDto create(SystemErrorCode errorCode,String requestId){
    	ErrorResponseDto msg = new ErrorResponseDto(errorCode,requestId);
    	return msg;
    }

    public SystemErrorCode getErrorCode(){
    	return errorCode;
    }
    
	public String getRequestId() {
		return requestId;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public String getResponseContent() {
		return responseContent;
	}

	public Integer getHttpResponseCode(){
		return this.httpResponseCode;
	}
	
	@Override
    public int hashCode() {
	    return Objects.hashCode(responseCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ErrorResponseDto other = (ErrorResponseDto) obj;
        return Objects.equal(other.responseCode, this.responseCode);
    }

    @Override
    public String toString() {
    	return String.format("Http Reponse code: %s \nRequest Body: \n%s ", this.responseCode , JAXBUtil.toJson(this));
    }

}
