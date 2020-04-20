package de.bausdorf.simcacing.tt.web.model;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.format.Formatter;

public class LocalTimeFormatter implements Formatter<LocalTime> {

	public static final String TIME_PATTERN = "HH:mm";

	@Override
	public LocalTime parse(String s, Locale locale) throws ParseException {
		if( s == null ) {
			return LocalTime.MIN;
		}
		return LocalTime.parse(s, DateTimeFormatter.ofPattern(TIME_PATTERN));
	}

	@Override
	public String print(LocalTime localTime, Locale locale) {
		if( localTime == null ) {
			return LocalTime.MIN.format(DateTimeFormatter.ofPattern(TIME_PATTERN));
		}
		return localTime.format(DateTimeFormatter.ofPattern(TIME_PATTERN));
	}
}
