package com.elephant.gw.filter.aws;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.binary.Hex;

import com.elephant.common.constant.SystemAwsConstant;
import com.elephant.common.constant.SystemProperties;
import com.elephant.common.exception.ApiException;
import com.elephant.common.exception.SystemErrorCode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RequestScoped
public class ApiSignatureServiceImpl implements ApiSignatureService {
	
	private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	private static final String contentDigestAlgorithm = "SHA-256";
	private static final String signatureCalculationAlgorithm = "HmacSHA256";
	
	@Inject
	private SystemProperties systemProperties;

	public ApiSignatureServiceImpl(){
		
	}
	
	@Override
	public String getCanonicalRequest(String httpMethod, String canonicalURI,String canonicalQueryString, String canonicalHeaders,String signedHeaders, String hashedPayload) {
		
		StringBuilder canonicalRequest = new StringBuilder();
		canonicalRequest.append(httpMethod);
		canonicalRequest.append("\n");
		canonicalRequest.append(canonicalURI);
		canonicalRequest.append("\n");
		if(!Strings.isNullOrEmpty(canonicalQueryString)) canonicalRequest.append(canonicalQueryString);
		canonicalRequest.append("\n");
		canonicalRequest.append(canonicalHeaders);
		canonicalRequest.append("\n");
		canonicalRequest.append(signedHeaders);
		canonicalRequest.append("\n");
		canonicalRequest.append(hashedPayload);
		
		return canonicalRequest.toString();
	}

