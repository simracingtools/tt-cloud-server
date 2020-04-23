package de.bausdorf.simcacing.tt.web.model;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.format.Formatter;

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

	@Override
	public LocalDateTime parse(String s, Locale locale) throws ParseException {
		if (s == null) {
			return LocalDateTime.MIN;
		}
		return LocalDateTime.parse(s);
	}

	@Override
	public String print(LocalDateTime localDateTime, Locale locale) {
		if (localDateTime == null) {
			return LocalDateTime.MIN.toString();
		}
		return localDateTime.toString();
	}
}
