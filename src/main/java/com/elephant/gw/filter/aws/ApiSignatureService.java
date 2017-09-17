package com.elephant.gw.filter.aws;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

public interface ApiSignatureService {

	/**
	 * 
	 * @param httpMethod
	 *            is one of the HTTP methods, for example GET, PUT, HEAD, and
	 *            DELETE.
	 * @param canonicalURI
	 *            is the URI-encoded version of the absolute path component of
	 *            the URI?”everything starting with the "/" that follows the
	 *            domain name and up to the end of the string or to the question
	 *            mark character ('?') if you have query string parameters. The
	 *            URI in the following example, /example/myphoto.jpg, is
	 *            the absolute path and you don't encode the "/" in the absolute
	 *            path: http://hostname/example/myphoto.jpg
	 * @param canonicalQueryString
	 *            specifies the URI-encoded query string parameters. You
	 *            URI-encode name and values individually. You must also sort
	 *            the parameters in the canonical query string alphabetically by
	 *            key name. The sorting occurs after encoding. The query string
	 *            in the following URI example is
	 *            prefix=somePrefix&marker=someMarker&max-keys=20:
	 *            http://hostname/example?prefix=somePrefix&marker=
	 *            someMarker&max-keys=20
	 * @param canonicalHeaders
	 *            is a list of request headers with their values. Individual
	 *            header name and value pairs are separated by the newline
	 *            character ("\n"). Header names must be in lowercase. You must
	 *            sort the header names alphabetically to construct the string,
	 *            as shown in the following example:
	 *            Lowercase(<HeaderName1>)+":"+Trim(<value>)+"\n"
	 *            Lowercase(<HeaderName2>)+":"+Trim(<value>)+"\n" ...
	 *            Lowercase(<HeaderNameN>)+":"+Trim(<value>)+"\n" The
	 *            CanonicalHeaders list must include the following:
	 *            x-auth-request-timestamp
	 * @param signedHeaders
	 *            is an alphabetically sorted, semicolon-separated list of
	 *            lowercase request header names. The request headers in the
	 *            list are the same headers that you included in the
	 *            CanonicalHeaders string. For example, for example, if
	 *            CanonicalHeaders list include the following:
	 *            host, x-auth-request-timestamp,
	 *            the value of SignedHeaders would be as follows:
	 *            host;x-auth-request-timestamp
	 *            
	 * @return the Canonical Request base on inputs.
	 */
	public String getCanonicalRequest(String httpMethod, String canonicalURI,
			String canonicalQueryString, String canonicalHeaders,
			String signedHeaders, String hashedPayload);
	
	/**
	 * 
	 * @param signingKey the key use to calculate HMAC-SHA256 hash.
	 * @param canonicalRequest the Canonical Request to be compute.
	 * @return the computed Signature.
	 */
	public String calculateSignature(String signingKey, String canonicalRequest);

	/**
	 * URI encode every byte. Uri-Encode() must enforce the following rules:
	 * 
	 * URI encode every byte except the unreserved characters: 'A'-'Z', 'a'-'z',
	 * '0'-'9', '-', '.', '_', and '~'. The space character is a reserved
	 * character and must be encoded as "%20" (and not as "+"). Each Uri-encoded
	 * byte is formed by a '%' and the two-digit hexadecimal value of the byte.
	 * Letters in the hexadecimal value must be uppercase, for example "%1A".
	 * Encode the forward slash character, '/', everywhere except in the object
	 * key name. For example, if the object key name is photos/Jan/sample.jpg,
	 * the forward slash in the key name is not encoded.
	 * 
	 * @param input
	 *            CharSequence to be encode.
	 * @param encodeSlash
	 *            encode slash.
	 * @return the encoded String.
	 */
	public String uriEncode(CharSequence input, boolean encodeSlash);
	
	/**
	 * Validate x-auth-signed-headers header.
	 * @param signedHeaders the value of x-auth-signed-headers header.
	 * @throws ApiSignatureException if the value is not valid.
	 */
	public void validateSignedHeaders(String signedHeaders);
	
	/**
	 * Validate x-auth-request-timestamp header.
	 * @param requestTimestamp the value of x-auth-request-timestamp header.
	 * @throws ApiSignatureException if the value is not valid.
	 */
	
	/**
	 * validate header by signedHeader
	 * check if the headers contains all the signedHeader
	 * @param multiValueQueryMap
	 * @param headerFilter
	 * @throws ApiSignatureException
	 */
	public void validateHeaders(MultivaluedMap<String, String> multiValueQueryMap,List<String> headerFilter);
	
	public void validateRequestTimestamp(String requestTimestamp);
	
	/**
	 * 
	 * @param authSignature the value of x-auth-signature header.
	 * @param calculatedSignature the calculated value being compare to authSignature.
	 * @throws ApiSignatureException if calculatedSignature and authSignature not match.
	 */
	public void validateSignature(String authSignature, String calculatedSignature);
		
	/**
	 * 
	 * @param entityStream the payload stream
	 * @return hashed payload string (SHA-256 in Hex format)
	 * @throws ApiSignatureException
	 */
	public String getHashedPayload(String entityStream);
	
	/**
	 * canonicalize the query parameters map and return a canonicalized String
	 * @param queryParams
	 * @return
	 */
	public String canonicalizeQueryParameter(MultivaluedMap<String, String> multiValueQueryMap);
	
	/**
	 * filter the header parameter by headerFilter,
	 * canonicalize the filtered header parameters map and return a canonicalized String 
	 * @param queryParams
	 * @return
	 */
	public String canonicalizeHeaderParameter(MultivaluedMap<String, String> multiValueQueryMap, List<String> headerFilter);
}
