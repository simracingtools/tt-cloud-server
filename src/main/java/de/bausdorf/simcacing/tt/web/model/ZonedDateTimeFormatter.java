package de.bausdorf.simcacing.tt.web.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.springframework.format.Formatter;

public class ZonedDateTimeFormatter implements Formatter<ZonedDateTime> {

	@Override
	public ZonedDateTime parse(String s, Locale locale) {
		if (s == null) {
			return ZonedDateTime.of(LocalDateTime.MIN, ZoneId.systemDefault());
		}
		return ZonedDateTime.parse(s);
	}

	@Override
	public String print(ZonedDateTime zonedDateTime, Locale locale) {
		if (zonedDateTime == null) {
			return ZonedDateTime.of(LocalDateTime.MIN, ZoneId.systemDefault()).toString();
		}
		return zonedDateTime.toString();
	}
}
