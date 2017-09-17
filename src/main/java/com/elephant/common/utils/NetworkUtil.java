package com.elephant.common.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import com.google.common.primitives.UnsignedInts;
import com.google.common.primitives.UnsignedLong;

public class NetworkUtil {

	private static enum IPVersion{V4,V6};
	
    private final static String[] HEADERS_TO_TRY = {
            "X-FORWARDED-FOR", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA",
            "REMOTE_ADDR" };
    
	/**
	 * <p>
	 * Same as the MySQL function INET_ATON() which convert an IP address
	 * to an unsigned numeric value
	 * 
	 * @param ipAddress
	 * @return
	 * @see https ://dev.mysql.com/doc/refman/5.0/en/miscellaneous-functions.html#function_inet-aton
	 */
	public static Long inetAton(InetAddress ipAddress) {
		return UnsignedInts.toLong(InetAddresses.coerceToInteger(ipAddress));
	}
	
	/**
	 * Same as MySQL function INET_NTOA() which convert an unsigned numeric value 
	 * to an IP address
	 * @return
	 * @see https://dev.mysql.com/doc/refman/5.0/en/miscellaneous-functions.html#function_inet-ntoa
	 */
	public static InetAddress inetNtoa(Long ipAddress){
		return InetAddresses.fromInteger(UnsignedLong.valueOf(ipAddress).intValue());
	}
	
	/**
	 * Only encode the username and password using basic access authentication (i.e username:password and then base64 encode it)
	 * @see  https://en.wikipedia.org/wiki/Basic_access_authentication
	 */
	public static String base64Encode(String username, String pwd) {
		return Base64.getEncoder().encodeToString((MoreObjects.firstNonNull(username, "") + ":" + MoreObjects.firstNonNull(pwd, "")).getBytes());
	}
	
	/**
	 * 
	 * @return 0th index is username . 1st index is password
	 */
	public static String[] base64Decode(String encodedUsernamePassword){
		byte[] decodedByte = Base64.getDecoder().decode(encodedUsernamePassword);
		return new String(decodedByte, Charset.forName("utf8")).split(":");
	}
	
