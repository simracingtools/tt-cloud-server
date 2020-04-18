package de.bausdorf.simcacing.tt.web.model;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.format.Formatter;

import de.bausdorf.simcacing.tt.util.TimeTools;

public class LocalTimeFormatter implements Formatter<LocalTime> {

	@Override
	public LocalTime parse(String s, Locale locale) throws ParseException {
		if( s == null ) {
			return LocalTime.MIN;
		}
		return TimeTools.timeFromString(s);
	}

	@Override
	public String print(LocalTime localTime, Locale locale) {
		if( localTime == null ) {
			return "";
		}
		return localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}
}
