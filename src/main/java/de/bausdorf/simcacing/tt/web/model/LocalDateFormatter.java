package de.bausdorf.simcacing.tt.web.model;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;

import org.springframework.format.Formatter;

public class LocalDateFormatter implements Formatter<LocalDate> {


	@Override
	public LocalDate parse(String s, Locale locale) throws ParseException {
		if( s == null ) {
			return LocalDate.MIN;
		}
		return LocalDate.parse(s);
	}

	@Override
	public String print(LocalDate localDate, Locale locale) {
		if( localDate == null ) {
			return LocalDate.MIN.toString();
		}
		return localDate.toString();
	}
}
