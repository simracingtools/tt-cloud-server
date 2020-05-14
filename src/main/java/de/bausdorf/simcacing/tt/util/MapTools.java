package de.bausdorf.simcacing.tt.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapTools {

	public static final String EXCEPTION_LOG_PATTERN = "{} - {}: {}";

	private MapTools() {
		super();
	}

	public static String stringFromMap(String key, Map<String, Object> data) {
		try {
			return (String)data.get(key);
		} catch( Exception e ) {
			log.warn(EXCEPTION_LOG_PATTERN, key, e.getMessage(), data.get(key));
		}
		return "";
	}

	public static double doubleFromMap(String key, Map<String, Object> data) {
		try {
			return (Double)data.get(key);
		} catch( Exception e ) {
			log.warn(EXCEPTION_LOG_PATTERN, key, e.getMessage(), data.get(key));
		}
		return 0.0D;
	}

	public static int intFromMap(String key, Map<String, Object> data) {
		try {
			return ((Long) data.get(key)).intValue();
		} catch( Exception e ) {
			log.warn(EXCEPTION_LOG_PATTERN, key, e.getMessage(), data.get(key));
		}
		return 0;
	}

	public static long longFromMap(String key, Map<String, Object> data) {
		try {
			return (Long) data.get(key);
		} catch( Exception e ) {
			log.warn(EXCEPTION_LOG_PATTERN, key, e.getMessage(), data.get(key));
		}
		return 0L;
	}

	public static boolean booleanFromMap(String key, Map<String, Object> data, boolean defaultValue) {
		try {
			return (Boolean) data.get(key);
		} catch( Exception e ) {
			log.warn(EXCEPTION_LOG_PATTERN, key, e.getMessage(), data.get(key));
		}
		return defaultValue;
	}

	public static Duration durationFromMap(String key, Map<String, Object> data) {
		try {
			return Duration.parse((String)data.get(key));
		} catch( Exception e ) {
			log.warn(EXCEPTION_LOG_PATTERN, key, e.getMessage(), data.get(key));
		}
		return Duration.ZERO;
	}

	public static LocalTime timeFromMap(String key, Map<String, Object> data) {
		try {
			return LocalTime.parse((String)data.get(key));
		} catch( Exception e ) {
			log.warn(EXCEPTION_LOG_PATTERN, key, e.getMessage(), data.get(key));
		}
		return LocalTime.MIN;
	}

	public static LocalDateTime dateTimeFromMap(String key, Map<String, Object> data) {
		try {
			return LocalDateTime.parse((String)data.get(key));
		} catch( Exception e ) {
			log.warn(EXCEPTION_LOG_PATTERN, key, e.getMessage(), data.get(key));
		}
		return LocalDateTime.MIN;
	}

	public static List<String> stringListFromMap(String key, Map<String, Object> data) {
		try {
			return (List<String>)data.get(key);
		} catch( Exception e ) {
			log.warn(EXCEPTION_LOG_PATTERN, key, e.getMessage(), data.get(key));
		}
		return new ArrayList<>();
	}

}
