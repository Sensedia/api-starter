package com.sensedia.api.infra;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {
	private static final ObjectMapper parser = new ObjectMapper();

	private static final String BEGINNING_INPUT_BOUNDARY_DELIMITER = "\\A";

	public static String toString(Object object) {
		try {
			return parser.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T toObject(InputStream input, Class<T> type) {
		Scanner scanner = new Scanner(input).useDelimiter(BEGINNING_INPUT_BOUNDARY_DELIMITER);
		String json = scanner.hasNext() ? scanner.next() : "";
		return toObject(json, type);
	}

	public static <T> T toObject(String json, Class<T> type) {
		try {
			return parser.readValue(json, type);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}