	@Override
	public String uriEncode(CharSequence input, boolean encodeSlash) {
		
		StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '-' || ch == '~' || ch == '.') {
                result.append(ch);
            } else if (ch == '/') {
                result.append(encodeSlash ? "%2F" : ch);
            } else {
                result.append("%" + Integer.toHexString(ch).toUpperCase());
            }
        }
        return result.toString();
	}

	@Override
	public String calculateSignature(String signingKey, String canonicalRequest) {
		
		try {
			Mac sha256_HMAC = Mac.getInstance(signatureCalculationAlgorithm);
			SecretKeySpec secret_key = new SecretKeySpec(signingKey.getBytes(), signatureCalculationAlgorithm);
			sha256_HMAC.init(secret_key);
			return Hex.encodeHexString(sha256_HMAC.doFinal(canonicalRequest.getBytes()));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void validateSignedHeaders(String signedHeaders) {
		
		List<String> signedHeaderList = Lists.newArrayList(signedHeaders.split(";"));
		
		//x-auth-request-timestamp must be included into signature calculation
		ApiException.throwIfTrue(!signedHeaderList.contains(SystemAwsConstant.AUTH_REQUEST_TIMESTAMP), SystemErrorCode.MISS_X_AUTH_REQUEST_TIMESTAMP_IN_X_AUTH_SIGNED_HEADER);
		ApiException.throwIfTrue(signedHeaderList.contains("Cookie"), SystemErrorCode.X_AUTH_SIGNED_HEADER_HAS_INVALID_VALUE_COOKIE);
	
	}

	@Override
	public void validateRequestTimestamp(String requestTimestamp){
		try {
			OffsetDateTime odt = OffsetDateTime.parse(requestTimestamp, dtf);
			long interval = OffsetDateTime.now().toEpochSecond() - odt.toEpochSecond();
			
			//server accept interval < 0
			ApiException.throwIfTrue(Math.abs(interval) > systemProperties.getMaxAccecptedTimeDiffInSecondForAws(), SystemErrorCode.INVALID_X_AUTH_REQUEST_TIMESTAMP);
		} catch (DateTimeParseException  e) {
			ApiException.throwNow(SystemErrorCode.INVALID_X_AUTH_REQUEST_TIMESTAMP_FORMAT);
		}
		
	}

	public String getHashedPayload(String content){
		
		String contentHash = "";
		try {
			MessageDigest digest = MessageDigest.getInstance(contentDigestAlgorithm);
			DigestInputStream digestInputStream = new DigestInputStream(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), digest);
			byte[] buffer = new byte[1024];
			int read = digestInputStream.read(buffer);
			while (read != -1) {
	            read = digestInputStream.read(buffer);
	        }
			contentHash = Hex.encodeHexString(digestInputStream.getMessageDigest().digest());
		}
		catch(NoSuchAlgorithmException | IOException e) {
			ApiException.throwNow(SystemErrorCode.INTERNAL_SERVER_ERROR);
		}
		return contentHash.toLowerCase();
	}

	@Override
	public void validateSignature(String authSignature,String calculatedSignature){
		
		ApiException.throwIfTrue(!authSignature.trim().equals(calculatedSignature.trim()), SystemErrorCode.SIGNATURE_NOT_MATCH);
		
	}

	/*
	 * Explain: 
	 * 		Sort the multiValueQueryMap by key
	 * 		Join key and value by "="
	 * 		Join the entries by "&"
	 * Example:
	 *  Action=ListUsers&
		Version=2010-05-08&
		X-Amz-Algorithm=AWS4-HMAC-SHA256&
		X-Amz-Credential=AKIDEXAMPLE%2F20150830%2Fus-east-1%2Fiam%2Faws4_request&
		X-Amz-Date=20150830T123600Z&
		X-Amz-SignedHeaders=content-type%3Bhost%3Bx-amz-date
	 */
	@Override
	public String canonicalizeQueryParameter(MultivaluedMap<String, String> multiValueQueryMap) {
		
		Map<String, String> queryParams = Maps.newHashMap();
		for (String key : multiValueQueryMap.keySet()) {
			//compare with AWS S2, the authentication parameters move from query parameters into header
			//=> no need to exclude some parameters from query map
			queryParams.put(key, multiValueQueryMap.getFirst(key));
		}
		
		queryParams = MoreObjects.firstNonNull(queryParams, new HashMap<String, String>());
		SortedMap<String, String> sortedParamMap = new TreeMap<String, String>(queryParams);

		if (sortedParamMap.isEmpty()) {
			return "";
		}

		StringJoiner joiner = new StringJoiner("&");
		for (Map.Entry<String, String> entry : sortedParamMap.entrySet()) {
			joiner.add(entry.getKey() + "=" + entry.getValue());
		}
		return joiner.toString();
	}

	/*
	 * Explain:
	 * 		Create a map = Filter the multiValueQueryMap by headerFilter, Change the key = LowerCase(key), value =Trim(value) 
	 *  	Sort the created map by key
	 *  	Join key and value by ":"
	 *  	Join each Entries by "\n"
	 * 
	 * Example 
	 * 	content-type:application/x-www-form-urlencoded; charset=utf-8\n
		host:iam.amazonaws.com\n
		x-amz-date:20150830T123600Z\n

	 */
	@Override
	public String canonicalizeHeaderParameter(MultivaluedMap<String, String> multiValueQueryMap,List<String> headerFilter) {
		
		SortedMap<String, String> sortedHeaderMap = Maps.newTreeMap();
		headerFilter.forEach(headerKey ->{
			String value = MoreObjects.firstNonNull(multiValueQueryMap.getFirst(headerKey), "");
			sortedHeaderMap.put(headerKey.toLowerCase(), value.trim());
		});
		
		if (sortedHeaderMap.isEmpty()) {
			return "";
		}
		
		StringJoiner joiner = new StringJoiner("\n");
		for (Map.Entry<String, String> entry : sortedHeaderMap.entrySet()) {
			joiner.add(entry.getKey() + ":" + entry.getValue());
		}
		return joiner.toString();	}

	@Override
	public void validateHeaders(MultivaluedMap<String, String> multiValueQueryMap, List<String> headerFilter){
		
		boolean isContain = multiValueQueryMap.keySet().containsAll(headerFilter);
		ApiException.throwIfTrue(!isContain, SystemErrorCode.MISS_X_AUTH_SIGNED_HEADER_VALUE_IN_HTTP_HEADER);
	}

}
