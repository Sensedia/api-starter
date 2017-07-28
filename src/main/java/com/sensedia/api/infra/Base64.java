package com.sensedia.api.infra;

public class Base64 {

	public static String encode(String data) {
		return java.util.Base64.getEncoder().encodeToString(data.getBytes());
	}

	public static String decode(String data) {
		byte[] decodedData = java.util.Base64.getDecoder().decode(data);
		return new String(decodedData);
	}
}