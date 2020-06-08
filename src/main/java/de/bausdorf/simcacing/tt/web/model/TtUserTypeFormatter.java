package de.bausdorf.simcacing.tt.web.model;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import de.bausdorf.simcacing.tt.web.security.TtUserType;

public class TtUserTypeFormatter implements Formatter<TtUserType> {

	@Override
	public TtUserType parse(String s, Locale locale) throws ParseException {
		if (s != null) {
			return TtUserType.valueOf(s);
		}
		return TtUserType.TT_NEW;
	}

	@Override
	public String print(TtUserType ttUserType, Locale locale) {
		if (ttUserType != null) {
			return ttUserType.name();
		}
		return "TT_NEW";
	}
}