	/**
	 * Try to get the IP of the request
	 */
	public static String getIpAddress(HttpServletRequest request) {
		for (String header : HEADERS_TO_TRY) {
			String ip = request.getHeader(header);
			if (!Strings.isNullOrEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}
	
	public static String getAvailableIpV4(String byteStart, String byteEnd, String ipMap) {
		return getAvailableIp(byteStart, byteEnd, ipMap,IPVersion.V4);
	}

	public static String getAvailableIpV6(String byteStart, String byteEnd, String ipMap) {
		return getAvailableIp(byteStart, byteEnd, ipMap,IPVersion.V6);
	}
	
	private static String getAvailableIp(String byteStart, String byteEnd, String ipMap, IPVersion ipVersion) {
		BigInteger ipStartB = new BigInteger(byteStart, 16);
		BigInteger ipEndB = new BigInteger(byteEnd, 16);

		int range = ipEndB.subtract(ipStartB).intValue() + 1;
		String[] mapArray = ipMap.split(",");
		byte[] bMap = new byte[mapArray.length];
		for (int i = 0; i < mapArray.length; i++) {
			bMap[i] = (byte) Integer.parseInt(mapArray[i], 16);
		}

		int startByte = 0;
		int mask = 1 << 7;
//		int ipCount = 0;
		for (int i = 0; i < range; i++) {
			if (mask == (mask & bMap[startByte])) {
				if(ipVersion==IPVersion.V4){
					return getIpFromHexIpV4(ipStartB.add(BigInteger.valueOf(i)).toString(16));
				}else{
					return getIpFromHexIpV6(ipStartB.add(BigInteger.valueOf(i)).toString(16));
				}
				
			}
			mask = mask >> 1;
			if (mask == 0) {
				mask = 1 << 7;
				startByte++;
			}
		}
		return null;
	}
	
	public static String getIpFromHexIpV4(String ipHex) {
		String ipStr = "";
		String padding = "00000000";
		ipHex = padding.substring(0, 8 - ipHex.length()) + ipHex;
		ipStr = "" + Long.parseLong(ipHex.substring(0, 2), 16) + "." + Long.parseLong(ipHex.substring(2, 4), 16)
				+ "." + Long.parseLong(ipHex.substring(4, 6), 16) + "." + Long.parseLong(ipHex.substring(6, 8), 16);
		return ipStr;
	}
	
	public static String getIpFromHexIpV6(String ipHex) {
		String ipStr = "";
		String padding = "00000000000000000000000000000000";
		ipHex = padding.substring(0, 32 - ipHex.length()) + ipHex;
		ipStr = ipHex.substring(0, 4) + ":" + ipHex.substring(4, 8) + ":" +
				ipHex.substring(8, 12) + ":" + ipHex.substring(12, 16) + ":" +
				ipHex.substring(16, 20) + ":" + ipHex.substring(20, 24) + ":" +
				ipHex.substring(24, 28) + ":" + ipHex.substring(28, 32);
		return ipStr;
	}

	// Convert compressed IPv6 address to long format
	public static String getExpandedAddressForIPV6(String compressedAddress) {
		try {
			String expandedAddress = "";
			String temp = "";
			String[] addressParts = compressedAddress.split("\\:", -1);
			for (String addressPart : addressParts) {
				if (!addressPart.isEmpty()) {
					temp = ("0000" + addressPart).substring(addressPart.length());
				} else {
					temp = "";
				}
				expandedAddress += temp + ":";
			}
			expandedAddress = expandedAddress.substring(0, expandedAddress.length() - 1);
			if (expandedAddress.substring(0, 1).equalsIgnoreCase(":")) {
				temp = "0000:";
			} else {
				temp = ":";
			}

			for (int i = 0; i <= 8 - addressParts.length; i++) {
				temp += "0000:";
			}
			expandedAddress = expandedAddress.replace("::", temp);

			if (expandedAddress.endsWith(":")) {
				expandedAddress = expandedAddress.concat("0000");
			}

			return expandedAddress;
		} catch (final Exception ex) {
			return null;
		}
	}

	public static String getCompressedAddress(String expandedAddress) {
		try {
			expandedAddress = Inet6Address.getByName(expandedAddress).getHostAddress();
			return expandedAddress.replaceFirst("(^|:)(0+(:|$)){2,8}", "::").toUpperCase();
		} catch (final Exception ex) {
			return null;
		}
	}

	 /**
     * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in
     * the 200-399 range.
     * 
     * @param url The HTTP URL to be pinged.
     * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that
     *            the total timeout is effectively two times the given timeout.
     * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the
     *         given timeout, otherwise <code>false</code>.
     * 
     * @see http://stackoverflow.com/questions/3584210/preferred-java-way-to-ping-a-http-url-for-availability/3584332#3584332
     */
    public static boolean ping(String url, int timeout) {
        url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }
	

	/**
	 * addr is subnet address and addr1 is ip address. Function will return
	 * true, if addr1 is within addr(subnet)
	 * 
	 * @param addr
	 * @param addr1
	 * @return
	 */
	public static boolean netMatch(String addr, String addr1) {

		String[] parts = addr.split("/");
		String ip = parts[0];
		int prefix;

		if (parts.length < 2) {
			prefix = 0;
		} else {
			prefix = Integer.parseInt(parts[1]);
		}

		Inet4Address a = null;
		Inet4Address a1 = null;
		try {
			a = (Inet4Address) InetAddress.getByName(ip);
			a1 = (Inet4Address) InetAddress.getByName(addr1);
		} catch (UnknownHostException e) {
		}

		byte[] b = a.getAddress();
		int ipInt = ((b[0] & 0xFF) << 24) |
				((b[1] & 0xFF) << 16) |
				((b[2] & 0xFF) << 8) |
				((b[3] & 0xFF) << 0);

		byte[] b1 = a1.getAddress();
		int ipInt1 = ((b1[0] & 0xFF) << 24) |
				((b1[1] & 0xFF) << 16) |
				((b1[2] & 0xFF) << 8) |
				((b1[3] & 0xFF) << 0);

		int mask = ~((1 << (32 - prefix)) - 1);

		if ((ipInt & mask) == (ipInt1 & mask)) {
			return true;
		}
		else {
			return false;
		}
	}

	
	public static String convertInetAddressToHex(InetAddress ip) {
		StringBuilder sb = new StringBuilder();
		for (byte b : ip.getAddress()) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}
	
	public static int getNumOfIpInRangeInclusive(InetAddress ipStart, InetAddress ipEnd) {
		BigInteger biStart = new BigInteger(convertInetAddressToHex(ipStart), 16);
		BigInteger biEnd = new BigInteger(convertInetAddressToHex(ipEnd), 16);
		return biEnd.subtract(biStart).intValue() + 1;
	}
	
	public static boolean isIpv4WithinRange(String byteStartHex, String byteEndHex, String ipv4Hex) {
		BigInteger ipStartB = new BigInteger(byteStartHex, 16);
		BigInteger ipB = new BigInteger(ipv4Hex, 16);
		int startDiff = ipB.subtract(ipStartB).intValue();
		if (startDiff < 0) {
			return false;
		}
		BigInteger ipEndB = new BigInteger(byteEndHex, 16);
		int endDiff = ipEndB.subtract(ipB).intValue();
		if (endDiff < 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * Also work for IPv6
	 * @param ipStart
	 * @param ipEnd
	 * @param ipToCheck
	 * @return true if within range, false otherwise
	 */
	public static boolean isIpWithinRangeInclusive(InetAddress ipStart, InetAddress ipEnd, InetAddress ipToCheck) {
		byte[] bStart = ipStart.getAddress();
		byte[] bEnd = ipEnd.getAddress();
		byte[] vIp = ipToCheck.getAddress();
		for (int i=0; i<bStart.length; i++) {
			if (Byte.toUnsignedInt(bStart[i]) > Byte.toUnsignedInt(vIp[i]))
				return false;
		}
		for (int i=0; i<bEnd.length; i++) {
			if (Byte.toUnsignedInt(bEnd[i]) < Byte.toUnsignedInt(vIp[i]))
				return false;
		}
		return true;
	}

}
