package de.bausdorf.simcacing.tt.web.model;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

public class IntegerFormatter implements Formatter<Integer> {

	@Override
	public Integer parse(String s, Locale locale) throws ParseException {
		if( s == null ) {
			return 0;
		}
		return Integer.parseInt(s);
	}

	@Override
	public String print(Integer integer, Locale locale) {
		if( integer == null ) {
			return "";
		}
		return integer.toString();
	}
}
