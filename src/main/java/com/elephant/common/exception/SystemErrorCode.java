package com.elephant.common.exception;

public enum SystemErrorCode {

	MISS_X_AUTH_SIGNED_HEADER_VALUE_IN_HTTP_HEADER("ELEPHANT-GW-1001","Missing some header parameters indicated in 'x-auth-signedheaders'.",400),
	SIGNATURE_NOT_MATCH("ELEPHANT-GW-1002","Calculated signature and x-auth-signature is not matched.",401),
	INVALID_X_AUTH_REQUEST_TIMESTAMP("ELEPHANT-GW-1003","The header parameter 'x-auth-request-timestamp' is not within accepted interval",400),
	X_AUTH_SIGNED_HEADER_HAS_INVALID_VALUE_COOKIE("ELEPHANT-GW-1004","Cookie should not be included in x-auth-signed-headers.",400),
	INVALID_X_AUTH_PUBLIC_KEY("ELEPHANT-GW-1005","The 'x-auth-api-key' is invalid.",400),
	ACCOUNT_IS_NOT_ACTIVE("ELEPHANT-GW-1006","The target account is not active.",400),
	ACCOUNT_IS_NOT_IN_VALID_PERIOD("ELEPHANT-GW-1007","The target account is not in valid period.",400),
	REQUEST_IP_NOT_ACCEPTED("ELEPHANT-GW-1008","The request ip is not in white IP list.",401),
	
	MISS_X_AUTH_REQUEST_TIMESTAMP_IN_X_AUTH_SIGNED_HEADER("ELEPHANT-GW-1009","Please include 'x-auth-request-timestamp' in your header parameter 'x-auth-signedheaders'.",400),
	MISS_X_AUTH_PUBLIC_KEY("ELEPHANT-GW-1010","Missing header parameter 'x-auth-api-key', please refer to API guide",400),
	MISS_PARAM_X_AUTH_SIGNED_HEADER("ELEPHANT-GW-1011","Missing header parameter 'x-auth-signed-headers'.",401),
	MISS_PARAM_X_AUTH_SIGNATURE("ELEPHANT-GW-1012","Missing header parameter 'x-auth-signature'.",400),
	MISS_PARAM_X_AUTH_REQUEST_TIMESTAMP("ELEPHANT-GW-1013","Missing header parameter 'x-auth-request-timestamp'.",400),
	
	NO_PERMISSION("ELEPHANT-GW-1014","Current account does not have permission to invoke target API.",400),
	TARGET_API_IS_NOT_ENABLED("ELEPHANT-GW-1015","The target API is not enabled.",400),
	TARGET_API_IS_NOT_IN_VALID_PERIOD("ELEPHANT-GW-1016","The target API is not in valid period.",400),
	MISS_X_REQUEST_ID("ELEPHANT-GW-1017","Missing header parameter 'x-request-id'.",400),
	RATE_LIMIT_EXCEEDED("ELEPHANT-GW-1018","Rate limit exceeded for this api, please try later.",429),
	INVALID_X_AUTH_REQUEST_TIMESTAMP_FORMAT("ELEPHANT-GW-1019","The header parameter 'x-auth-request-timestamp' has invalid format.",400),

	NO_SITE_PERMISSION("ELEPHANT-GW-1020","Current account deos not have permission to access target site.",403),

	//debug
	INVALID_X_DEBUG_PUBLIC_KEY("ELEPHANT-GW-1021","The 'x-debug-api-key' is invalid.",400),

	INTERNAL_SERVER_ERROR("ELEPHANT-GW-0001","Unknown  error.",500),
	INTERNAL_SERVER_ERROR_FILTER_FLOW("ELEPHANT-GW-0002","System filter configuration error, please check the priority.",500),
	INTERNAL_SERVER_ERROR_API_MAPPING("ELEPHANT-GW-0003","Internal error,some API is not mapped by api code.",500);

	private String responseCode = "";
	private String responseMsg = "";
	private Integer httpResponseCode;


	private SystemErrorCode(String responseCode,String responseMsg,  Integer httpResponseCode){
		this.responseMsg = responseMsg;
		this.httpResponseCode=httpResponseCode;
		this.responseCode = responseCode;
	}

	public String getResponseCode(){
		return responseCode;
	}
	
	public String getResponseMsg() {
		return responseMsg;
	}

	public Integer getHttpResponseCode() {
		return httpResponseCode;
	}
	
	public static SystemErrorCode fromResponseCode(String responseCode){
		for (SystemErrorCode errorCode : SystemErrorCode.values()) {
			if (errorCode.responseCode.equals(responseCode)) {
				return errorCode;
			}
		}
		return INTERNAL_SERVER_ERROR;
	}
	
	public static void main(String[] args){
		for (SystemErrorCode code :SystemErrorCode.values()) {
			System.out.println("\"" + code.httpResponseCode + "\",\"" + code.responseCode + "\",\"" + code.responseMsg + "\"");
		}
	}
	
}
