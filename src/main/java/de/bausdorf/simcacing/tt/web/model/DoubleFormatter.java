package de.bausdorf.simcacing.tt.web.model;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

public class DoubleFormatter implements Formatter<Double> {

	@Override
	public Double parse(String s, Locale locale) throws ParseException {
		if( s == null ) {
			return 0.0D;
		}
		return Double.parseDouble(s.replaceAll(",", "."));
	}

	@Override
	public String print(Double aDouble, Locale locale) {
		if( aDouble == null ) {
			return "";
		}
		return String.format("%.3f", aDouble);
	}
}
